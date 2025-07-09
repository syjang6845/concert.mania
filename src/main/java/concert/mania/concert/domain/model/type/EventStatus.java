package concert.mania.concert.domain.model.type;

/**
 * 이벤트 처리 상태를 나타내는 열거형
 */
public enum EventStatus {
    /**
     * 처리 대기 중인 상태
     */
    PENDING,
    
    /**
     * 처리 완료된 상태
     */
    PROCESSED,
    
    /**
     * 처리 실패한 상태
     */
    FAILED
}