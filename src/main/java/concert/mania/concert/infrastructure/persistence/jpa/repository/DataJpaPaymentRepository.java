package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.domain.model.type.PaymentMethod;
import concert.mania.concert.domain.model.type.PaymentStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.PaymentJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 결제 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaPaymentRepository extends JpaRepository<PaymentJpaEntity, Long> {

    /**
     * 특정 예매의 결제 정보 조회
     * 
     * @param reservation 예매 엔티티
     * @return 해당 예매의 결제 정보 (Optional)
     */
    Optional<PaymentJpaEntity> findByReservation(ReservationJpaEntity reservation);

    /**
     * 외부 결제 ID로 결제 정보 조회
     * 
     * @param externalPaymentId 외부 결제 ID
     * @return 해당 외부 결제 ID의 결제 정보 (Optional)
     */
    Optional<PaymentJpaEntity> findByExternalPaymentId(String externalPaymentId);

    /**
     * 특정 상태의 모든 결제 정보 조회
     * 
     * @param status 결제 상태
     * @return 해당 상태의 모든 결제 목록
     */
    List<PaymentJpaEntity> findByStatus(PaymentStatus status);

    /**
     * 특정 결제 방식의 모든 결제 정보 조회
     * 
     * @param method 결제 방식
     * @return 해당 결제 방식의 모든 결제 목록
     */
    List<PaymentJpaEntity> findByMethod(PaymentMethod method);

    /**
     * 특정 기간에 완료된 결제 정보 조회
     * 
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return 해당 기간에 완료된 결제 목록
     */
    List<PaymentJpaEntity> findByCompletedAtBetweenAndStatus(
            LocalDateTime startDateTime, 
            LocalDateTime endDateTime, 
            PaymentStatus status);

    /**
     * 특정 기간에 취소된 결제 정보 조회
     * 
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return 해당 기간에 취소된 결제 목록
     */
    List<PaymentJpaEntity> findByCancelledAtBetweenAndStatus(
            LocalDateTime startDateTime, 
            LocalDateTime endDateTime, 
            PaymentStatus status);

    /**
     * 특정 금액 이상의 결제 정보 조회
     * 
     * @param amount 기준 금액
     * @return 해당 금액 이상의 결제 목록
     */
    List<PaymentJpaEntity> findByAmountGreaterThanEqual(BigDecimal amount);

    /**
     * 특정 사용자의 결제 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 결제 목록
     */
    @Query("SELECT p FROM PaymentJpaEntity p JOIN p.reservation r WHERE r.userId = :userId")
    List<PaymentJpaEntity> findByUserId(@Param("userId") Long userId);

    /**
     * 특정 콘서트의 결제 정보 조회
     * 
     * @param concertId 콘서트 ID
     * @return 해당 콘서트의 결제 목록
     */
    @Query("SELECT p FROM PaymentJpaEntity p JOIN p.reservation r WHERE r.concert.id = :concertId")
    List<PaymentJpaEntity> findByConcertId(@Param("concertId") Long concertId);

    /**
     * 특정 기간 동안의 결제 총액 계산
     * 
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return 해당 기간 동안의 결제 총액
     */
    @Query("SELECT SUM(p.amount) FROM PaymentJpaEntity p WHERE p.completedAt BETWEEN :startDateTime AND :endDateTime AND p.status = 'COMPLETED'")
    BigDecimal sumAmountByCompletedAtBetween(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    /**
     * 특정 상태이면서 지정된 시간 이전에 생성된 결제 정보 조회
     * 
     * @param status 결제 상태
     * @param createdAt 생성 시간 기준
     * @return 조건에 맞는 결제 목록
     */
    List<PaymentJpaEntity> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime createdAt);

    /**
     * 특정 상태이면서 지정된 시간 이후에 생성된 결제 정보 조회
     * 
     * @param status 결제 상태
     * @param createdAt 생성 시간 기준
     * @return 조건에 맞는 결제 목록
     */
    List<PaymentJpaEntity> findByStatusAndCreatedAtAfter(PaymentStatus status, LocalDateTime createdAt);
}
