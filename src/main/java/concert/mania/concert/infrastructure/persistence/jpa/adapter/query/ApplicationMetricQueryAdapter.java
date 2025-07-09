package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.ApplicationMetricQueryPort;
import concert.mania.concert.domain.model.ApplicationMetric;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ApplicationMetricJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaApplicationMetricRepository;
import concert.mania.concert.infrastructure.persistence.mapper.ApplicationMetricMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 애플리케이션 메트릭 조회 영속성 어댑터
 * 애플리케이션 메트릭 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationMetricQueryAdapter implements ApplicationMetricQueryPort {

    private final DataJpaApplicationMetricRepository metricRepository;
    private final ApplicationMetricMapper metricMapper;

    @Override
    public Optional<ApplicationMetric> findById(Long id) {
        return metricRepository.findById(id)
                .map(metricMapper::toDomain);
    }

    @Override
    public List<ApplicationMetric> findAll() {
        return metricRepository.findAll().stream()
                .map(metricMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationMetric> findByMetricName(String metricName) {
        return metricRepository.findByMetricName(metricName).stream()
                .map(metricMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationMetric> findByTimestampBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return metricRepository.findByTimestampBetween(startDateTime, endDateTime).stream()
                .map(metricMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationMetric> findByMetricNameAndTimestampBetween(String metricName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return metricRepository.findByMetricNameAndTimestampBetween(metricName, startDateTime, endDateTime).stream()
                .map(metricMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ApplicationMetric> findLatestByMetricName(String metricName) {
        ApplicationMetricJpaEntity entity = metricRepository.findLatestByMetricName(metricName);
        return Optional.ofNullable(entity).map(metricMapper::toDomain);
    }

    @Override
    public double calculateAverageByMetricNameAndTimestampBetween(String metricName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Double average = metricRepository.calculateAverageByMetricNameAndPeriod(metricName, startDateTime, endDateTime);
        return average != null ? average : 0.0;
    }

    @Override
    public double findMaxValueByMetricNameAndTimestampBetween(String metricName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Since there's no direct repository method for max, we'll fetch all metrics and calculate max
        List<ApplicationMetricJpaEntity> metrics = metricRepository.findByMetricNameAndTimestampBetween(
                metricName, startDateTime, endDateTime);
        
        return metrics.stream()
                .mapToDouble(ApplicationMetricJpaEntity::getMetricValue)
                .max()
                .orElse(0.0);
    }

    @Override
    public double findMinValueByMetricNameAndTimestampBetween(String metricName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Since there's no direct repository method for min, we'll fetch all metrics and calculate min
        List<ApplicationMetricJpaEntity> metrics = metricRepository.findByMetricNameAndTimestampBetween(
                metricName, startDateTime, endDateTime);
        
        return metrics.stream()
                .mapToDouble(ApplicationMetricJpaEntity::getMetricValue)
                .min()
                .orElse(0.0);
    }

    @Override
    public List<ApplicationMetric> findByTag(String tagKey, String tagValue) {
        // Since there's no direct repository method for tag search, we'll fetch all metrics and filter
        // This assumes tags are stored in JSON format like {"key1":"value1","key2":"value2"}
        String tagPattern = "\"" + tagKey + "\":\"" + tagValue + "\"";
        
        return metricRepository.findAll().stream()
                .filter(metric -> metric.getTags() != null && metric.getTags().contains(tagPattern))
                .map(metricMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByMetricName(String metricName) {
        return metricRepository.findByMetricName(metricName).size();
    }
}