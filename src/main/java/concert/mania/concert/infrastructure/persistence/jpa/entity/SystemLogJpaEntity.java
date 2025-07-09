package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.concert.domain.model.type.LogLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "system_logs", indexes = {
    @Index(name = "idx_system_log_timestamp", columnList = "timestamp"),
    @Index(name = "idx_system_log_level", columnList = "logLevel")
})
public class SystemLogJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id; // 로그 고유 식별자
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel; // 로그 레벨 (INFO, WARN, ERROR)
    
    @Column(nullable = false, length = 255)
    private String logger; // 로그를 생성한 로거 이름
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message; // 로그 메시지
    
    @Column(columnDefinition = "TEXT")
    private String stackTrace; // 예외 발생 시 스택 트레이스
    
    @Column(nullable = false)
    private LocalDateTime timestamp; // 로그 생성 시간
    
    @Column(length = 100)
    private String userId; // 관련 사용자 ID (있는 경우)
    
    @Column(length = 255)
    private String requestUri; // 요청 URI (있는 경우)
}