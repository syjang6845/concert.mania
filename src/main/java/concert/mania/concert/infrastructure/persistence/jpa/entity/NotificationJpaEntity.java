package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.concert.domain.model.type.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "notifications")
public class NotificationJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id; // 알림 고유 식별자
    
    @Column(name = "user_id", nullable = false)
    private Long userId; // 알림 수신자 ID
    
    @Column(nullable = false, length = 20)
    private String type; // 알림 유형 (EMAIL, SMS)
    
    @Column(nullable = false, length = 255)
    private String title; // 알림 제목
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 알림 내용
    
    @Column
    private LocalDateTime sentAt; // 알림 발송 시간
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status; // 알림 상태 (PENDING, SENT, FAILED)
}