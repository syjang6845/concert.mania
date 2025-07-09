package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.infrastructure.persistence.jpa.entity.ApplicationMetricJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 애플리케이션 메트릭 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaApplicationMetricRepository extends JpaRepository<ApplicationMetricJpaEntity, Long> {
    
    /**
     * 특정 메트릭 이름으로 메트릭 목록 조회
     * 
     * @param metricName 메트릭 이름
     * @return 해당 이름의 메트릭 목록
     */
    List<ApplicationMetricJpaEntity> findByMetricName(String metricName);
    
    /**
     * 특정 시간 이후의 메트릭 목록 조회
     * 
     * @param timestamp 기준 시간
     * @return 해당 시간 이후의 메트릭 목록
     */
    List<ApplicationMetricJpaEntity> findByTimestampAfter(LocalDateTime timestamp);
    
    /**
     * 특정 시간 이전의 메트릭 목록 조회
     * 
     * @param timestamp 기준 시간
     * @return 해당 시간 이전의 메트릭 목록
     */
    List<ApplicationMetricJpaEntity> findByTimestampBefore(LocalDateTime timestamp);
    
    /**
     * 특정 기간 내의 메트릭 목록 조회
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 기간 내의 메트릭 목록
     */
    List<ApplicationMetricJpaEntity> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 특정 메트릭 이름과 기간으로 메트릭 목록 조회
     * 
     * @param metricName 메트릭 이름
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 이름과 기간의 메트릭 목록
     */
    List<ApplicationMetricJpaEntity> findByMetricNameAndTimestampBetween(
            String metricName, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 특정 메트릭 이름의 최근 값 조회
     * 
     * @param metricName 메트릭 이름
     * @return 최근 메트릭 값
     */
    @Query("SELECT m FROM ApplicationMetricJpaEntity m WHERE m.metricName = :metricName ORDER BY m.timestamp DESC LIMIT 1")
    ApplicationMetricJpaEntity findLatestByMetricName(@Param("metricName") String metricName);
    
    /**
     * 특정 메트릭 이름의 평균값 계산
     * 
     * @param metricName 메트릭 이름
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 평균 메트릭 값
     */
    @Query("SELECT AVG(m.metricValue) FROM ApplicationMetricJpaEntity m WHERE m.metricName = :metricName AND m.timestamp BETWEEN :startTime AND :endTime")
    Double calculateAverageByMetricNameAndPeriod(
            @Param("metricName") String metricName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}