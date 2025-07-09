package concert.mania.concert.domain.model;

import concert.mania.concert.domain.model.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 예매 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Reservation {
    
    private Long id; // 예매 고유 식별자
    private String reservationNumber; // 예매 번호 (고객에게 표시되는 식별자)
    private Long userId; // 예매한 사용자 ID
    private BigDecimal totalAmount; // 총 결제 금액
    private ReservationStatus status; // 예매 상태 (PENDING, COMPLETED, CANCELLED)
    private LocalDateTime completedAt; // 예매 완료 시간
    private LocalDateTime cancelledAt; // 예매 취소 시간
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    // 관계
    private Concert concert; // 예매한 콘서트
    private List<ReservationDetail> reservationDetails = new ArrayList<>(); // 예매 상세 정보 목록
    private Payment payment; // 결제 정보
    
    /**
     * 예매가 완료되었는지 확인
     * @return 완료 여부
     */
    public boolean isCompleted() {
        return status == ReservationStatus.COMPLETED;
    }
    
    /**
     * 예매가 취소되었는지 확인
     * @return 취소 여부
     */
    public boolean isCancelled() {
        return status == ReservationStatus.CANCELLED;
    }
    
    /**
     * 예매가 진행 중인지 확인
     * @return 진행 중 여부
     */
    public boolean isPending() {
        return status == ReservationStatus.PENDING;
    }
    
    /**
     * 예매 완료 처리
     */
    public void complete() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("진행 중인 예매만 완료 처리할 수 있습니다.");
        }
        this.status = ReservationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * 예매 취소 처리
     */
    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예매입니다.");
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
    
    /**
     * 예매 상세 정보 추가
     * @param detail 추가할 예매 상세 정보
     */
    public void addReservationDetail(ReservationDetail detail) {
        this.reservationDetails.add(detail);
    }
    
    /**
     * 총 좌석 수 계산
     * @return 총 좌석 수
     */
    public int getTotalSeats() {
        return reservationDetails.size();
    }
}