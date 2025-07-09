package concert.mania.concert.infrastructure.web.dto.request;

import concert.mania.concert.domain.model.type.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * 결제 요청 DTO
 */
public record PaymentRequest(
    @NotNull(message = "예약 ID는 필수입니다")
    @Schema(description = "예약 ID", example = "1", required = true)
    Long reservationId,

    @NotNull(message = "콘서트 ID는 필수입니다")
    @Schema(description = "콘서트 ID", example = "1", required = true)
    Long concertId,

    @NotNull(message = "좌석 ID는 필수입니다")
    @Schema(description = "좌석 ID", example = "1", required = true)
    Long seatId,

    @NotNull(message = "좌석 잠금 ID는 필수입니다")
    @Schema(description = "좌석 잠금 ID", example = "1", required = true)
    Long seatLockId,

    @NotNull(message = "결제 금액은 필수입니다")
    @Positive(message = "결제 금액은 양수여야 합니다")
    @Schema(description = "결제 금액", example = "50000", required = true)
    BigDecimal amount,

    @NotNull(message = "결제 방식은 필수입니다")
    @Schema(description = "결제 방식", example = "CREDIT_CARD", required = true)
    PaymentMethod method,

    @NotNull(message = "사용자 ID는 필수입니다")
    @Schema(description = "사용자 ID", example = "1", required = true)
    Long userId
) {}
