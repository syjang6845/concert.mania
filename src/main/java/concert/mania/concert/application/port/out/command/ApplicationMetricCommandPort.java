package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.ApplicationMetric;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 애플리케이션 메트릭 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface ApplicationMetricCommandPort {
    
    /**
     * 메트릭 저장
     * 
     * @param metric 저장할 메트릭
     * @return 저장된 메트릭
     */
    ApplicationMetric save(ApplicationMetric metric);
    
    /**
     * 메트릭 일괄 저장
     * 
     * @param metrics 저장할 메트릭 목록
     * @return 저장된 메트릭 목록
     */
    List<ApplicationMetric> saveAll(List<ApplicationMetric> metrics);
    
    /**
     * 메트릭 삭제
     * 
     * @param metricId 삭제할 메트릭 ID
     */
    void delete(Long metricId);
    
    /**
     * 특정 시간 이전의 메트릭 삭제
     * 
     * @param dateTime 기준 시간
     * @return 삭제된 메트릭 수
     */
    int deleteMetricsBefore(LocalDateTime dateTime);
    
    /**
     * 특정 메트릭 이름의 메트릭 삭제
     * 
     * @param metricName 메트릭 이름
     * @return 삭제된 메트릭 수
     */
    int deleteMetricsByName(String metricName);
    
    /**
     * 메트릭 기록
     * 
     * @param metricName 메트릭 이름
     * @param metricValue 메트릭 값
     * @param tags 태그 (JSON 형태)
     * @return 저장된 메트릭
     */
    ApplicationMetric recordMetric(String metricName, Double metricValue, String tags);
    
    /**
     * 메트릭 업데이트
     * 
     * @param metric 업데이트할 메트릭 정보
     * @return 업데이트된 메트릭
     */
    ApplicationMetric update(ApplicationMetric metric);
}