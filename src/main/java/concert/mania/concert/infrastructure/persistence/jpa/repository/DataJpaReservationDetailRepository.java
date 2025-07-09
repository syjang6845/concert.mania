package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationDetailJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 예매 상세 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaReservationDetailRepository extends JpaRepository<ReservationDetailJpaEntity, Long> {
    
    /**
     * 특정 예매의 모든 예매 상세 정보 조회
     * 
     * @param reservation 예매 엔티티
     * @return 해당 예매의 모든 예매 상세 목록
     */
    List<ReservationDetailJpaEntity> findByReservation(ReservationJpaEntity reservation);

    /**
     * 특정 예매 ID의 모든 예매 상세 정보 조회
     *
     * @param reservationId 예매 ID
     * @return 해당 예매의 모든 예매 상세 목록
     */
    List<ReservationDetailJpaEntity> findByReservationId(Long reservationId);

    /**
     * 특정 좌석의 예매 상세 정보 조회
     * 
     * @param seat 좌석 엔티티
     * @return 해당 좌석의 예매 상세 정보 (Optional)
     */
    Optional<ReservationDetailJpaEntity> findBySeat(SeatJpaEntity seat);
    
    /**
     * 특정 예매의 예매 상세 수 조회
     * 
     * @param reservationId 예매 ID
     * @return 예매 상세 수
     */
    long countByReservationId(Long reservationId);
    
    /**
     * 특정 예매의 총 금액 계산
     * 
     * @param reservationId 예매 ID
     * @return 총 금액
     */
    @Query("SELECT SUM(rd.price) FROM ReservationDetailJpaEntity rd WHERE rd.reservation.id = :reservationId")
    BigDecimal sumPriceByReservationId(@Param("reservationId") Long reservationId);
    
    /**
     * 특정 좌석 등급의 예매 상세 목록 조회
     * 
     * @param seatGradeId 좌석 등급 ID
     * @return 해당 좌석 등급의 예매 상세 목록
     */
    @Query("SELECT rd FROM ReservationDetailJpaEntity rd JOIN rd.seat s WHERE s.seatGrade.id = :seatGradeId")
    List<ReservationDetailJpaEntity> findBySeatGradeId(@Param("seatGradeId") Long seatGradeId);
    
    /**
     * 특정 콘서트의 예매 상세 목록 조회
     * 
     * @param concertId 콘서트 ID
     * @return 해당 콘서트의 예매 상세 목록
     */
    @Query("SELECT rd FROM ReservationDetailJpaEntity rd JOIN rd.seat s WHERE s.concert.id = :concertId")
    List<ReservationDetailJpaEntity> findByConcertId(@Param("concertId") Long concertId);
    
    /**
     * 특정 사용자의 예매 상세 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 예매 상세 목록
     */
    @Query("SELECT rd FROM ReservationDetailJpaEntity rd JOIN rd.reservation r WHERE r.userId = :userId")
    List<ReservationDetailJpaEntity> findByUserId(@Param("userId") Long userId);
}