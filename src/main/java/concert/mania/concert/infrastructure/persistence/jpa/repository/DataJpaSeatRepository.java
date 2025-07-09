package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.domain.model.type.SeatStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatGradeJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.SeatCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

/**
 * 좌석 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaSeatRepository extends JpaRepository<SeatJpaEntity, Long>, QueryByExampleExecutor<SeatJpaEntity>, SeatCustomRepository {
    
    /**
     * 특정 콘서트의 모든 좌석 조회
     * 
     * @param concert 콘서트 엔티티
     * @return 해당 콘서트의 모든 좌석 목록
     */
    List<SeatJpaEntity> findByConcert(ConcertJpaEntity concert);


    /**
     * 특정 콘서트의 특정 상태인 좌석 조회
     * 
     * @param concert 콘서트 엔티티
     * @param status 좌석 상태
     * @return 해당 콘서트의 특정 상태인 좌석 목록
     */
    List<SeatJpaEntity> findByConcertAndStatus(ConcertJpaEntity concert, SeatStatus status);
    
    /**
     * 특정 좌석 등급의 모든 좌석 조회
     * 
     * @param seatGrade 좌석 등급 엔티티
     * @return 해당 등급의 모든 좌석 목록
     */
    List<SeatJpaEntity> findBySeatGrade(SeatGradeJpaEntity seatGrade);
    
    /**
     * 특정 좌석 등급의 특정 상태인 좌석 조회
     * 
     * @param seatGrade 좌석 등급 엔티티
     * @param status 좌석 상태
     * @return 해당 등급의 특정 상태인 좌석 목록
     */
    List<SeatJpaEntity> findBySeatGradeAndStatus(SeatGradeJpaEntity seatGrade, SeatStatus status);
    
    /**
     * 특정 콘서트의 특정 좌석 번호 조회
     * 
     * @param concert 콘서트 엔티티
     * @param seatNumber 좌석 번호
     * @return 해당 콘서트의 특정 좌석 번호에 해당하는 좌석
     */
    SeatJpaEntity findByConcertAndSeatNumber(ConcertJpaEntity concert, String seatNumber);
    
    /**
     * 특정 콘서트의 예매 가능한 좌석 수 조회
     * 
     * @param concertId 콘서트 ID
     * @return 예매 가능한 좌석 수
     */
    @Query("SELECT COUNT(s) FROM SeatJpaEntity s WHERE s.concert.id = :concertId AND s.status = 'AVAILABLE'")
    long countAvailableSeatsByConcertId(@Param("concertId") Long concertId);
    
    /**
     * 특정 콘서트의 특정 등급의 예매 가능한 좌석 수 조회
     * 
     * @param concertId 콘서트 ID
     * @param seatGradeId 좌석 등급 ID
     * @return 예매 가능한 좌석 수
     */
    @Query("SELECT COUNT(s) FROM SeatJpaEntity s WHERE s.concert.id = :concertId AND s.seatGrade.id = :seatGradeId AND s.status = 'AVAILABLE'")
    long countAvailableSeatsByConcertIdAndSeatGradeId(
            @Param("concertId") Long concertId,
            @Param("seatGradeId") Long seatGradeId);
}