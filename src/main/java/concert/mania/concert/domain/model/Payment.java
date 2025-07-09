package concert.mania.concert.domain.model;

import concert.mania.concert.domain.model.type.PaymentMethod;
import concert.mania.concert.domain.model.type.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Payment {
    
    private Long id; // 결제 고유 식별자
    private String externalPaymentId; // 외부 결제 시스템 ID
    private BigDecimal amount; // 결제 금액
    private PaymentMethod method; // 결제 방식 (CREDIT_CARD, BANK_TRANSFER, MOBILE)
    private PaymentStatus status; // 결제 상태 (PENDING, COMPLETED, FAILED, CANCELLED)
    private LocalDateTime completedAt; // 결제 완료 시간
    private LocalDateTime cancelledAt; // 결제 취소 시간
    private String paymentDetails; // JSON 형태로 결제 상세 정보 저장
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    // 관계
    private Reservation reservation; // 연관된 예매
    
    /**
     * 결제가 완료되었는지 확인
     * @return 완료 여부
     */
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }
    
    /**
     * 결제가 실패했는지 확인
     * @return 실패 여부
     */
    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }
    
    /**
     * 결제가 취소되었는지 확인
     * @return 취소 여부
     */
    public boolean isCancelled() {
        return status == PaymentStatus.CANCELLED;
    }
    
    /**
     * 결제가 진행 중인지 확인
     * @return 진행 중 여부
     */
    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }
    
    /**
     * 결제 완료 처리
     */
    public void complete() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("진행 중인 결제만 완료 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        
        // 예매도 완료 처리
        if (this.reservation != null) {
            this.reservation.complete();
        }
    }
    
    /**
     * 결제 실패 처리
     */
    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("진행 중인 결제만 실패 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.FAILED;
    }
    
    /**
     * 결제 취소 처리
     */
    public void cancel() {
        if (this.status == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }
        if (this.status == PaymentStatus.FAILED) {
            throw new IllegalStateException("실패한 결제는 취소할 수 없습니다.");
        }
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        
        // 예매도 취소 처리
        if (this.reservation != null) {
            this.reservation.cancel();
        }
    }
}