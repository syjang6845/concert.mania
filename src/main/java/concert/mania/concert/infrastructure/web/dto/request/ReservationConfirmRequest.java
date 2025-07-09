package concert.mania.concert.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 예약 확정 요청 DTO
 */
public record ReservationConfirmRequest(
    @NotNull(message = "예약 ID는 필수입니다")
    @Schema(description = "예약 ID", example = "1", required = true)
    Long reservationId,

    @NotNull(message = "사용자 ID는 필수입니다")
    @Schema(description = "사용자 ID", example = "1", required = true)
    Long userId,

    @NotNull(message = "콘서트 ID는 필수입니다")
    @Schema(description = "콘서트 ID", example = "1", required = true)
    Long concertId,

    @NotNull(message = "좌석 ID 목록은 필수입니다")
    @NotEmpty(message = "좌석 ID 목록은 비어있을 수 없습니다")
    @Schema(description = "좌석 ID 목록", example = "[1, 2, 3]", required = true)
    List<Long> seatIds
) {}
