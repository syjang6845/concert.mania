package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.WaitingQueueEntryQueryPort;
import concert.mania.concert.domain.model.WaitingQueueEntry;
import concert.mania.concert.domain.model.type.QueueStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.WaitingQueueEntryCustomRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaConcertRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaWaitingQueueEntryRepository;
import concert.mania.concert.infrastructure.persistence.mapper.WaitingQueueEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 대기열 항목 조회 영속성 어댑터
 * 대기열 항목 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaitingQueueEntryQueryAdapter implements WaitingQueueEntryQueryPort {

    private final DataJpaWaitingQueueEntryRepository waitingQueueEntryRepository;
    private final WaitingQueueEntryCustomRepository waitingQueueEntryCustomRepository;
    private final DataJpaConcertRepository concertRepository;
    private final WaitingQueueEntryMapper waitingQueueEntryMapper;

    @Override
    public Optional<WaitingQueueEntry> findById(Long id) {
        return waitingQueueEntryRepository.findById(id)
                .map(waitingQueueEntryMapper::toDomain);
    }

    @Override
    public List<WaitingQueueEntry> findByConcertId(Long concertId) {
        ConcertJpaEntity concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다: " + concertId));

        return waitingQueueEntryRepository.findByConcert(concert).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WaitingQueueEntry> findByConcertIdAndStatus(Long concertId, QueueStatus status) {
        ConcertJpaEntity concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다: " + concertId));

        return waitingQueueEntryRepository.findByConcertAndStatus(concert, status).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WaitingQueueEntry> findByUserIdAndConcertId(Long userId, Long concertId) {
        ConcertJpaEntity concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다: " + concertId));

        return waitingQueueEntryRepository.findByUserIdAndConcert(userId, concert)
                .map(waitingQueueEntryMapper::toDomain);
    }

    @Override
    public List<WaitingQueueEntry> findByConcertIdOrderByQueuePosition(Long concertId) {
        ConcertJpaEntity concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다: " + concertId));

        return waitingQueueEntryRepository.findByConcertOrderByQueuePositionAsc(concert).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WaitingQueueEntry> findByConcertIdAndStatusOrderByQueuePosition(Long concertId, QueueStatus status) {
        ConcertJpaEntity concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다: " + concertId));

        return waitingQueueEntryRepository.findByConcertAndStatusOrderByQueuePositionAsc(concert, status).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByConcertId(Long concertId) {
        return waitingQueueEntryRepository.countByConcertId(concertId);
    }

    @Override
    public long countByConcertIdAndStatus(Long concertId, QueueStatus status) {
        return waitingQueueEntryRepository.countByConcertIdAndStatus(concertId, status);
    }

    @Override
    public long countQueueAhead(Long concertId, Integer queuePosition) {
        return waitingQueueEntryRepository.countQueueAhead(concertId, queuePosition);
    }

    @Override
    public List<WaitingQueueEntry> findByConcertIdAndStatusesAndEnteredAtRange(
            Long concertId, 
            List<QueueStatus> statuses, 
            LocalDateTime enteredAfter, 
            LocalDateTime enteredBefore) {

        return waitingQueueEntryCustomRepository.findByConcertIdAndStatusesAndEnteredAtRange(
                concertId, statuses, enteredAfter, enteredBefore).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WaitingQueueEntry> findByConcertIdAndStatusesAndAdmittedAtRange(
            Long concertId, 
            List<QueueStatus> statuses, 
            LocalDateTime admittedAfter, 
            LocalDateTime admittedBefore) {

        return waitingQueueEntryCustomRepository.findByConcertIdAndStatusesAndAdmittedAtRange(
                concertId, statuses, admittedAfter, admittedBefore).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WaitingQueueEntry> findByConcertIdAndQueuePositionRange(
            Long concertId, 
            Integer fromPosition, 
            Integer toPosition) {

        return waitingQueueEntryCustomRepository.findByConcertIdAndQueuePositionRange(
                concertId, fromPosition, toPosition).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WaitingQueueEntry> findNextBatchToAdmit(Long concertId, int batchSize) {
        return waitingQueueEntryCustomRepository.findNextBatchToAdmit(concertId, batchSize).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WaitingQueueEntry> findExpirableEntries(LocalDateTime dateTime) {
        return waitingQueueEntryCustomRepository.findExpirableEntries(dateTime).stream()
                .map(waitingQueueEntryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Map<QueueStatus, Long> getQueueStatistics(Long concertId) {
        return waitingQueueEntryCustomRepository.getQueueStatistics(concertId);
    }
}