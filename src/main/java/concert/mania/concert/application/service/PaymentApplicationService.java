package concert.mania.concert.application.service;

import concert.mania.concert.application.port.in.PaymentUseCase;
import concert.mania.concert.application.port.in.SeatUseCase;
import concert.mania.concert.application.port.out.command.PaymentCommandPort;
import concert.mania.concert.application.port.out.payment.PaymentGatewayPort;
import concert.mania.concert.application.port.out.query.PaymentQueryPort;
import concert.mania.concert.application.port.out.query.ReservationQueryPort;
import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.ReservationDetail;
import concert.mania.concert.domain.model.type.PaymentMethod;
import concert.mania.concert.domain.model.type.PaymentStatus;
import concert.mania.concert.infrastructure.messaging.producer.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 결제 서비스
 * 결제 처리 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApplicationService implements PaymentUseCase {

    private final PaymentCommandPort paymentCommandPort;
    private final PaymentQueryPort paymentQueryPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final ReservationQueryPort reservationQueryPort;
    private final SeatUseCase seatUseCase;
    private final PaymentProducer paymentProducer;

    /**
     * 결제 요청
     * @param reservationId 예약 ID
     * @param concertId 콘서트 ID
     * @param seatId 좌석 ID
     * @param seatLockId 좌석 잠금 ID
     * @param amount 결제 금액
     * @param method 결제 방식
     * @param userId 사용자 ID
     * @return 결제 정보
     */
    @Override
    @Transactional
    public Payment requestPayment(Long reservationId, Long concertId, Long seatId, Long seatLockId, 
                                 BigDecimal amount, PaymentMethod method, Long userId) {
        log.info("결제 요청 - 예약 ID: {}, 콘서트 ID: {}, 좌석 ID: {}, 좌석 잠금 ID: {}, 금액: {}, 결제 방식: {}, 사용자 ID: {}", 
                reservationId, concertId, seatId, seatLockId, amount, method, userId);

        // 1. 예약 정보 조회
        Reservation reservation = reservationQueryPort.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 2. 사용자 검증
        if (!reservation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("예약한 사용자만 결제할 수 있습니다.");
        }

        // 3. 금액 검증
        if (amount.compareTo(reservation.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("결제 금액이 예약 금액과 일치하지 않습니다.");
        }

        // 4. 결제 정보 생성
        Payment payment = Payment.builder()
                .amount(amount)
                .method(method)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .reservation(reservation)
                .build();

        // 5. 결제 정보 저장
        Payment savedPayment = paymentCommandPort.save(payment);

        // 6. 외부 결제 시스템에 결제 요청 (비동기로 처리)
        processPaymentWithRetry(savedPayment.getId(), concertId, seatId, seatLockId);

        return savedPayment;
    }

    /**
     * 결제 상태 확인
     * @param paymentId 결제 ID
     * @return 결제 정보
     */
    @Override
    @Transactional(readOnly = true)
    public Payment checkPaymentStatus(Long paymentId) {
        log.info("결제 상태 확인 - 결제 ID: {}", paymentId);

        Payment payment = paymentQueryPort.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        // 결제가 진행 중인 경우 외부 결제 시스템에서 상태 확인
        if (payment.isPending() && payment.getExternalPaymentId() != null) {
            String paymentStatus = paymentGatewayPort.getPaymentStatus(payment.getExternalPaymentId());

            // 결제 상세 정보 업데이트
            paymentCommandPort.updatePaymentDetails(paymentId, paymentStatus);

            // 결제 상태 업데이트
            if (paymentStatus.contains("\"status\":\"COMPLETED\"")) {
                payment = completePayment(paymentId);
            } else if (paymentStatus.contains("\"status\":\"FAILED\"")) {
                payment = handlePaymentFailure(paymentId);
            }
        }

        return payment;
    }

    /**
     * 결제 취소
     * @param paymentId 결제 ID
     * @param userId 사용자 ID
     * @return 취소된 결제 정보
     */
    @Override
    @Transactional
    public Payment cancelPayment(Long paymentId, Long userId) {
        log.info("결제 취소 요청 - 결제 ID: {}, 사용자 ID: {}", paymentId, userId);

        // 1. 결제 정보 조회
        Payment payment = paymentQueryPort.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        // 2. 사용자 검증
        if (!payment.getReservation().getUserId().equals(userId)) {
            throw new IllegalArgumentException("결제한 사용자만 취소할 수 있습니다.");
        }

        // 3. 외부 결제 시스템에 취소 요청
        boolean cancelSuccess = paymentGatewayPort.cancelPayment(payment);
        if (!cancelSuccess) {
            throw new IllegalStateException("결제 취소에 실패했습니다.");
        }

        // 4. 결제 상태 변경
        payment.cancel();

        // 5. 결제 정보 저장
        Payment canceledPayment = paymentCommandPort.save(payment);

        // 6. 좌석 상태 초기화
        releaseSeats(payment.getReservation());

        return canceledPayment;
    }

    /**
     * 결제 실패 처리
     * @param paymentId 결제 ID
     * @return 실패 처리된 결제 정보
     */
    @Override
    @Transactional
    public Payment handlePaymentFailure(Long paymentId) {
        log.info("결제 실패 처리 - 결제 ID: {}", paymentId);

        // 1. 결제 정보 조회
        Payment payment = paymentQueryPort.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        // 2. 결제 상태 변경
        payment.fail();

        // 3. 결제 정보 저장
        Payment failedPayment = paymentCommandPort.save(payment);

        // 4. RabbitMQ를 통해 결제 실패 메시지 전송
        // 좌석 잠금 해제는 PaymentConsumer에서 처리
        paymentProducer.sendFailureMessage(paymentId);
        log.info("결제 실패 메시지 전송 - 결제 ID: {}", paymentId);

        return failedPayment;
    }

    /**
     * 결제 완료 처리
     * @param paymentId 결제 ID
     * @return 완료 처리된 결제 정보
     */
    @Override
    @Transactional
    public Payment completePayment(Long paymentId) {
        log.info("결제 완료 처리 - 결제 ID: {}", paymentId);

        // 1. 결제 정보 조회
        Payment payment = paymentQueryPort.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        // 2. 결제 상태 변경
        payment.complete();

        // 3. 결제 정보 저장
        Payment completedPayment = paymentCommandPort.save(payment);

        // 4. RabbitMQ를 통해 결제 성공 메시지 전송
        paymentProducer.sendSuccessMessage(paymentId);
        log.info("결제 성공 메시지 전송 - 결제 ID: {}", paymentId);

        return completedPayment;
    }

    /**
     * 외부 결제 ID로 결제 정보 조회
     * @param externalPaymentId 외부 결제 시스템 ID
     * @return 결제 정보
     */
    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByExternalId(String externalPaymentId) {
        log.info("외부 결제 ID로 결제 정보 조회 - 외부 결제 ID: {}", externalPaymentId);

        return paymentQueryPort.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));
    }

    /**
     * 비동기로 결제 상태 확인 및 처리
     * @param paymentId 결제 ID
     */
    protected void processPaymentAsync(Long paymentId) {
        log.info("결제 처리 메시지 전송 - 결제 ID: {}", paymentId);

        // RabbitMQ를 통해 결제 처리 메시지 전송
        paymentProducer.sendProcessMessage(paymentId);
    }

    /**
     * 결제 처리 (최대 5회 재시도)
     * @param paymentId 결제 ID
     * @param concertId 콘서트 ID
     * @param seatId 좌석 ID
     * @param seatLockId 좌석 잠금 ID
     */
    @Async
    protected void processPaymentWithRetry(Long paymentId, Long concertId, Long seatId, Long seatLockId) {
        log.info("결제 처리 시작 - 결제 ID: {}, 콘서트 ID: {}, 좌석 ID: {}, 좌석 잠금 ID: {}", 
                paymentId, concertId, seatId, seatLockId);

        Payment payment = paymentQueryPort.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        // 외부 결제 ID 요청
        String externalPaymentId = paymentGatewayPort.requestPayment(payment);

        // 외부 결제 ID 업데이트
        payment = paymentCommandPort.updateExternalPaymentId(paymentId, externalPaymentId);

        // 최대 5회 재시도
        int maxRetries = 5;
        int retryCount = 0;
        boolean paymentSuccess = false;

        while (retryCount < maxRetries && !paymentSuccess) {
            try {
                log.info("결제 시도 {} - 결제 ID: {}", retryCount + 1, paymentId);

                // 결제 상태 확인
                String paymentStatus = paymentGatewayPort.getPaymentStatus(externalPaymentId);

                // 결제 상세 정보 업데이트
                paymentCommandPort.updatePaymentDetails(paymentId, paymentStatus);

                // 결제 상태 확인
                if (paymentStatus.contains("\"status\":\"COMPLETED\"")) {
                    // 결제 성공
                    completePayment(paymentId);
                    paymentSuccess = true;
                    log.info("결제 성공 - 결제 ID: {}, 시도: {}", paymentId, retryCount + 1);

                    // 상세 성공 정보를 포함한 메시지 전송
                    sendDetailedSuccessMessage(paymentId, concertId, seatId, seatLockId);
                } else if (paymentStatus.contains("\"status\":\"FAILED\"")) {
                    // 결제 실패, 재시도
                    log.warn("결제 실패, 재시도 - 결제 ID: {}, 시도: {}", paymentId, retryCount + 1);
                    retryCount++;

                    if (retryCount < maxRetries) {
                        // 잠시 대기 후 재시도
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        // 새로운 외부 결제 ID로 다시 시도
                        externalPaymentId = paymentGatewayPort.requestPayment(payment);
                        payment = paymentCommandPort.updateExternalPaymentId(paymentId, externalPaymentId);
                    }
                } else {
                    // 진행 중인 상태, 잠시 대기 후 다시 확인
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                log.error("결제 처리 중 오류 발생 - 결제 ID: {}, 시도: {}, 오류: {}", 
                        paymentId, retryCount + 1, e.getMessage());
                retryCount++;

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // 최대 재시도 횟수를 초과하여 실패한 경우
        if (!paymentSuccess) {
            log.error("최대 재시도 횟수 초과 - 결제 ID: {}, 시도: {}", paymentId, retryCount);

            // 결제 실패 처리
            handlePaymentFailure(paymentId);

            // RabbitMQ를 통해 결제 실패 메시지 전송
            paymentProducer.sendFailureMessage(paymentId);

            // 추가 정보를 포함한 실패 메시지 전송
            sendDetailedFailureMessage(paymentId, concertId, seatId, seatLockId);
        }
    }

    /**
     * 상세 실패 정보를 포함한 메시지 전송
     * @param paymentId 결제 ID
     * @param concertId 콘서트 ID
     * @param seatId 좌석 ID
     * @param seatLockId 좌석 잠금 ID
     */
    private void sendDetailedFailureMessage(Long paymentId, Long concertId, Long seatId, Long seatLockId) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> failureDetails = new java.util.HashMap<>();
            failureDetails.put("paymentId", paymentId);
            failureDetails.put("concertId", concertId);
            failureDetails.put("seatId", seatId);
            failureDetails.put("seatLockId", seatLockId);
            failureDetails.put("failedAt", LocalDateTime.now().toString());
            failureDetails.put("retryCount", 5);

            String failureMessage = objectMapper.writeValueAsString(failureDetails);

            // 상세 실패 정보를 포함한 메시지 전송
            paymentProducer.sendDetailedFailureMessage(failureMessage);

            log.info("상세 결제 실패 메시지 전송 완료 - 결제 ID: {}", paymentId);
        } catch (Exception e) {
            log.error("상세 결제 실패 메시지 전송 중 오류 발생 - 결제 ID: {}, 오류: {}", paymentId, e.getMessage());
        }
    }

    /**
     * 상세 성공 정보를 포함한 메시지 전송
     * @param paymentId 결제 ID
     * @param concertId 콘서트 ID
     * @param seatId 좌석 ID
     * @param seatLockId 좌석 잠금 ID
     */
    private void sendDetailedSuccessMessage(Long paymentId, Long concertId, Long seatId, Long seatLockId) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> successDetails = new java.util.HashMap<>();
            successDetails.put("paymentId", paymentId);
            successDetails.put("concertId", concertId);
            successDetails.put("seatId", seatId);
            successDetails.put("seatLockId", seatLockId);
            successDetails.put("succeededAt", LocalDateTime.now().toString());

            String successMessage = objectMapper.writeValueAsString(successDetails);

            // 상세 성공 정보를 포함한 메시지 전송
            paymentProducer.sendDetailedSuccessMessage(successMessage);

            log.info("상세 결제 성공 메시지 전송 완료 - 결제 ID: {}", paymentId);
        } catch (Exception e) {
            log.error("상세 결제 성공 메시지 전송 중 오류 발생 - 결제 ID: {}, 오류: {}", paymentId, e.getMessage());
        }
    }

    /**
     * 좌석 상태 초기화 (예약 취소 또는 결제 실패 시)
     * @param reservation 예약 정보
     */
    private void releaseSeats(Reservation reservation) {
        if (reservation == null) {
            return;
        }

        List<ReservationDetail> details = reservation.getReservationDetails();
        if (details == null || details.isEmpty()) {
            return;
        }

        log.info("좌석 상태 초기화 - 예약 ID: {}, 좌석 수: {}", reservation.getId(), details.size());

        // 각 좌석 상태 초기화
        for (ReservationDetail detail : details) {
            try {
                Long seatId = detail.getSeat().getId();
                seatUseCase.cancelSeatSelection(seatId, reservation.getUserId());
                log.info("좌석 상태 초기화 완료 - 좌석 ID: {}", seatId);
            } catch (Exception e) {
                log.error("좌석 상태 초기화 중 오류 발생 - 좌석: {}, 오류: {}", 
                        detail.getSeat().getSeatNumber(), e.getMessage());
            }
        }
    }
}
