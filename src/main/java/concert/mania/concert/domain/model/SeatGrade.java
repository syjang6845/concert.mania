package concert.mania.concert.domain.model;

import concert.mania.concert.domain.model.type.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 좌석 등급 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SeatGrade {

    private Long id; // 좌석 등급 고유 식별자
    private String name; // 좌석 등급명 (VIP, R, S, A 등)
    private BigDecimal price; // 좌석 등급별 가격
    private String description; // 좌석 등급 설명
    private Integer capacity; // 해당 등급의 총 좌석 수용 인원
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간

    // 관계
    private Concert concert; // 연관된 콘서트
    private List<Seat> seats = new ArrayList<>(); // 해당 등급에 속한 좌석 목록

    /**
     * 해당 등급의 남은 좌석 수 계산
     * @return 남은 좌석 수
     */
    public int getRemainingSeats() {
        if (seats == null || seats.isEmpty()) {
            return 0;
        }

        return (int) seats.stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .count();
    }

    /**
     * 해당 등급의 판매된 좌석 수 계산
     * @return 판매된 좌석 수
     */
    public int getSoldSeats() {
        if (seats == null || seats.isEmpty()) {
            return 0;
        }

        return (int) seats.stream()
                .filter(seat -> seat.getStatus() == SeatStatus.SOLD)
                .count();
    }

    /**
     * 해당 등급의 판매율 계산
     * @return 판매율 (0.0 ~ 1.0)
     */
    public double getSalesRate() {
        if (seats == null || seats.isEmpty() || capacity == 0) {
            return 0.0;
        }

        return (double) getSoldSeats() / capacity;
    }
}
