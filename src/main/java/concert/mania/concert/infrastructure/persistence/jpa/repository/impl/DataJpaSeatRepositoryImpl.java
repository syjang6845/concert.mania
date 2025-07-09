package concert.mania.concert.infrastructure.persistence.jpa.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import concert.mania.concert.domain.model.type.SeatStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QSeatGradeJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.QSeatJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.SeatCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static concert.mania.concert.infrastructure.persistence.jpa.entity.QConcertJpaEntity.*;
import static concert.mania.concert.infrastructure.persistence.jpa.entity.QSeatGradeJpaEntity.*;
import static concert.mania.concert.infrastructure.persistence.jpa.entity.QSeatJpaEntity.seatJpaEntity;

/**
 * SeatCustomRepository 인터페이스의 구현체
 * QueryDSL을 사용하여 동적 쿼리를 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class DataJpaSeatRepositoryImpl implements SeatCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SeatJpaEntity> findSeatsByConcertIdAndSeatGradeId(Long concertId, Long seatGradeId) {
        return queryFactory.selectFrom(seatJpaEntity)
                .join(seatJpaEntity.seatGrade, seatGradeJpaEntity).fetchJoin()
                .join(seatJpaEntity.concert, concertJpaEntity).fetchJoin()
                .where(seatJpaEntity.concert.id.eq(concertId)
                        .and(seatJpaEntity.seatGrade.id.eq(seatGradeId)))
                .fetch();
    }

    @Override
    public List<SeatJpaEntity> searchSeats(
            Long concertId,
            Long seatGradeId,
            List<SeatStatus> statuses,
            Integer rowNumber,
            Integer columnNumber) {
        
        QSeatJpaEntity seat = seatJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();
        
        // 콘서트 ID는 필수 조건
        builder.and(seat.concert.id.eq(concertId));
        
        // 선택적 조건들
        if (seatGradeId != null) {
            builder.and(seat.seatGrade.id.eq(seatGradeId));
        }
        
        if (statuses != null && !statuses.isEmpty()) {
            builder.and(seat.status.in(statuses));
        }
        
        if (rowNumber != null) {
            builder.and(seat.seatRow.eq(rowNumber));
        }
        
        if (columnNumber != null) {
            builder.and(seat.seatCol.eq(columnNumber));
        }
        
        return queryFactory
                .selectFrom(seat)
                .where(builder)
                .orderBy(seat.seatRow.asc(), seat.seatCol.asc())
                .fetch();
    }
    
    @Override
    public Map<SeatStatus, Long> getSeatStatusStatistics(Long concertId) {
        QSeatJpaEntity seat = seatJpaEntity;
        
        List<Tuple> results = queryFactory
                .select(seat.status, seat.count())
                .from(seat)
                .where(seat.concert.id.eq(concertId))
                .groupBy(seat.status)
                .fetch();
        
        Map<SeatStatus, Long> statistics = new HashMap<>();
        for (Tuple tuple : results) {
            statistics.put(tuple.get(seat.status), tuple.get(seat.count()));
        }
        
        return statistics;
    }
    
    @Override
    public Map<Long, Map<SeatStatus, Long>> getSeatStatusStatisticsByGrade(Long concertId) {
        QSeatJpaEntity seat = seatJpaEntity;
        QSeatGradeJpaEntity seatGrade = seatGradeJpaEntity;
        
        List<Tuple> results = queryFactory
                .select(seatGrade.id, seat.status, seat.count())
                .from(seat)
                .join(seat.seatGrade, seatGrade)
                .where(seat.concert.id.eq(concertId))
                .groupBy(seatGrade.id, seat.status)
                .fetch();
        
        Map<Long, Map<SeatStatus, Long>> statistics = new HashMap<>();
        
        for (Tuple tuple : results) {
            Long gradeId = tuple.get(seatGrade.id);
            SeatStatus status = tuple.get(seat.status);
            Long count = tuple.get(seat.count());
            
            statistics.computeIfAbsent(gradeId, k -> new HashMap<>())
                    .put(status, count);
        }
        
        return statistics;
    }
    
    @Override
    public List<SeatJpaEntity> findAdjacentAvailableSeats(Long concertId, Long seatGradeId, int count) {
        QSeatJpaEntity seat = seatJpaEntity;
        
        // 먼저 사용 가능한 모든 좌석을 행과 열 순서로 정렬하여 가져옴
        List<SeatJpaEntity> availableSeats = queryFactory
                .selectFrom(seat)
                .where(seat.concert.id.eq(concertId)
                        .and(seat.seatGrade.id.eq(seatGradeId))
                        .and(seat.status.eq(SeatStatus.AVAILABLE)))
                .orderBy(seat.seatRow.asc(), seat.seatCol.asc())
                .fetch();
        
        // 인접한 좌석 찾기
        List<SeatJpaEntity> adjacentSeats = findAdjacentSeatsInRow(availableSeats, count);
        
        return adjacentSeats;
    }
    
    /**
     * 같은 행에서 인접한 좌석을 찾는 헬퍼 메서드
     */
    private List<SeatJpaEntity> findAdjacentSeatsInRow(List<SeatJpaEntity> availableSeats, int count) {
        if (availableSeats.size() < count) {
            return new ArrayList<>();
        }
        
        // 행별로 좌석 그룹화
        Map<Integer, List<SeatJpaEntity>> seatsByRow = availableSeats.stream()
                .collect(Collectors.groupingBy(SeatJpaEntity::getSeatRow));
        
        // 각 행에서 인접한 좌석 찾기
        for (List<SeatJpaEntity> rowSeats : seatsByRow.values()) {
            if (rowSeats.size() < count) {
                continue;
            }
            
            // 열 번호로 정렬
            rowSeats.sort((s1, s2) -> s1.getSeatCol().compareTo(s2.getSeatCol()));
            
            // 인접한 좌석 찾기
            for (int i = 0; i <= rowSeats.size() - count; i++) {
                boolean adjacent = true;
                for (int j = 0; j < count - 1; j++) {
                    if (rowSeats.get(i + j).getSeatCol() + 1 != rowSeats.get(i + j + 1).getSeatCol()) {
                        adjacent = false;
                        break;
                    }
                }
                
                if (adjacent) {
                    return rowSeats.subList(i, i + count);
                }
            }
        }
        
        return new ArrayList<>();
    }
}