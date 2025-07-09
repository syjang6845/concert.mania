package concert.mania.concert.domain.model.type;

/**
 * 좌석 상태를 나타내는 열거형
 */
public enum SeatStatus {
    /**
     * 예매 가능한 상태
     */
    AVAILABLE,
    
    /**
     * 사용자가 선택한 상태 (임시 점유)
     */
    SELECTED,
    
    /**
     * 예매가 진행 중인 상태
     */
    RESERVED,
    
    /**
     * 판매 완료된 상태
     */
    SOLD
}