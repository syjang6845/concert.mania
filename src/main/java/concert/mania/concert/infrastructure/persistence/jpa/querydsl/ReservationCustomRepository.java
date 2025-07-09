package concert.mania.concert.infrastructure.persistence.jpa.querydsl;

import concert.mania.concert.domain.model.type.ReservationStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 예매 엔티티에 대한 사용자 정의 쿼리 인터페이스
 * QueryDSL을 사용한 동적 쿼리 메서드를 정의합니다.
 */
public interface ReservationCustomRepository {
    
    /**
     * 다양한 조건으로 예매를 검색합니다.
     * 
     * @param userId 사용자 ID (선택적)
     * @param concertId 콘서트 ID (선택적)
     * @param status 예매 상태 (선택적)
     * @param fromDate 예매 생성일 범위 시작 (선택적)
     * @param toDate 예매 생성일 범위 종료 (선택적)
     * @param minAmount 최소 결제 금액 (선택적)
     * @param maxAmount 최대 결제 금액 (선택적)
     * @param pageable 페이지 정보
     * @return 조건에 맞는 예매 목록 (페이지네이션)
     */
    Page<ReservationJpaEntity> searchReservations(
            Long userId,
            Long concertId,
            ReservationStatus status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable);
    
    /**
     * 특정 기간 동안의 일별 예매 통계를 조회합니다.
     * 
     * @param fromDate 시작일
     * @param toDate 종료일
     * @return 일별 예매 수 맵 (날짜 -> 예매 수)
     */
    Map<LocalDateTime, Long> getDailyReservationStatistics(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * 특정 기간 동안의 콘서트별 예매 통계를 조회합니다.
     * 
     * @param fromDate 시작일
     * @param toDate 종료일
     * @return 콘서트별 예매 수 맵 (콘서트 ID -> 예매 수)
     */
    Map<Long, Long> getConcertReservationStatistics(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * 특정 사용자의 최근 예매 내역을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param limit 조회할 예매 수
     * @return 최근 예매 목록
     */
    List<ReservationJpaEntity> findRecentReservationsByUser(Long userId, int limit);
    
    /**
     * 특정 콘서트의 시간대별 예매 통계를 조회합니다.
     * 
     * @param concertId 콘서트 ID
     * @return 시간대별 예매 수 맵 (시간 -> 예매 수)
     */
    Map<Integer, Long> getHourlyReservationStatisticsByConcert(Long concertId);
}