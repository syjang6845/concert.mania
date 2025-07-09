package concert.mania.concert.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "seat_locks", indexes = {
    @Index(name = "idx_seat_lock_expires_at", columnList = "expiresAt")
})
public class SeatLockJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_lock_id")
    private Long id; // 좌석 잠금 고유 식별자
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    private SeatJpaEntity seat; // 잠금 대상 좌석
    
    @Column(nullable = false)
    private Long userId; // 좌석을 잠근 사용자 ID
    
    @Column(nullable = false)
    private LocalDateTime lockedAt; // 좌석 잠금 시작 시간
    
    @Column(nullable = false)
    private LocalDateTime expiresAt; // 좌석 잠금 만료 시간 (10분 후 자동 해제)
}