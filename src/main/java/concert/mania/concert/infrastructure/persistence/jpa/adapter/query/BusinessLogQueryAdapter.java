package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.BusinessLogQueryPort;
import concert.mania.concert.domain.model.BusinessLog;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaBusinessLogRepository;
import concert.mania.concert.infrastructure.persistence.mapper.BusinessLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 비즈니스 로그 조회 영속성 어댑터
 * 비즈니스 로그 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessLogQueryAdapter implements BusinessLogQueryPort {

    private final DataJpaBusinessLogRepository businessLogRepository;
    private final BusinessLogMapper businessLogMapper;

    @Override
    public Optional<BusinessLog> findById(Long id) {
        return businessLogRepository.findById(id)
                .map(businessLogMapper::toDomain);
    }

    @Override
    public List<BusinessLog> findAll() {
        return businessLogRepository.findAll().stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByAction(String action) {
        return businessLogRepository.findByAction(action).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByEntityType(String entityType) {
        return businessLogRepository.findByEntityType(entityType).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByEntityId(String entityId) {
        return businessLogRepository.findByEntityId(entityId).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByUserId(String userId) {
        return businessLogRepository.findByUserId(userId).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByTimestampAfter(LocalDateTime timestamp) {
        return businessLogRepository.findByTimestampAfter(timestamp).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByTimestampBefore(LocalDateTime timestamp) {
        return businessLogRepository.findByTimestampBefore(timestamp).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return businessLogRepository.findByTimestampBetween(startTime, endTime).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByActionAndEntityType(String action, String entityType) {
        return businessLogRepository.findByActionAndEntityType(action, entityType).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessLog> findByEntityTypeAndEntityId(String entityType, String entityId) {
        return businessLogRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByActionAndTimestampBetween(String action, LocalDateTime startTime, LocalDateTime endTime) {
        return businessLogRepository.countByActionAndTimestampBetween(action, startTime, endTime);
    }
}