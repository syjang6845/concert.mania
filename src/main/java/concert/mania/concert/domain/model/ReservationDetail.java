package concert.mania.concert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 예매 상세 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReservationDetail {
    
    private Long id; // 예매 상세 고유 식별자
    private BigDecimal price; // 구매 당시 좌석 가격
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    // 관계
    private Reservation reservation; // 연관된 예매
    private Seat seat; // 예매된 좌석
    
    /**
     * 좌석 등급 정보 조회
     * @return 좌석 등급
     */
    public SeatGrade getSeatGrade() {
        return seat.getSeatGrade();
    }
    
    /**
     * 좌석 번호 조회
     * @return 좌석 번호
     */
    public String getSeatNumber() {
        return seat.getSeatNumber();
    }
    
    /**
     * 좌석 행 번호 조회
     * @return 좌석 행 번호
     */
    public Integer getRowNumber() {
        return seat.getSeatRow();
    }
    
    /**
     * 좌석 열 번호 조회
     * @return 좌석 열 번호
     */
    public Integer getColumnNumber() {
        return seat.getSeatCol();
    }
}