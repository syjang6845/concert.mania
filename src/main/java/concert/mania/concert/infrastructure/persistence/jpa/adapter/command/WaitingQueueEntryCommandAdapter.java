package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.WaitingQueueEntryCommandPort;
import concert.mania.concert.domain.model.WaitingQueueEntry;
import concert.mania.concert.domain.model.type.QueueStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.WaitingQueueEntryJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaWaitingQueueEntryRepository;
import concert.mania.concert.infrastructure.persistence.mapper.WaitingQueueEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 대기열 항목 명령 영속성 어댑터
 * 대기열 항목 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class WaitingQueueEntryCommandAdapter implements WaitingQueueEntryCommandPort {

    private final DataJpaWaitingQueueEntryRepository waitingQueueEntryRepository;
    private final WaitingQueueEntryMapper waitingQueueEntryMapper;

    @Override
    public WaitingQueueEntry save(WaitingQueueEntry waitingQueueEntry) {
        WaitingQueueEntryJpaEntity entity = waitingQueueEntryMapper.toEntity(waitingQueueEntry);
        WaitingQueueEntryJpaEntity savedEntity = waitingQueueEntryRepository.save(entity);
        return waitingQueueEntryMapper.toDomain(savedEntity);
    }

    @Override
    public List<WaitingQueueEntry> saveAll(List<WaitingQueueEntry> waitingQueueEntries) {
        List<WaitingQueueEntryJpaEntity> entities = waitingQueueEntries.stream()
                .map(waitingQueueEntryMapper::toEntity)
                .collect(Collectors.toList());
        List<WaitingQueueEntryJpaEntity> savedEntities = waitingQueueEntryRepository.saveAll(entities);
        return savedEntities.stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long waitingQueueEntryId) {
        waitingQueueEntryRepository.deleteById(waitingQueueEntryId);
    }

    @Override
    public int updateStatusByConcertId(Long concertId, QueueStatus fromStatus, QueueStatus toStatus) {
        return waitingQueueEntryRepository.updateStatusByConcertId(concertId, fromStatus, toStatus);
    }

    @Override
    public WaitingQueueEntry admit(Long waitingQueueEntryId) {
        WaitingQueueEntryJpaEntity entity = waitingQueueEntryRepository.findById(waitingQueueEntryId)
                .orElseThrow(() -> new IllegalArgumentException("대기열 항목을 찾을 수 없습니다: " + waitingQueueEntryId));

        if (entity.getStatus() != QueueStatus.WAITING) {
            throw new IllegalStateException("대기 중인 항목만 입장 허용할 수 있습니다.");
        }

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        WaitingQueueEntryJpaEntity updatedEntity = WaitingQueueEntryJpaEntity.builder()
                .id(entity.getId())
                .concert(entity.getConcert())
                .userId(entity.getUserId())
                .queuePosition(entity.getQueuePosition())
                .enteredAt(entity.getEnteredAt())
                .admittedAt(LocalDateTime.now())
                .status(QueueStatus.ADMITTED)
                .build();

        WaitingQueueEntryJpaEntity savedEntity = waitingQueueEntryRepository.save(updatedEntity);
        return waitingQueueEntryMapper.toDomain(savedEntity);
    }

    @Override
    public WaitingQueueEntry expire(Long waitingQueueEntryId) {
        WaitingQueueEntryJpaEntity entity = waitingQueueEntryRepository.findById(waitingQueueEntryId)
                .orElseThrow(() -> new IllegalArgumentException("대기열 항목을 찾을 수 없습니다: " + waitingQueueEntryId));

        if (entity.getStatus() != QueueStatus.WAITING && entity.getStatus() != QueueStatus.ADMITTED) {
            throw new IllegalStateException("대기 중이거나 입장 허용된 항목만 만료 처리할 수 있습니다.");
        }

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        WaitingQueueEntryJpaEntity updatedEntity = WaitingQueueEntryJpaEntity.builder()
                .id(entity.getId())
                .concert(entity.getConcert())
                .userId(entity.getUserId())
                .queuePosition(entity.getQueuePosition())
                .enteredAt(entity.getEnteredAt())
                .admittedAt(entity.getAdmittedAt())
                .status(QueueStatus.EXPIRED)
                .build();

        WaitingQueueEntryJpaEntity savedEntity = waitingQueueEntryRepository.save(updatedEntity);
        return waitingQueueEntryMapper.toDomain(savedEntity);
    }

    @Override
    public WaitingQueueEntry cancel(Long waitingQueueEntryId) {
        WaitingQueueEntryJpaEntity entity = waitingQueueEntryRepository.findById(waitingQueueEntryId)
                .orElseThrow(() -> new IllegalArgumentException("대기열 항목을 찾을 수 없습니다: " + waitingQueueEntryId));

        if (entity.getStatus() == QueueStatus.CANCELLED || entity.getStatus() == QueueStatus.EXPIRED) {
            throw new IllegalStateException("이미 취소되었거나 만료된 항목입니다.");
        }

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        WaitingQueueEntryJpaEntity updatedEntity = WaitingQueueEntryJpaEntity.builder()
                .id(entity.getId())
                .concert(entity.getConcert())
                .userId(entity.getUserId())
                .queuePosition(entity.getQueuePosition())
                .enteredAt(entity.getEnteredAt())
                .admittedAt(entity.getAdmittedAt())
                .status(QueueStatus.CANCELLED)
                .build();

        WaitingQueueEntryJpaEntity savedEntity = waitingQueueEntryRepository.save(updatedEntity);
        return waitingQueueEntryMapper.toDomain(savedEntity);
    }
}