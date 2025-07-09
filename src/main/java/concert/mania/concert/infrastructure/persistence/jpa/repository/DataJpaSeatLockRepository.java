package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatLockJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 좌석 잠금 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaSeatLockRepository extends JpaRepository<SeatLockJpaEntity, Long> {
    
    /**
     * 특정 좌석의 잠금 정보 조회
     * 
     * @param seat 좌석 엔티티
     * @return 좌석 잠금 정보 (Optional)
     */
    Optional<SeatLockJpaEntity> findBySeat(SeatJpaEntity seat);
    
    /**
     * 특정 사용자의 모든 좌석 잠금 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 좌석 잠금 목록
     */
    List<SeatLockJpaEntity> findByUserId(Long userId);
    
    /**
     * 특정 시간 이전에 만료되는 모든 좌석 잠금 정보 조회
     * 
     * @param dateTime 기준 시간
     * @return 해당 시간 이전에 만료되는 좌석 잠금 목록
     */
    List<SeatLockJpaEntity> findByExpiresAtBefore(LocalDateTime dateTime);
    
    /**
     * 특정 콘서트의 모든 좌석 잠금 정보 조회
     * 
     * @param concertId 콘서트 ID
     * @return 해당 콘서트의 모든 좌석 잠금 목록
     */
    @Query("SELECT sl FROM SeatLockJpaEntity sl JOIN sl.seat s WHERE s.concert.id = :concertId")
    List<SeatLockJpaEntity> findByConcertId(@Param("concertId") Long concertId);
    
    /**
     * 특정 사용자의 특정 콘서트에 대한 좌석 잠금 정보 조회
     * 
     * @param userId 사용자 ID
     * @param concertId 콘서트 ID
     * @return 해당 사용자의 해당 콘서트에 대한 좌석 잠금 목록
     */
    @Query("SELECT sl FROM SeatLockJpaEntity sl JOIN sl.seat s WHERE sl.userId = :userId AND s.concert.id = :concertId")
    List<SeatLockJpaEntity> findByUserIdAndConcertId(
            @Param("userId") Long userId,
            @Param("concertId") Long concertId);
    
    /**
     * 만료된 좌석 잠금 삭제
     * 
     * @param now 현재 시간
     * @return 삭제된 레코드 수
     */
    @Modifying
    @Query("DELETE FROM SeatLockJpaEntity sl WHERE sl.expiresAt < :now")
    int deleteExpiredLocks(@Param("now") LocalDateTime now);
    
    /**
     * 특정 좌석의 잠금 정보 삭제
     * 
     * @param seatId 좌석 ID
     * @return 삭제된 레코드 수
     */
    @Modifying
    @Query("DELETE FROM SeatLockJpaEntity sl WHERE sl.seat.id = :seatId")
    int deleteBySeatId(@Param("seatId") Long seatId);
    
    /**
     * 특정 사용자의 모든 좌석 잠금 정보 삭제
     * 
     * @param userId 사용자 ID
     * @return 삭제된 레코드 수
     */
    @Modifying
    @Query("DELETE FROM SeatLockJpaEntity sl WHERE sl.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}