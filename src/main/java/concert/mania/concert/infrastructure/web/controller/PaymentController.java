package concert.mania.concert.infrastructure.web.controller;

import concert.mania.concert.application.port.in.PaymentUseCase;
import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.infrastructure.web.dto.request.PaymentRequest;
import concert.mania.concert.infrastructure.web.dto.response.PaymentResponse;
import concert.mania.concert.infrastructure.web.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import concert.mania.concert.infrastructure.web.docs.payment.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 결제 컨트롤러
 * 결제 관련 API를 제공
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    /**
     * 결제 요청
     */
    @PostMapping
    @RequestPaymentApiDoc
    public ResponseEntity<SuccessResponse> requestPayment(@RequestBody PaymentRequest request) {
        log.info("결제 요청 - 예약 ID: {}, 콘서트 ID: {}, 좌석 ID: {}, 좌석 잠금 ID: {}, 금액: {}, 결제 방식: {}, 사용자 ID: {}", 
                request.reservationId(), request.concertId(), request.seatId(), request.seatLockId(),
                request.amount(), request.method(), request.userId());

        Payment payment = paymentUseCase.requestPayment(
                request.reservationId(),
                request.concertId(),
                request.seatId(),
                request.seatLockId(),
                request.amount(),
                request.method(),
                request.userId()
        );

        PaymentResponse response = PaymentResponse.from(payment);
        return ResponseEntity.ok(SuccessResponse.of("결제 요청이 접수되었습니다.", HttpStatus.OK.value(), response));
    }

    /**
     * 결제 상태 확인
     */
    @GetMapping("/{paymentId}")
    @CheckPaymentStatusApiDoc
    public ResponseEntity<SuccessResponse> checkPaymentStatus(
            @Parameter(description = "결제 ID", required = true) @PathVariable Long paymentId) {

        log.info("결제 상태 확인 - 결제 ID: {}", paymentId);

        Payment payment = paymentUseCase.checkPaymentStatus(paymentId);
        PaymentResponse response = PaymentResponse.from(payment);

        return ResponseEntity.ok(SuccessResponse.of("결제 상태 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 결제 취소
     */
    @PostMapping("/{paymentId}/cancel")
    @CancelPaymentApiDoc
    public ResponseEntity<SuccessResponse> cancelPayment(
            @Parameter(description = "결제 ID", required = true) @PathVariable Long paymentId,
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId) {

        log.info("결제 취소 요청 - 결제 ID: {}, 사용자 ID: {}", paymentId, userId);

        Payment payment = paymentUseCase.cancelPayment(paymentId, userId);
        PaymentResponse response = PaymentResponse.from(payment);

        return ResponseEntity.ok(SuccessResponse.of("결제 취소 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 외부 결제 ID로 결제 정보 조회
     */
    @GetMapping("/external/{externalPaymentId}")
    @GetPaymentByExternalIdApiDoc
    public ResponseEntity<SuccessResponse> getPaymentByExternalId(
            @Parameter(description = "외부 결제 시스템 ID", required = true) @PathVariable String externalPaymentId) {

        log.info("외부 결제 ID로 결제 정보 조회 - 외부 결제 ID: {}", externalPaymentId);

        Payment payment = paymentUseCase.getPaymentByExternalId(externalPaymentId);
        PaymentResponse response = PaymentResponse.from(payment);

        return ResponseEntity.ok(SuccessResponse.of("결제 정보 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 결제 완료 콜백 (PG사에서 호출)
     */
    @PostMapping("/callback/complete")
    @CompletePaymentCallbackApiDoc
    public ResponseEntity<SuccessResponse> completePaymentCallback(
            @Parameter(description = "외부 결제 시스템 ID", required = true) @RequestParam String externalPaymentId) {

        log.info("결제 완료 콜백 - 외부 결제 ID: {}", externalPaymentId);

        // 외부 결제 ID로 결제 정보 조회
        Payment payment = paymentUseCase.getPaymentByExternalId(externalPaymentId);

        // 결제 완료 처리
        Payment completedPayment = paymentUseCase.completePayment(payment.getId());
        PaymentResponse response = PaymentResponse.from(completedPayment);

        return ResponseEntity.ok(SuccessResponse.of("결제 완료 처리 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 결제 실패 콜백 (PG사에서 호출)
     */
    @PostMapping("/callback/fail")
    @FailPaymentCallbackApiDoc
    public ResponseEntity<SuccessResponse> failPaymentCallback(
            @Parameter(description = "외부 결제 시스템 ID", required = true) @RequestParam String externalPaymentId) {

        log.info("결제 실패 콜백 - 외부 결제 ID: {}", externalPaymentId);

        // 외부 결제 ID로 결제 정보 조회
        Payment payment = paymentUseCase.getPaymentByExternalId(externalPaymentId);

        // 결제 실패 처리
        Payment failedPayment = paymentUseCase.handlePaymentFailure(payment.getId());
        PaymentResponse response = PaymentResponse.from(failedPayment);

        return ResponseEntity.ok(SuccessResponse.of("결제 실패 처리 성공", HttpStatus.OK.value(), response));
    }
}
