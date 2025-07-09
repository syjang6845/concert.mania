package concert.mania.concert.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "application_metrics")
public class ApplicationMetricJpaEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric_id")
    private Long id; // 메트릭 고유 식별자
    
    @Column(nullable = false, length = 100)
    private String metricName; // 메트릭 이름 (예: cpu_usage, memory_usage, active_users)
    
    @Column(nullable = false)
    private Double metricValue; // 메트릭 값
    
    @Column(nullable = false)
    private LocalDateTime timestamp; // 메트릭 측정 시간
    
    @Column(columnDefinition = "TEXT")
    private String tags; // JSON 형태로 태그 저장 (예: {"server": "app-1", "environment": "production"})
}