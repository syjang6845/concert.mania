package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.concert.domain.model.type.EventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "events", indexes = {
    @Index(name = "idx_event_type", columnList = "eventType"),
    @Index(name = "idx_event_status", columnList = "status")
})
public class EventJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id; // 이벤트 고유 식별자
    
    @Column(nullable = false, length = 100)
    private String eventType; // 이벤트 유형 (RESERVATION_COMPLETED, PAYMENT_COMPLETED 등)
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload; // JSON 형태로 이벤트 데이터 저장
    
    @Column
    private LocalDateTime processedAt; // 이벤트 처리 시간
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status; // 이벤트 상태 (PENDING, PROCESSED, FAILED)
}