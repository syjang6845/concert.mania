package concert.mania.concert.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 좌석 선택 요청 DTO
 */
@Schema(description = "좌석 선택 요청")
public record SeatSelectionRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    @Schema(description = "사용자 ID", example = "1", required = true)
    Long userId
) {}
