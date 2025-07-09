package concert.mania.concert.infrastructure.web.dto.response;

import concert.mania.concert.domain.model.SeatLock;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 좌석 잠금 응답 DTO
 */
@Schema(description = "좌석 잠금 정보")
public record SeatLockResponse(
    @Schema(description = "좌석 잠금 ID", example = "1")
    Long id,

    @Schema(description = "사용자 ID", example = "1")
    Long userId,

    @Schema(description = "좌석 ID", example = "1")
    Long seatId,

    @Schema(description = "잠금 시작 시간", example = "2023-01-01T12:00:00")
    LocalDateTime lockedAt,

    @Schema(description = "잠금 만료 시간", example = "2023-01-01T12:10:00")
    LocalDateTime expiresAt,

    @Schema(description = "남은 시간(분)", example = "5")
    long remainingMinutes,

    @Schema(description = "만료 여부", example = "false")
    boolean expired
) {

    /**
     * SeatLock 도메인 모델을 SeatLockResponse DTO로 변환
     * @param seatLock 좌석 잠금 도메인 모델
     * @return 좌석 잠금 응답 DTO
     */
    public static SeatLockResponse from(SeatLock seatLock) {
        return new SeatLockResponse(
                seatLock.getId(),
                seatLock.getUserId(),
                seatLock.getSeat().getId(),
                seatLock.getLockedAt(),
                seatLock.getExpiresAt(),
                seatLock.getRemainingMinutes(),
                seatLock.isExpired()
        );
    }
}
