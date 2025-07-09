package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.concert.domain.model.type.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservation_user_id", columnList = "user_id"),
    @Index(name = "idx_reservation_concert_id", columnList = "concert_id"),
    @Index(name = "idx_reservation_status", columnList = "status")
})
public class ReservationJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id; // 예매 고유 식별자
    
    @Column(nullable = false, unique = true, length = 50)
    private String reservationNumber; // 예매 번호 (고객에게 표시되는 식별자)
    
    @Column(name = "user_id", nullable = false)
    private Long userId; // 예매한 사용자 ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private ConcertJpaEntity concert; // 예매한 콘서트
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // 총 결제 금액
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status; // 예매 상태 (PENDING, COMPLETED, CANCELLED)
    
    @Column
    private LocalDateTime completedAt; // 예매 완료 시간
    
    @Column
    private LocalDateTime cancelledAt; // 예매 취소 시간
    
    // 양방향 관계 설정
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationDetailJpaEntity> reservationDetails = new ArrayList<>(); // 예매 상세 정보 목록
}