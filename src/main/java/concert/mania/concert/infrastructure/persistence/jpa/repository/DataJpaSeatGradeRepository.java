package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatGradeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 좌석 등급 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaSeatGradeRepository extends JpaRepository<SeatGradeJpaEntity, Long> {
    
    /**
     * 특정 콘서트의 모든 좌석 등급 조회
     * 
     * @param concert 콘서트 엔티티
     * @return 해당 콘서트의 모든 좌석 등급 목록
     */
    List<SeatGradeJpaEntity> findByConcert(ConcertJpaEntity concert);
    
    /**
     * 특정 콘서트의 모든 좌석 등급을 가격 오름차순으로 조회
     * 
     * @param concert 콘서트 엔티티
     * @return 가격 오름차순으로 정렬된 좌석 등급 목록
     */
    List<SeatGradeJpaEntity> findByConcertOrderByPriceAsc(ConcertJpaEntity concert);
    
    /**
     * 특정 콘서트의 모든 좌석 등급을 가격 내림차순으로 조회
     * 
     * @param concert 콘서트 엔티티
     * @return 가격 내림차순으로 정렬된 좌석 등급 목록
     */
    List<SeatGradeJpaEntity> findByConcertOrderByPriceDesc(ConcertJpaEntity concert);
    
    /**
     * 특정 콘서트의 특정 가격 범위 내 좌석 등급 조회
     * 
     * @param concert 콘서트 엔티티
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 범위 내 좌석 등급 목록
     */
    List<SeatGradeJpaEntity> findByConcertAndPriceBetween(
            ConcertJpaEntity concert, 
            BigDecimal minPrice, 
            BigDecimal maxPrice);
    
    /**
     * 특정 콘서트의 특정 이름의 좌석 등급 조회
     * 
     * @param concert 콘서트 엔티티
     * @param name 좌석 등급 이름
     * @return 해당 이름의 좌석 등급
     */
    SeatGradeJpaEntity findByConcertAndName(ConcertJpaEntity concert, String name);
    
    /**
     * 특정 콘서트의 좌석 등급별 남은 좌석 수 조회
     * 
     * @param concertId 콘서트 ID
     * @return 좌석 등급 ID와 남은 좌석 수의 배열
     */
    @Query("SELECT sg.id, COUNT(s) FROM SeatJpaEntity s " +
           "JOIN s.seatGrade sg " +
           "WHERE s.concert.id = :concertId AND s.status = 'AVAILABLE' " +
           "GROUP BY sg.id")
    List<Object[]> countAvailableSeatsBySeatGrade(@Param("concertId") Long concertId);
}