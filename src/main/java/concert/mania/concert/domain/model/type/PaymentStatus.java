package concert.mania.concert.domain.model.type;

/**
 * 결제 상태를 나타내는 열거형
 */
public enum PaymentStatus {
    /**
     * 결제 진행 중인 상태
     */
    PENDING,
    
    /**
     * 결제 완료된 상태
     */
    COMPLETED,
    
    /**
     * 결제 실패한 상태
     */
    FAILED,
    
    /**
     * 결제 취소된 상태
     */
    CANCELLED
}