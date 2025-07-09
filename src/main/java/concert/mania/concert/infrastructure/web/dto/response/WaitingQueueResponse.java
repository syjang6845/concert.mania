package concert.mania.concert.infrastructure.web.dto.response;

import concert.mania.concert.domain.model.WaitingQueue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 대기열 응답 DTO
 */
public record WaitingQueueResponse(
    @Schema(description = "대기열 ID", example = "1")
    Long id,

    @Schema(description = "콘서트 ID", example = "1")
    Long concertId,

    @Schema(description = "사용자 ID", example = "1")
    Long userId,

    @Schema(description = "대기열 위치", example = "10")
    Integer position,

    @Schema(description = "예상 대기 시간 (분)", example = "5")
    Integer estimatedWaitingTime,

    @Schema(description = "대기 상태", example = "WAITING")
    String status,

    @Schema(description = "등록 시간", example = "2023-01-01T12:00:00")
    LocalDateTime registeredAt,

    @Schema(description = "입장 시간", example = "2023-01-01T12:05:00")
    LocalDateTime enteredAt
) {

    /**
     * 도메인 모델을 응답 DTO로 변환
     * @param waitingQueue 대기열 도메인 모델
     * @return 대기열 응답 DTO
     */
    public static WaitingQueueResponse from(WaitingQueue waitingQueue) {
        return new WaitingQueueResponse(
                waitingQueue.getId(),
                waitingQueue.getConcertId(),
                waitingQueue.getUserId(),
                waitingQueue.getPosition(),
                waitingQueue.calculateEstimatedWaitingTime(),
                waitingQueue.getStatus().name(),
                waitingQueue.getRegisteredAt(),
                waitingQueue.getEnteredAt()
        );
    }
}
