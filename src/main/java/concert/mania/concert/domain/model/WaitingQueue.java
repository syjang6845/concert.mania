package concert.mania.concert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 대기열 모델
 * 콘서트 예매 오픈 전 대기열 정보를 관리
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingQueue {
    
    private Long id;
    private Long concertId;
    private Long userId;
    private Integer position; // 대기열 위치
    private LocalDateTime registeredAt; // 등록 시간
    private LocalDateTime enteredAt; // 입장 시간
    private WaitingStatus status; // 대기 상태
    
    /**
     * 대기 상태 열거형
     */
    public enum WaitingStatus {
        WAITING,    // 대기 중
        PROCESSING, // 처리 중
        ENTERED,    // 입장 완료
        EXPIRED     // 만료됨
    }
    
    /**
     * 대기열 등록
     */
    public static WaitingQueue register(Long concertId, Long userId, Integer position) {
        return WaitingQueue.builder()
                .concertId(concertId)
                .userId(userId)
                .position(position)
                .registeredAt(LocalDateTime.now())
                .status(WaitingStatus.WAITING)
                .build();
    }
    
    /**
     * 입장 처리
     */
    public WaitingQueue enter() {
        this.enteredAt = LocalDateTime.now();
        this.status = WaitingStatus.ENTERED;
        return this;
    }
    
    /**
     * 처리 중 상태로 변경
     */
    public WaitingQueue process() {
        this.status = WaitingStatus.PROCESSING;
        return this;
    }
    
    /**
     * 만료 처리
     */
    public WaitingQueue expire() {
        this.status = WaitingStatus.EXPIRED;
        return this;
    }
    
    /**
     * 예상 대기 시간 계산 (분 단위)
     * 한 사용자당 평균 처리 시간을 30초로 가정
     */
    public int calculateEstimatedWaitingTime() {
        // 앞에 있는 사용자 수 * 평균 처리 시간(초) / 60
        return (position - 1) * 30 / 60;
    }
}