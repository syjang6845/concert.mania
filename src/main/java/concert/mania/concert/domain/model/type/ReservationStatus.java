package concert.mania.concert.domain.model.type;

/**
 * 예매 상태를 나타내는 열거형
 */
public enum ReservationStatus {
    /**
     * 예매 진행 중인 상태
     */
    PENDING,
    
    /**
     * 예매 완료된 상태
     */
    COMPLETED,
    
    /**
     * 예매 취소된 상태
     */
    CANCELLED
}