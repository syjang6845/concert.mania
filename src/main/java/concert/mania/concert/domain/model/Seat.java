package concert.mania.concert.domain.model;

import concert.mania.concert.domain.model.type.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 좌석 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Seat {
    
    private Long id; // 좌석 고유 식별자
    private String seatNumber; // 좌석 번호 (예: "A-12")
    private Integer seatRow; // 좌석 행 번호
    private Integer seatCol; // 좌석 열 번호
    private SeatStatus status; // 좌석 상태 (AVAILABLE, SELECTED, RESERVED, SOLD)
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    // 관계
    private Concert concert; // 연관된 콘서트
    private SeatGrade seatGrade; // 좌석 등급
    
    /**
     * 좌석이 예매 가능한지 확인
     * @return 예매 가능 여부
     */
    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }
    
    /**
     * 좌석이 이미 판매되었는지 확인
     * @return 판매 여부
     */
    public boolean isSold() {
        return status == SeatStatus.SOLD;
    }
    
    /**
     * 좌석 상태 변경
     * @param status 변경할 상태
     */
    public void setStatus(SeatStatus status) {
        this.status = status;
    }
    
    /**
     * 좌석 선택 (임시 점유)
     */
    public void select() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 선택되었거나 판매된 좌석입니다.");
        }
        this.status = SeatStatus.SELECTED;
    }
    
    /**
     * 좌석 예매 진행
     */
    public void reserve() {
        if (this.status != SeatStatus.SELECTED && this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("선택되지 않았거나 이미 판매된 좌석입니다.");
        }
        this.status = SeatStatus.RESERVED;
    }
    
    /**
     * 좌석 판매 완료
     */
    public void sell() {
        if (this.status != SeatStatus.RESERVED) {
            throw new IllegalStateException("예매 진행 중이 아닌 좌석입니다.");
        }
        this.status = SeatStatus.SOLD;
    }
    
    /**
     * 좌석 상태 초기화 (예매 취소 등)
     */
    public void reset() {
        if (this.status == SeatStatus.SOLD) {
            throw new IllegalStateException("이미 판매 완료된 좌석은 초기화할 수 없습니다.");
        }
        this.status = SeatStatus.AVAILABLE;
    }
}