package concert.mania.concert.domain.model;

import concert.mania.concert.domain.model.type.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 대기열 항목 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WaitingQueueEntry {
    
    private Long id; // 대기열 항목 고유 식별자
    private Long userId; // 대기 중인 사용자 ID
    private Integer queuePosition; // 대기열 내 위치 (순번)
    private LocalDateTime enteredAt; // 대기열 진입 시간
    private LocalDateTime admittedAt; // 입장 허용 시간
    private QueueStatus status; // 대기열 상태 (WAITING, ADMITTED, EXPIRED, CANCELLED)
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    // 관계
    private Concert concert; // 연관된 콘서트
    
    /**
     * 대기 중인지 확인
     * @return 대기 중 여부
     */
    public boolean isWaiting() {
        return status == QueueStatus.WAITING;
    }
    
    /**
     * 입장 허용되었는지 확인
     * @return 입장 허용 여부
     */
    public boolean isAdmitted() {
        return status == QueueStatus.ADMITTED;
    }
    
    /**
     * 만료되었는지 확인
     * @return 만료 여부
     */
    public boolean isExpired() {
        return status == QueueStatus.EXPIRED;
    }
    
    /**
     * 취소되었는지 확인
     * @return 취소 여부
     */
    public boolean isCancelled() {
        return status == QueueStatus.CANCELLED;
    }
    
    /**
     * 입장 허용 처리
     */
    public void admit() {
        if (this.status != QueueStatus.WAITING) {
            throw new IllegalStateException("대기 중인 항목만 입장 허용할 수 있습니다.");
        }
        this.status = QueueStatus.ADMITTED;
        this.admittedAt = LocalDateTime.now();
    }
    
    /**
     * 만료 처리
     */
    public void expire() {
        if (this.status != QueueStatus.WAITING && this.status != QueueStatus.ADMITTED) {
            throw new IllegalStateException("대기 중이거나 입장 허용된 항목만 만료 처리할 수 있습니다.");
        }
        this.status = QueueStatus.EXPIRED;
    }
    
    /**
     * 취소 처리
     */
    public void cancel() {
        if (this.status == QueueStatus.CANCELLED || this.status == QueueStatus.EXPIRED) {
            throw new IllegalStateException("이미 취소되었거나 만료된 항목입니다.");
        }
        this.status = QueueStatus.CANCELLED;
    }
    
    /**
     * 대기 시간 계산 (분 단위)
     * @return 대기 시간 (분)
     */
    public long getWaitingTimeInMinutes() {
        if (this.status == QueueStatus.WAITING) {
            return ChronoUnit.MINUTES.between(enteredAt, LocalDateTime.now());
        } else if (this.status == QueueStatus.ADMITTED) {
            return ChronoUnit.MINUTES.between(enteredAt, admittedAt);
        } else {
            return 0;
        }
    }
    
    /**
     * 예상 대기 시간 계산 (분 단위)
     * @param averageProcessingTimePerPosition 위치당 평균 처리 시간 (분)
     * @return 예상 대기 시간 (분)
     */
    public long getEstimatedWaitingTimeInMinutes(double averageProcessingTimePerPosition) {
        if (this.status != QueueStatus.WAITING) {
            return 0;
        }
        return (long) (queuePosition * averageProcessingTimePerPosition);
    }
}