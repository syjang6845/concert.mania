package concert.mania.concert.domain.model.type;

/**
 * 결제 방식을 나타내는 열거형
 */
public enum PaymentMethod {
    /**
     * 신용카드 결제
     */
    CREDIT_CARD,
    
    /**
     * 계좌이체 결제
     */
    BANK_TRANSFER,
    
    /**
     * 모바일 결제
     */
    MOBILE
}