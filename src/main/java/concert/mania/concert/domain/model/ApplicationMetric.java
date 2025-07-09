package concert.mania.concert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 애플리케이션 메트릭 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ApplicationMetric {
    
    private Long id; // 메트릭 고유 식별자
    private String metricName; // 메트릭 이름 (예: cpu_usage, memory_usage, active_users)
    private Double metricValue; // 메트릭 값
    private LocalDateTime timestamp; // 메트릭 측정 시간
    private String tags; // JSON 형태로 태그 저장 (예: {"server": "app-1", "environment": "production"})
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    /**
     * 메트릭이 CPU 사용량인지 확인
     * @return CPU 사용량 여부
     */
    public boolean isCpuUsage() {
        return "cpu_usage".equals(metricName);
    }
    
    /**
     * 메트릭이 메모리 사용량인지 확인
     * @return 메모리 사용량 여부
     */
    public boolean isMemoryUsage() {
        return "memory_usage".equals(metricName);
    }
    
    /**
     * 메트릭이 활성 사용자 수인지 확인
     * @return 활성 사용자 수 여부
     */
    public boolean isActiveUsers() {
        return "active_users".equals(metricName);
    }
    
    /**
     * 메트릭이 API 응답 시간인지 확인
     * @return API 응답 시간 여부
     */
    public boolean isApiResponseTime() {
        return "api_response_time".equals(metricName);
    }
    
    /**
     * 메트릭이 특정 시간 이후인지 확인
     * @param dateTime 기준 시간
     * @return 기준 시간 이후 여부
     */
    public boolean isAfter(LocalDateTime dateTime) {
        return timestamp.isAfter(dateTime);
    }
    
    /**
     * 메트릭이 특정 시간 이전인지 확인
     * @param dateTime 기준 시간
     * @return 기준 시간 이전 여부
     */
    public boolean isBefore(LocalDateTime dateTime) {
        return timestamp.isBefore(dateTime);
    }
    
    /**
     * 메트릭 값이 특정 임계값을 초과하는지 확인
     * @param threshold 임계값
     * @return 임계값 초과 여부
     */
    public boolean isAboveThreshold(double threshold) {
        return metricValue > threshold;
    }
    
    /**
     * 메트릭 값이 특정 임계값 미만인지 확인
     * @param threshold 임계값
     * @return 임계값 미만 여부
     */
    public boolean isBelowThreshold(double threshold) {
        return metricValue < threshold;
    }
}