package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.SystemLogCommandPort;
import concert.mania.concert.domain.model.SystemLog;
import concert.mania.concert.domain.model.type.LogLevel;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SystemLogJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSystemLogRepository;
import concert.mania.concert.infrastructure.persistence.mapper.SystemLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static concert.mania.concert.domain.model.type.LogLevel.*;

/**
 * 시스템 로그 명령 영속성 어댑터
 * 시스템 로그 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class SystemLogCommandAdapter implements SystemLogCommandPort {

    private final DataJpaSystemLogRepository systemLogRepository;
    private final SystemLogMapper systemLogMapper;

    @Override
    public SystemLog save(SystemLog systemLog) {
        SystemLogJpaEntity entity = systemLogMapper.toEntity(systemLog);
        SystemLogJpaEntity savedEntity = systemLogRepository.save(entity);
        return systemLogMapper.toDomain(savedEntity);
    }

    @Override
    public List<SystemLog> saveAll(List<SystemLog> systemLogs) {
        List<SystemLogJpaEntity> entities = systemLogs.stream()
                .map(systemLogMapper::toEntity)
                .collect(Collectors.toList());
        List<SystemLogJpaEntity> savedEntities = systemLogRepository.saveAll(entities);
        return savedEntities.stream()
                .map(systemLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long systemLogId) {
        systemLogRepository.deleteById(systemLogId);
    }

    @Override
    public int deleteInfoLogsBefore(LocalDateTime dateTime) {
        return systemLogRepository.deleteInfoLogsBefore(dateTime);
    }

    @Override
    public int deleteLogsByLevelAndBefore(LogLevel logLevel, LocalDateTime dateTime) {
        return 0;
    }

    @Override
    public SystemLog logInfo(String logger, String message, String userId, String requestUri) {
        SystemLogJpaEntity entity = SystemLogJpaEntity.builder()
                .logLevel(INFO)
                .logger(logger)
                .message(message)
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .requestUri(requestUri)
                .build();
        SystemLogJpaEntity savedEntity = systemLogRepository.save(entity);
        return systemLogMapper.toDomain(savedEntity);
    }

    @Override
    public SystemLog logWarn(String logger, String message, String userId, String requestUri) {
        SystemLogJpaEntity entity = SystemLogJpaEntity.builder()
                .logLevel(WARN)
                .logger(logger)
                .message(message)
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .requestUri(requestUri)
                .build();
        SystemLogJpaEntity savedEntity = systemLogRepository.save(entity);
        return systemLogMapper.toDomain(savedEntity);
    }

    @Override
    public SystemLog logError(String logger, String message, String stackTrace, String userId, String requestUri) {
        SystemLogJpaEntity entity = SystemLogJpaEntity.builder()
                .logLevel(ERROR)
                .logger(logger)
                .message(message)
                .stackTrace(stackTrace)
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .requestUri(requestUri)
                .build();
        SystemLogJpaEntity savedEntity = systemLogRepository.save(entity);
        return systemLogMapper.toDomain(savedEntity);
    }

    @Override
    public SystemLog update(SystemLog systemLog) {
        // Check if the system log exists
        systemLogRepository.findById(systemLog.getId())
                .orElseThrow(() -> new NoSuchElementException("시스템 로그 정보를 찾을 수 없습니다. ID: " + systemLog.getId()));

        SystemLogJpaEntity entity = systemLogMapper.toEntity(systemLog);
        SystemLogJpaEntity savedEntity = systemLogRepository.save(entity);
        return systemLogMapper.toDomain(savedEntity);
    }
}