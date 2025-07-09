package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.domain.model.type.ReservationStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예매 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaReservationRepository extends JpaRepository<ReservationJpaEntity, Long> {
    
    /**
     * 예매 번호로 예매 정보 조회
     * 
     * @param reservationNumber 예매 번호
     * @return 예매 정보 (Optional)
     */
    Optional<ReservationJpaEntity> findByReservationNumber(String reservationNumber);
    
    /**
     * 특정 사용자의 모든 예매 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 예매 목록
     */
    List<ReservationJpaEntity> findByUserId(Long userId);
    
    /**
     * 특정 사용자의 예매 정보를 페이지네이션하여 조회
     * 
     * @param userId 사용자 ID
     * @param pageable 페이지 정보
     * @return 페이지네이션된 예매 목록
     */
    Page<ReservationJpaEntity> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 특정 콘서트의 모든 예매 정보 조회
     * 
     * @param concert 콘서트 엔티티
     * @return 해당 콘서트의 모든 예매 목록
     */
    List<ReservationJpaEntity> findByConcert(ConcertJpaEntity concert);
    
    /**
     * 특정 상태의 모든 예매 정보 조회
     * 
     * @param status 예매 상태
     * @return 해당 상태의 모든 예매 목록
     */
    List<ReservationJpaEntity> findByStatus(ReservationStatus status);
    
    /**
     * 특정 사용자의 특정 상태의 예매 정보 조회
     * 
     * @param userId 사용자 ID
     * @param status 예매 상태
     * @return 해당 사용자의 해당 상태의 예매 목록
     */
    List<ReservationJpaEntity> findByUserIdAndStatus(Long userId, ReservationStatus status);
    
    /**
     * 특정 콘서트의 특정 상태의 예매 정보 조회
     * 
     * @param concert 콘서트 엔티티
     * @param status 예매 상태
     * @return 해당 콘서트의 해당 상태의 예매 목록
     */
    List<ReservationJpaEntity> findByConcertAndStatus(ConcertJpaEntity concert, ReservationStatus status);
    
    /**
     * 특정 기간에 생성된 예매 정보 조회
     * 
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return 해당 기간에 생성된 예매 목록
     */
    List<ReservationJpaEntity> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * 특정 콘서트의 예매 수 조회
     * 
     * @param concertId 콘서트 ID
     * @return 예매 수
     */
    @Query("SELECT COUNT(r) FROM ReservationJpaEntity r WHERE r.concert.id = :concertId AND r.status = 'COMPLETED'")
    long countCompletedReservationsByConcertId(@Param("concertId") Long concertId);
    
    /**
     * 특정 기간 내 완료된 예매 수 조회
     * 
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return 해당 기간 내 완료된 예매 수
     */
    @Query("SELECT COUNT(r) FROM ReservationJpaEntity r WHERE r.completedAt BETWEEN :startDateTime AND :endDateTime AND r.status = 'COMPLETED'")
    long countCompletedReservationsByPeriod(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
}