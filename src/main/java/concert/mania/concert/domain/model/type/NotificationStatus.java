package concert.mania.concert.domain.model.type;

/**
 * 알림 상태를 나타내는 열거형
 */
public enum NotificationStatus {
    /**
     * 발송 대기 중인 상태
     */
    PENDING,
    
    /**
     * 발송 완료된 상태
     */
    SENT,
    
    /**
     * 발송 실패한 상태
     */
    FAILED
}