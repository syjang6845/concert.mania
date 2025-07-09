package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.ApplicationMetricCommandPort;
import concert.mania.concert.domain.model.ApplicationMetric;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ApplicationMetricJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaApplicationMetricRepository;
import concert.mania.concert.infrastructure.persistence.mapper.ApplicationMetricMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 애플리케이션 메트릭 명령 영속성 어댑터
 * 애플리케이션 메트릭 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class ApplicationMetricCommandAdapter implements ApplicationMetricCommandPort {

    private final DataJpaApplicationMetricRepository metricRepository;
    private final ApplicationMetricMapper metricMapper;

    @Override
    public ApplicationMetric save(ApplicationMetric metric) {
        ApplicationMetricJpaEntity entity = metricMapper.toEntity(metric);
        ApplicationMetricJpaEntity savedEntity = metricRepository.save(entity);
        return metricMapper.toDomain(savedEntity);
    }

    @Override
    public List<ApplicationMetric> saveAll(List<ApplicationMetric> metrics) {
        List<ApplicationMetricJpaEntity> entities = metrics.stream()
                .map(metricMapper::toEntity)
                .collect(Collectors.toList());
        List<ApplicationMetricJpaEntity> savedEntities = metricRepository.saveAll(entities);
        return savedEntities.stream()
                .map(metricMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long metricId) {
        metricRepository.deleteById(metricId);
    }

    @Override
    public int deleteMetricsBefore(LocalDateTime dateTime) {
        List<ApplicationMetricJpaEntity> metricsToDelete = metricRepository.findByTimestampBefore(dateTime);
        int count = metricsToDelete.size();
        metricRepository.deleteAll(metricsToDelete);
        return count;
    }

    @Override
    public int deleteMetricsByName(String metricName) {
        List<ApplicationMetricJpaEntity> metricsToDelete = metricRepository.findByMetricName(metricName);
        int count = metricsToDelete.size();
        metricRepository.deleteAll(metricsToDelete);
        return count;
    }

    @Override
    public ApplicationMetric recordMetric(String metricName, Double metricValue, String tags) {
        ApplicationMetricJpaEntity entity = ApplicationMetricJpaEntity.builder()
                .metricName(metricName)
                .metricValue(metricValue)
                .timestamp(LocalDateTime.now())
                .tags(tags)
                .build();
        ApplicationMetricJpaEntity savedEntity = metricRepository.save(entity);
        return metricMapper.toDomain(savedEntity);
    }

    @Override
    public ApplicationMetric update(ApplicationMetric metric) {
        // Check if the metric exists
        metricRepository.findById(metric.getId())
                .orElseThrow(() -> new NoSuchElementException("메트릭 정보를 찾을 수 없습니다. ID: " + metric.getId()));

        ApplicationMetricJpaEntity entity = metricMapper.toEntity(metric);
        ApplicationMetricJpaEntity savedEntity = metricRepository.save(entity);
        return metricMapper.toDomain(savedEntity);
    }
}
