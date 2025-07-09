package concert.mania.concert.infrastructure.persistence.jpa.querydsl;

import concert.mania.concert.domain.model.type.SeatStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * 좌석 엔티티에 대한 사용자 정의 쿼리 인터페이스
 * QueryDSL을 사용한 동적 쿼리 메서드를 정의합니다.
 */
public interface SeatCustomRepository {

    List<SeatJpaEntity> findSeatsByConcertIdAndSeatGradeId(@Param("concertId") Long concertId, @Param("seatGradeId") Long seatGradeId);
    /**
     * 특정 콘서트의 좌석을 다양한 조건으로 검색합니다.
     * 
     * @param concertId 콘서트 ID
     * @param seatGradeId 좌석 등급 ID (선택적)
     * @param statuses 좌석 상태 목록 (선택적)
     * @param rowNumber 행 번호 (선택적)
     * @param columnNumber 열 번호 (선택적)
     * @return 조건에 맞는 좌석 목록
     */
    List<SeatJpaEntity> searchSeats(
            Long concertId,
            Long seatGradeId,
            List<SeatStatus> statuses,
            Integer rowNumber,
            Integer columnNumber);
    
    /**
     * 특정 콘서트의 좌석 상태 통계를 조회합니다.
     * 
     * @param concertId 콘서트 ID
     * @return 상태별 좌석 수 맵 (상태 -> 좌석 수)
     */
    Map<SeatStatus, Long> getSeatStatusStatistics(Long concertId);
    
    /**
     * 특정 콘서트의 좌석 등급별 상태 통계를 조회합니다.
     * 
     * @param concertId 콘서트 ID
     * @return 등급별 상태별 좌석 수 맵 (등급 ID -> (상태 -> 좌석 수))
     */
    Map<Long, Map<SeatStatus, Long>> getSeatStatusStatisticsByGrade(Long concertId);
    
    /**
     * 특정 콘서트의 인접한 좌석을 검색합니다.
     * 
     * @param concertId 콘서트 ID
     * @param seatGradeId 좌석 등급 ID
     * @param count 필요한 좌석 수
     * @return 인접한 좌석 목록 (가능한 경우)
     */
    List<SeatJpaEntity> findAdjacentAvailableSeats(Long concertId, Long seatGradeId, int count);
}