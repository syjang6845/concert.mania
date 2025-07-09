package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.BusinessLogCommandPort;
import concert.mania.concert.domain.model.BusinessLog;
import concert.mania.concert.infrastructure.persistence.jpa.entity.BusinessLogJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaBusinessLogRepository;
import concert.mania.concert.infrastructure.persistence.mapper.BusinessLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 비즈니스 로그 명령 영속성 어댑터
 * 비즈니스 로그 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class BusinessLogCommandAdapter implements BusinessLogCommandPort {

    private final DataJpaBusinessLogRepository businessLogRepository;
    private final BusinessLogMapper businessLogMapper;

    @Override
    public BusinessLog save(BusinessLog log) {
        BusinessLogJpaEntity entity = businessLogMapper.toEntity(log);
        BusinessLogJpaEntity savedEntity = businessLogRepository.save(entity);
        return businessLogMapper.toDomain(savedEntity);
    }

    @Override
    public List<BusinessLog> saveAll(List<BusinessLog> logs) {
        List<BusinessLogJpaEntity> entities = logs.stream()
                .map(businessLogMapper::toEntity)
                .collect(Collectors.toList());
        List<BusinessLogJpaEntity> savedEntities = businessLogRepository.saveAll(entities);
        return savedEntities.stream()
                .map(businessLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long logId) {
        businessLogRepository.deleteById(logId);
    }

    @Override
    public int deleteLogsBefore(LocalDateTime dateTime) {
        List<BusinessLogJpaEntity> logsToDelete = businessLogRepository.findByTimestampBefore(dateTime);
        int count = logsToDelete.size();
        businessLogRepository.deleteAll(logsToDelete);
        return count;
    }

    @Override
    public int deleteLogsByAction(String action) {
        List<BusinessLogJpaEntity> logsToDelete = businessLogRepository.findByAction(action);
        int count = logsToDelete.size();
        businessLogRepository.deleteAll(logsToDelete);
        return count;
    }

    @Override
    public int deleteLogsByEntityType(String entityType) {
        List<BusinessLogJpaEntity> logsToDelete = businessLogRepository.findByEntityType(entityType);
        int count = logsToDelete.size();
        businessLogRepository.deleteAll(logsToDelete);
        return count;
    }

    @Override
    public BusinessLog logAction(String action, String entityType, String entityId, String userId, String details) {
        BusinessLogJpaEntity entity = BusinessLogJpaEntity.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        BusinessLogJpaEntity savedEntity = businessLogRepository.save(entity);
        return businessLogMapper.toDomain(savedEntity);
    }

    @Override
    public BusinessLog update(BusinessLog log) {
        // Check if the log exists
        businessLogRepository.findById(log.getId())
                .orElseThrow(() -> new NoSuchElementException("비즈니스 로그 정보를 찾을 수 없습니다. ID: " + log.getId()));

        BusinessLogJpaEntity entity = businessLogMapper.toEntity(log);
        BusinessLogJpaEntity savedEntity = businessLogRepository.save(entity);
        return businessLogMapper.toDomain(savedEntity);
    }
}