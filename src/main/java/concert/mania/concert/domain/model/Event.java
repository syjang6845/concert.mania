package concert.mania.concert.domain.model;

import concert.mania.concert.domain.model.type.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 이벤트 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Event {
    
    private Long id; // 이벤트 고유 식별자
    private String eventType; // 이벤트 유형 (RESERVATION_COMPLETED, PAYMENT_COMPLETED 등)
    private String payload; // JSON 형태로 이벤트 데이터 저장
    private LocalDateTime processedAt; // 이벤트 처리 시간
    private EventStatus status; // 이벤트 상태 (PENDING, PROCESSED, FAILED)
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    /**
     * 이벤트가 처리 대기 중인지 확인
     * @return 처리 대기 중 여부
     */
    public boolean isPending() {
        return status == EventStatus.PENDING;
    }
    
    /**
     * 이벤트가 처리 완료되었는지 확인
     * @return 처리 완료 여부
     */
    public boolean isProcessed() {
        return status == EventStatus.PROCESSED;
    }
    
    /**
     * 이벤트 처리가 실패했는지 확인
     * @return 처리 실패 여부
     */
    public boolean isFailed() {
        return status == EventStatus.FAILED;
    }
    
    /**
     * 이벤트 처리 완료 처리
     */
    public void markAsProcessed() {
        if (this.status != EventStatus.PENDING) {
            throw new IllegalStateException("대기 중인 이벤트만 처리 완료로 변경할 수 있습니다.");
        }
        this.status = EventStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }
    
    /**
     * 이벤트 처리 실패 처리
     */
    public void markAsFailed() {
        if (this.status != EventStatus.PENDING) {
            throw new IllegalStateException("대기 중인 이벤트만 처리 실패로 변경할 수 있습니다.");
        }
        this.status = EventStatus.FAILED;
    }
    
    /**
     * 이벤트 재처리를 위해 상태 초기화
     */
    public void resetForRetry() {
        if (this.status != EventStatus.FAILED) {
            throw new IllegalStateException("실패한 이벤트만 재처리를 위해 초기화할 수 있습니다.");
        }
        this.status = EventStatus.PENDING;
    }
}