package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.ApplicationMetric;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 애플리케이션 메트릭 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface ApplicationMetricQueryPort {
    
    /**
     * ID로 애플리케이션 메트릭 조회
     * 
     * @param id 애플리케이션 메트릭 ID
     * @return 애플리케이션 메트릭 (Optional)
     */
    Optional<ApplicationMetric> findById(Long id);
    
    /**
     * 모든 애플리케이션 메트릭 조회
     * 
     * @return 애플리케이션 메트릭 목록
     */
    List<ApplicationMetric> findAll();
    
    /**
     * 특정 메트릭 이름의 애플리케이션 메트릭 조회
     * 
     * @param metricName 메트릭 이름
     * @return 애플리케이션 메트릭 목록
     */
    List<ApplicationMetric> findByMetricName(String metricName);
    
    /**
     * 특정 기간 내의 애플리케이션 메트릭 조회
     * 
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 애플리케이션 메트릭 목록
     */
    List<ApplicationMetric> findByTimestampBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * 특정 메트릭 이름과 기간 내의 애플리케이션 메트릭 조회
     * 
     * @param metricName 메트릭 이름
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 애플리케이션 메트릭 목록
     */
    List<ApplicationMetric> findByMetricNameAndTimestampBetween(String metricName, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * 특정 메트릭 이름의 최신 애플리케이션 메트릭 조회
     * 
     * @param metricName 메트릭 이름
     * @return 애플리케이션 메트릭 (Optional)
     */
    Optional<ApplicationMetric> findLatestByMetricName(String metricName);
    
    /**
     * 특정 메트릭 이름의 평균값 계산
     * 
     * @param metricName 메트릭 이름
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 평균값
     */
    double calculateAverageByMetricNameAndTimestampBetween(String metricName, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * 특정 메트릭 이름의 최대값 조회
     * 
     * @param metricName 메트릭 이름
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 최대값
     */
    double findMaxValueByMetricNameAndTimestampBetween(String metricName, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * 특정 메트릭 이름의 최소값 조회
     * 
     * @param metricName 메트릭 이름
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 최소값
     */
    double findMinValueByMetricNameAndTimestampBetween(String metricName, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * 특정 태그를 포함하는 애플리케이션 메트릭 조회
     * 
     * @param tagKey 태그 키
     * @param tagValue 태그 값
     * @return 애플리케이션 메트릭 목록
     */
    List<ApplicationMetric> findByTag(String tagKey, String tagValue);
    
    /**
     * 특정 메트릭 이름의 애플리케이션 메트릭 수 조회
     * 
     * @param metricName 메트릭 이름
     * @return 애플리케이션 메트릭 수
     */
    long countByMetricName(String metricName);
}