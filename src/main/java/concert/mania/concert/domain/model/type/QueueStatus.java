package concert.mania.concert.domain.model.type;

/**
 * 대기열 상태를 나타내는 열거형
 */
public enum QueueStatus {
    /**
     * 대기 중인 상태
     */
    WAITING,
    
    /**
     * 입장 허용된 상태
     */
    ADMITTED,
    
    /**
     * 만료된 상태 (입장 시간 초과)
     */
    EXPIRED,
    
    /**
     * 사용자에 의해 취소된 상태
     */
    CANCELLED
}