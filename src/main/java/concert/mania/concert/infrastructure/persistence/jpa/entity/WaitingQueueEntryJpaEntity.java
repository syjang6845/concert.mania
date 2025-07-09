package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.concert.domain.model.type.QueueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "waiting_queue_entries", indexes = {
    @Index(name = "idx_waiting_queue_concert_id", columnList = "concert_id"),
    @Index(name = "idx_waiting_queue_user_id", columnList = "user_id"),
    @Index(name = "idx_waiting_queue_status", columnList = "status")
})
public class WaitingQueueEntryJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiting_queue_entry_id")
    private Long id; // 대기열 항목 고유 식별자
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private ConcertJpaEntity concert; // 연관된 콘서트
    
    @Column(name = "user_id", nullable = false)
    private Long userId; // 대기 중인 사용자 ID
    
    @Column(nullable = false)
    private Integer queuePosition; // 대기열 내 위치 (순번)
    
    @Column(nullable = false)
    private LocalDateTime enteredAt; // 대기열 진입 시간
    
    @Column
    private LocalDateTime admittedAt; // 입장 허용 시간
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QueueStatus status; // 대기열 상태 (WAITING, ADMITTED, EXPIRED, CANCELLED)
}