package concert.mania.concert.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 대기열 등록 요청 DTO
 */
public record WaitingQueueRegisterRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    @Schema(description = "사용자 ID", example = "1", required = true)
    Long userId
) {}
