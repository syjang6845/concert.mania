package concert.mania.concert.infrastructure.persistence.jpa.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import concert.mania.concert.domain.model.type.PaymentMethod;
import concert.mania.concert.domain.model.type.PaymentStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.PaymentJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QPaymentJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QReservationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.PaymentCustomRepository;
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

/**
 * PaymentCustomRepository 인터페이스의 구현체
 * QueryDSL을 사용하여 동적 쿼리를 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<PaymentJpaEntity> searchPayments(
            Long userId,
            Long concertId,
            PaymentStatus status,
            PaymentMethod method,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable) {
        
        QPaymentJpaEntity payment = QPaymentJpaEntity.paymentJpaEntity;
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();
        
        // 기본 조인 조건
        builder.and(payment.reservation.eq(reservation));
        
        // 선택적 조건들
        if (userId != null) {
            builder.and(reservation.userId.eq(userId));
        }
        
        if (concertId != null) {
            builder.and(reservation.concert.id.eq(concertId));
        }
        
        if (status != null) {
            builder.and(payment.status.eq(status));
        }
        
        if (method != null) {
            builder.and(payment.method.eq(method));
        }
        
        if (fromDate != null) {
            builder.and(payment.createdAt.goe(fromDate));
        }
        
        if (toDate != null) {
            builder.and(payment.createdAt.loe(toDate));
        }
        
        if (minAmount != null) {
            builder.and(payment.amount.goe(minAmount));
        }
        
        if (maxAmount != null) {
            builder.and(payment.amount.loe(maxAmount));
        }
        
        // 전체 카운트 쿼리
        long total = queryFactory
                .selectFrom(payment)
                .join(payment.reservation, reservation)
                .where(builder)
                .fetchCount();
        
        // 페이지네이션 적용 쿼리
        List<PaymentJpaEntity> results = queryFactory
                .selectFrom(payment)
                .join(payment.reservation, reservation)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(payment.createdAt.desc())
                .fetch();
        
        return new PageImpl<>(results, pageable, total);
    }
    
    @Override
    public Map<PaymentMethod, BigDecimal> getPaymentMethodStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        QPaymentJpaEntity payment = QPaymentJpaEntity.paymentJpaEntity;
        
        List<Tuple> results = queryFactory
                .select(payment.method, payment.amount.sum())
                .from(payment)
                .where(payment.completedAt.between(fromDate, toDate)
                        .and(payment.status.eq(PaymentStatus.COMPLETED)))
                .groupBy(payment.method)
                .fetch();
        
        Map<PaymentMethod, BigDecimal> statistics = new HashMap<>();
        for (Tuple tuple : results) {
            statistics.put(tuple.get(payment.method), tuple.get(payment.amount.sum()));
        }
        
        return statistics;
    }
    
    @Override
    public Map<LocalDateTime, BigDecimal> getDailyPaymentStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        QPaymentJpaEntity payment = QPaymentJpaEntity.paymentJpaEntity;
        
        // 날짜 부분만 추출하는 표현식 (시간 부분은 제거)
        List<Tuple> results = queryFactory
                .select(
                        Expressions.dateTemplate(LocalDateTime.class, "DATE_TRUNC('day', {0})", payment.completedAt),
                        payment.amount.sum()
                )
                .from(payment)
                .where(payment.completedAt.between(fromDate, toDate)
                        .and(payment.status.eq(PaymentStatus.COMPLETED)))
                .groupBy(Expressions.dateTemplate(LocalDateTime.class, "DATE_TRUNC('day', {0})", payment.completedAt))
                .orderBy(Expressions.dateTemplate(LocalDateTime.class, "DATE_TRUNC('day', {0})", payment.completedAt).asc())
                .fetch();
        
        Map<LocalDateTime, BigDecimal> statistics = new HashMap<>();
        for (Tuple tuple : results) {
            statistics.put(tuple.get(0, LocalDateTime.class), tuple.get(1, BigDecimal.class));
        }
        
        return statistics;
    }
    
    @Override
    public Map<Long, BigDecimal> getConcertPaymentStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        QPaymentJpaEntity payment = QPaymentJpaEntity.paymentJpaEntity;
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        QConcertJpaEntity concert = QConcertJpaEntity.concertJpaEntity;
        
        List<Tuple> results = queryFactory
                .select(concert.id, payment.amount.sum())
                .from(payment)
                .join(payment.reservation, reservation)
                .join(reservation.concert, concert)
                .where(payment.completedAt.between(fromDate, toDate)
                        .and(payment.status.eq(PaymentStatus.COMPLETED)))
                .groupBy(concert.id)
                .orderBy(payment.amount.sum().desc())
                .fetch();
        
        Map<Long, BigDecimal> statistics = new HashMap<>();
        for (Tuple tuple : results) {
            statistics.put(tuple.get(concert.id), tuple.get(payment.amount.sum()));
        }
        
        return statistics;
    }
    
    @Override
    public List<PaymentJpaEntity> findPaymentsByUser(Long userId, PaymentStatus status, int limit) {
        QPaymentJpaEntity payment = QPaymentJpaEntity.paymentJpaEntity;
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(reservation.userId.eq(userId));
        
        if (status != null) {
            builder.and(payment.status.eq(status));
        }
        
        return queryFactory
                .selectFrom(payment)
                .join(payment.reservation, reservation)
                .where(builder)
                .orderBy(payment.createdAt.desc())
                .limit(limit)
                .fetch();
    }
}