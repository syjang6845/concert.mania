package concert.mania.concert.infrastructure.persistence.jpa.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import concert.mania.concert.domain.model.type.QueueStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QWaitingQueueEntryJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.WaitingQueueEntryJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.WaitingQueueEntryCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WaitingQueueEntryCustomRepository 인터페이스의 QueryDSL 구현체
 */
@Repository
@RequiredArgsConstructor
public class WaitingQueueEntryCustomRepositoryImpl implements WaitingQueueEntryCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QWaitingQueueEntryJpaEntity qWaitingQueueEntry = QWaitingQueueEntryJpaEntity.waitingQueueEntryJpaEntity;

    @Override
    public List<WaitingQueueEntryJpaEntity> findByConcertIdAndStatusesAndEnteredAtRange(
            Long concertId, 
            List<QueueStatus> statuses, 
            LocalDateTime enteredAfter, 
            LocalDateTime enteredBefore) {
        
        return queryFactory
                .selectFrom(qWaitingQueueEntry)
                .where(
                    concertIdEq(concertId),
                    statusIn(statuses),
                    enteredAtAfter(enteredAfter),
                    enteredAtBefore(enteredBefore)
                )
                .orderBy(qWaitingQueueEntry.queuePosition.asc())
                .fetch();
    }

    @Override
    public List<WaitingQueueEntryJpaEntity> findByConcertIdAndStatusesAndAdmittedAtRange(
            Long concertId, 
            List<QueueStatus> statuses, 
            LocalDateTime admittedAfter, 
            LocalDateTime admittedBefore) {
        
        return queryFactory
                .selectFrom(qWaitingQueueEntry)
                .where(
                    concertIdEq(concertId),
                    statusIn(statuses),
                    admittedAtAfter(admittedAfter),
                    admittedAtBefore(admittedBefore)
                )
                .orderBy(qWaitingQueueEntry.queuePosition.asc())
                .fetch();
    }

    @Override
    public List<WaitingQueueEntryJpaEntity> findByConcertIdAndQueuePositionRange(
            Long concertId, 
            Integer fromPosition, 
            Integer toPosition) {
        
        return queryFactory
                .selectFrom(qWaitingQueueEntry)
                .where(
                    concertIdEq(concertId),
                    queuePositionGoe(fromPosition),
                    queuePositionLoe(toPosition)
                )
                .orderBy(qWaitingQueueEntry.queuePosition.asc())
                .fetch();
    }

    @Override
    public List<WaitingQueueEntryJpaEntity> findNextBatchToAdmit(Long concertId, int batchSize) {
        return queryFactory
                .selectFrom(qWaitingQueueEntry)
                .where(
                    concertIdEq(concertId),
                    qWaitingQueueEntry.status.eq(QueueStatus.WAITING)
                )
                .orderBy(qWaitingQueueEntry.queuePosition.asc())
                .limit(batchSize)
                .fetch();
    }

    @Override
    public List<WaitingQueueEntryJpaEntity> findExpirableEntries(LocalDateTime dateTime) {
        return queryFactory
                .selectFrom(qWaitingQueueEntry)
                .where(
                    qWaitingQueueEntry.status.eq(QueueStatus.ADMITTED),
                    qWaitingQueueEntry.admittedAt.before(dateTime)
                )
                .fetch();
    }

    @Override
    public Map<QueueStatus, Long> getQueueStatistics(Long concertId) {
        Map<QueueStatus, Long> statistics = new HashMap<>();
        
        // 각 상태별로 개수 조회
        for (QueueStatus status : QueueStatus.values()) {
            Long count = queryFactory
                    .select(qWaitingQueueEntry.count())
                    .from(qWaitingQueueEntry)
                    .where(
                        concertIdEq(concertId),
                        qWaitingQueueEntry.status.eq(status)
                    )
                    .fetchOne();
            
            statistics.put(status, count != null ? count : 0L);
        }
        
        return statistics;
    }

    // 조건 메서드들
    private BooleanExpression concertIdEq(Long concertId) {
        return concertId != null ? qWaitingQueueEntry.concert.id.eq(concertId) : null;
    }

    private BooleanExpression statusIn(List<QueueStatus> statuses) {
        return statuses != null && !statuses.isEmpty() ? qWaitingQueueEntry.status.in(statuses) : null;
    }

    private BooleanExpression enteredAtAfter(LocalDateTime dateTime) {
        return dateTime != null ? qWaitingQueueEntry.enteredAt.after(dateTime) : null;
    }

    private BooleanExpression enteredAtBefore(LocalDateTime dateTime) {
        return dateTime != null ? qWaitingQueueEntry.enteredAt.before(dateTime) : null;
    }

    private BooleanExpression admittedAtAfter(LocalDateTime dateTime) {
        return dateTime != null ? qWaitingQueueEntry.admittedAt.after(dateTime) : null;
    }

    private BooleanExpression admittedAtBefore(LocalDateTime dateTime) {
        return dateTime != null ? qWaitingQueueEntry.admittedAt.before(dateTime) : null;
    }

    private BooleanExpression queuePositionGoe(Integer position) {
        return position != null ? qWaitingQueueEntry.queuePosition.goe(position) : null;
    }

    private BooleanExpression queuePositionLoe(Integer position) {
        return position != null ? qWaitingQueueEntry.queuePosition.loe(position) : null;
    }
}