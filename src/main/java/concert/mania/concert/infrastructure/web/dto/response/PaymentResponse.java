package concert.mania.concert.infrastructure.web.dto.response;

import concert.mania.concert.domain.model.Payment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 결제 응답 DTO
 */
public record PaymentResponse(
    @Schema(description = "결제 ID", example = "1")
    Long id,

    @Schema(description = "외부 결제 시스템 ID", example = "PG_1234567890abcdef1234")
    String externalPaymentId,

    @Schema(description = "결제 금액", example = "50000")
    BigDecimal amount,

    @Schema(description = "결제 방식", example = "CREDIT_CARD")
    String method,

    @Schema(description = "결제 상태", example = "COMPLETED")
    String status,

    @Schema(description = "결제 완료 시간", example = "2023-01-01T12:00:00")
    LocalDateTime completedAt,

    @Schema(description = "결제 취소 시간", example = "2023-01-01T13:00:00")
    LocalDateTime cancelledAt,

    @Schema(description = "결제 상세 정보", example = "{\"cardNumber\":\"1234-xxxx-xxxx-5678\"}")
    String paymentDetails,

    @Schema(description = "생성 시간", example = "2023-01-01T11:00:00")
    LocalDateTime createdAt,

    @Schema(description = "예약 ID", example = "1")
    Long reservationId
) {

    /**
     * 도메인 모델을 응답 DTO로 변환
     * @param payment 결제 도메인 모델
     * @return 결제 응답 DTO
     */
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getExternalPaymentId(),
                payment.getAmount(),
                payment.getMethod() != null ? payment.getMethod().name() : null,
                payment.getStatus() != null ? payment.getStatus().name() : null,
                payment.getCompletedAt(),
                payment.getCancelledAt(),
                payment.getPaymentDetails(),
                payment.getCreatedAt(),
                payment.getReservation() != null ? payment.getReservation().getId() : null
        );
    }

    /**
     * 도메인 모델 목록을 응답 DTO 목록으로 변환
     * @param payments 결제 도메인 모델 목록
     * @return 결제 응답 DTO 목록
     */
    public static List<PaymentResponse> fromList(List<Payment> payments) {
        return payments.stream()
                .map(PaymentResponse::from)
                .collect(Collectors.toList());
    }
}
