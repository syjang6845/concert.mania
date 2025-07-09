package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.concert.domain.model.type.PaymentMethod;
import concert.mania.concert.domain.model.type.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_reservation_id", columnList = "reservation_id"),
    @Index(name = "idx_payment_status", columnList = "status")
})
public class PaymentJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id; // 결제 고유 식별자
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private ReservationJpaEntity reservation; // 연관된 예매
    
    @Column(nullable = false, unique = true, length = 100)
    private String externalPaymentId; // 외부 결제 시스템 ID
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // 결제 금액
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method; // 결제 방식 (CREDIT_CARD, BANK_TRANSFER, MOBILE)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status; // 결제 상태 (PENDING, COMPLETED, FAILED, CANCELLED)
    
    @Column
    private LocalDateTime completedAt; // 결제 완료 시간
    
    @Column
    private LocalDateTime cancelledAt; // 결제 취소 시간
    
    @Column(columnDefinition = "TEXT")
    private String paymentDetails; // JSON 형태로 결제 상세 정보 저장
}