package concert.mania.concert.infrastructure.persistence.jpa.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import concert.mania.concert.domain.model.type.ReservationStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QReservationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.ReservationCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ReservationCustomRepository 인터페이스의 구현체
 * QueryDSL을 사용하여 동적 쿼리를 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<ReservationJpaEntity> searchReservations(
            Long userId,
            Long concertId,
            ReservationStatus status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable) {
        
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();
        
        // 선택적 조건들
        if (userId != null) {
            builder.and(reservation.userId.eq(userId));
        }
        
        if (concertId != null) {
            builder.and(reservation.concert.id.eq(concertId));
        }
        
        if (status != null) {
            builder.and(reservation.status.eq(status));
        }
        
        if (fromDate != null) {
            builder.and(reservation.createdAt.goe(fromDate));
        }
        
        if (toDate != null) {
            builder.and(reservation.createdAt.loe(toDate));
        }
        
        if (minAmount != null) {
            builder.and(reservation.totalAmount.goe(minAmount));
        }
        
        if (maxAmount != null) {
            builder.and(reservation.totalAmount.loe(maxAmount));
        }
        
        // 전체 카운트 쿼리
        long total = queryFactory
                .selectFrom(reservation)
                .where(builder)
                .fetchCount();
        
        // 페이지네이션 적용 쿼리
        List<ReservationJpaEntity> results = queryFactory
                .selectFrom(reservation)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(reservation.createdAt.desc())
                .fetch();
        
        return new PageImpl<>(results, pageable, total);
    }
    
    @Override
    public Map<LocalDateTime, Long> getDailyReservationStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        
        // 날짜 부분만 추출하는 표현식 (시간 부분은 제거)
        List<Tuple> results = queryFactory
                .select(
                        Expressions.dateTemplate(LocalDateTime.class, "DATE_TRUNC('day', {0})", reservation.createdAt),
                        reservation.count()
                )
                .from(reservation)
                .where(reservation.createdAt.between(fromDate, toDate)
                        .and(reservation.status.eq(ReservationStatus.COMPLETED)))
                .groupBy(Expressions.dateTemplate(LocalDateTime.class, "DATE_TRUNC('day', {0})", reservation.createdAt))
                .orderBy(Expressions.dateTemplate(LocalDateTime.class, "DATE_TRUNC('day', {0})", reservation.createdAt).asc())
                .fetch();
        
        Map<LocalDateTime, Long> statistics = new HashMap<>();
        for (Tuple tuple : results) {
            statistics.put(tuple.get(0, LocalDateTime.class), tuple.get(1, Long.class));
        }
        
        return statistics;
    }
    
    @Override
    public Map<Long, Long> getConcertReservationStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        QConcertJpaEntity concert = QConcertJpaEntity.concertJpaEntity;
        
        List<Tuple> results = queryFactory
                .select(concert.id, reservation.count())
                .from(reservation)
                .join(reservation.concert, concert)
                .where(reservation.createdAt.between(fromDate, toDate)
                        .and(reservation.status.eq(ReservationStatus.COMPLETED)))
                .groupBy(concert.id)
                .orderBy(reservation.count().desc())
                .fetch();
        
        Map<Long, Long> statistics = new HashMap<>();
        for (Tuple tuple : results) {
            statistics.put(tuple.get(concert.id), tuple.get(reservation.count()));
        }
        
        return statistics;
    }
    
    @Override
    public List<ReservationJpaEntity> findRecentReservationsByUser(Long userId, int limit) {
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.userId.eq(userId))
                .orderBy(reservation.createdAt.desc())
                .limit(limit)
                .fetch();
    }
    
    @Override
    public Map<Integer, Long> getHourlyReservationStatisticsByConcert(Long concertId) {
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        
        // 시간 부분만 추출하는 표현식
        List<Tuple> results = queryFactory
                .select(
                        Expressions.numberTemplate(Integer.class, "EXTRACT(HOUR FROM {0})", reservation.createdAt),
                        reservation.count()
                )
                .from(reservation)
                .where(reservation.concert.id.eq(concertId)
                        .and(reservation.status.eq(ReservationStatus.COMPLETED)))
                .groupBy(Expressions.numberTemplate(Integer.class, "EXTRACT(HOUR FROM {0})", reservation.createdAt))
                .orderBy(Expressions.numberTemplate(Integer.class, "EXTRACT(HOUR FROM {0})", reservation.createdAt).asc())
                .fetch();
        
        Map<Integer, Long> statistics = new HashMap<>();
        for (Tuple tuple : results) {
            statistics.put(tuple.get(0, Integer.class), tuple.get(1, Long.class));
        }
        
        return statistics;
    }
}