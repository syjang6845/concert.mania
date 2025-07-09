package concert.mania.concert.infrastructure.messaging.consumer;

import concert.mania.concert.application.port.in.PaymentUseCase;
import concert.mania.concert.application.port.in.SeatUseCase;
import concert.mania.concert.application.port.out.query.ReservationQueryPort;
import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.ReservationDetail;
import concert.mania.concert.infrastructure.messaging.producer.PaymentProducer.PaymentMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static concert.mania.config.RabbitMQConfig.*;

/**
 * 결제 메시지 소비자
 * RabbitMQ로부터 결제 관련 메시지를 수신하고 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentUseCase paymentUseCase;
    private final SeatUseCase seatUseCase;
    private final ReservationQueryPort reservationQueryPort;

    /**
     * 결제 처리 메시지 처리
     * @param message 결제 메시지
     */
    @RabbitListener(queues = PAYMENT_PROCESS_QUEUE)
    public void handleProcessMessage(PaymentMessage message) {
        log.info("결제 처리 메시지 수신 - 결제 ID: {}", message.getPaymentId());

        try {
            Payment payment = paymentUseCase.checkPaymentStatus(message.getPaymentId());
            log.info("결제 상태 확인 완료 - 결제 ID: {}, 상태: {}", message.getPaymentId(), payment.getStatus());
        } catch (Exception e) {
            log.error("결제 처리 실패 - 결제 ID: {}, 오류: {}", message.getPaymentId(), e.getMessage());
        }
    }

    /**
     * 결제 성공 메시지 처리
     * @param message 결제 메시지
     */
    @RabbitListener(queues = PAYMENT_SUCCESS_QUEUE)
    @Transactional
    public void handleSuccessMessage(PaymentMessage message) {
        log.info("결제 성공 메시지 수신 - 결제 ID: {}", message.getPaymentId());

        try {
            // 결제 완료 처리
            Payment payment = paymentUseCase.completePayment(message.getPaymentId());

            // 예약 정보 조회
            Reservation reservation = payment.getReservation();
            if (reservation != null) {
                // 예약 완료 처리
                reservation.complete();

                // 좌석 확정 처리
                confirmSeats(reservation);

                log.info("결제 성공 처리 완료 - 결제 ID: {}, 예약 ID: {}", 
                        message.getPaymentId(), reservation.getId());
            }
        } catch (Exception e) {
            log.error("결제 성공 처리 실패 - 결제 ID: {}, 오류: {}", message.getPaymentId(), e.getMessage());
        }
    }

    /**
     * 결제 실패 메시지 처리
     * @param message 결제 메시지
     */
    @RabbitListener(queues = PAYMENT_FAILURE_QUEUE)
    public void handleFailureMessage(PaymentMessage message) {
        log.info("결제 실패 메시지 수신 - 결제 ID: {}", message.getPaymentId());

        try {
            // 결제 실패 처리
            Payment payment = paymentUseCase.handlePaymentFailure(message.getPaymentId());

            log.info("결제 실패 처리 완료 - 결제 ID: {}", message.getPaymentId());
        } catch (Exception e) {
            log.error("결제 실패 처리 오류 - 결제 ID: {}, 오류: {}", message.getPaymentId(), e.getMessage());
        }
    }

    /**
     * 좌석 확정 처리
     * @param reservation 예약 정보
     */
    private void confirmSeats(Reservation reservation) {
        if (reservation == null) {
            return;
        }

        List<ReservationDetail> details = reservation.getReservationDetails();
        if (details == null || details.isEmpty()) {
            return;
        }

        log.info("좌석 확정 처리 - 예약 ID: {}, 좌석 수: {}", reservation.getId(), details.size());

        // 각 좌석 확정 처리
        for (ReservationDetail detail : details) {
            try {
                Long seatId = detail.getSeat().getId();
                seatUseCase.confirmSeat(seatId, reservation.getUserId());
                log.info("좌석 확정 완료 - 좌석 ID: {}", seatId);
            } catch (Exception e) {
                log.error("좌석 확정 처리 중 오류 발생 - 좌석: {}, 오류: {}", 
                        detail.getSeat().getSeatNumber(), e.getMessage());
            }
        }
    }
}
