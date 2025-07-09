package concert.mania.concert.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "business_logs", indexes = {
    @Index(name = "idx_business_log_action", columnList = "action"),
    @Index(name = "idx_business_log_entity_type", columnList = "entityType"),
    @Index(name = "idx_business_log_timestamp", columnList = "timestamp")
})
public class BusinessLogJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id; // 로그 고유 식별자
    
    @Column(nullable = false, length = 100)
    private String action; // 비즈니스 액션 (RESERVATION_CREATED, PAYMENT_COMPLETED 등)
    
    @Column(nullable = false, length = 100)
    private String entityType; // 관련 엔티티 타입 (User, Concert, Reservation 등)
    
    @Column(length = 100)
    private String entityId; // 관련 엔티티 ID
    
    @Column(length = 100)
    private String userId; // 관련 사용자 ID
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String details; // JSON 형태로 상세 정보 저장
    
    @Column(nullable = false)
    private LocalDateTime timestamp; // 로그 생성 시간
}