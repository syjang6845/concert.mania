package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.type.PaymentMethod;
import concert.mania.concert.domain.model.type.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 결제 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface PaymentQueryPort {

    /**
     * ID로 결제 정보 조회
     * 
     * @param id 결제 ID
     * @return 결제 정보 (Optional)
     */
    Optional<Payment> findById(Long id);

    /**
     * 예매 ID로 결제 정보 조회
     * 
     * @param reservationId 예매 ID
     * @return 결제 정보 (Optional)
     */
    Optional<Payment> findByReservationId(Long reservationId);

    /**
     * 외부 결제 ID로 결제 정보 조회
     * 
     * @param externalPaymentId 외부 결제 ID
     * @return 결제 정보 (Optional)
     */
    Optional<Payment> findByExternalPaymentId(String externalPaymentId);

    /**
     * 특정 상태의 모든 결제 정보 조회
     * 
     * @param status 결제 상태
     * @return 해당 상태의 모든 결제 목록
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * 특정 결제 방식의 모든 결제 정보 조회
     * 
     * @param method 결제 방식
     * @return 해당 결제 방식의 모든 결제 목록
     */
    List<Payment> findByMethod(PaymentMethod method);

    /**
     * 특정 기간에 완료된 결제 정보 조회
     * 
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return 해당 기간에 완료된 결제 목록
     */
    List<Payment> findByCompletedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 특정 기간에 취소된 결제 정보 조회
     * 
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return 해당 기간에 취소된 결제 목록
     */
    List<Payment> findByCancelledAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 특정 금액 이상의 결제 정보 조회
     * 
     * @param amount 기준 금액
     * @return 해당 금액 이상의 결제 목록
     */
    List<Payment> findByAmountGreaterThanEqual(BigDecimal amount);

    /**
     * 특정 사용자의 결제 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 결제 목록
     */
    List<Payment> findByUserId(Long userId);

    /**
     * 특정 콘서트의 결제 정보 조회
     * 
     * @param concertId 콘서트 ID
     * @return 해당 콘서트의 결제 목록
     */
    List<Payment> findByConcertId(Long concertId);

    /**
     * 특정 기간 동안의 결제 총액 계산
     * 
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return 해당 기간 동안의 결제 총액
     */
    BigDecimal sumAmountByCompletedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 다양한 조건으로 결제 검색
     * 
     * @param userId 사용자 ID (선택적)
     * @param concertId 콘서트 ID (선택적)
     * @param status 결제 상태 (선택적)
     * @param method 결제 방식 (선택적)
     * @param fromDate 결제 일시 범위 시작 (선택적)
     * @param toDate 결제 일시 범위 종료 (선택적)
     * @param minAmount 최소 결제 금액 (선택적)
     * @param maxAmount 최대 결제 금액 (선택적)
     * @param pageable 페이지 정보
     * @return 조건에 맞는 결제 목록 (페이지네이션)
     */
    Page<Payment> searchPayments(
            Long userId,
            Long concertId,
            PaymentStatus status,
            PaymentMethod method,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable);

    /**
     * 특정 기간 동안의 결제 방식별 통계 조회
     * 
     * @param fromDate 시작일
     * @param toDate 종료일
     * @return 결제 방식별 금액 맵 (결제 방식 -> 총 금액)
     */
    Map<PaymentMethod, BigDecimal> getPaymentMethodStatistics(LocalDateTime fromDate, LocalDateTime toDate);

    /**
     * 특정 기간 동안의 일별 결제 통계 조회
     * 
     * @param fromDate 시작일
     * @param toDate 종료일
     * @return 일별 결제 금액 맵 (날짜 -> 총 금액)
     */
    Map<LocalDateTime, BigDecimal> getDailyPaymentStatistics(LocalDateTime fromDate, LocalDateTime toDate);

    /**
     * 특정 기간 동안의 콘서트별 결제 통계 조회
     * 
     * @param fromDate 시작일
     * @param toDate 종료일
     * @return 콘서트별 결제 금액 맵 (콘서트 ID -> 총 금액)
     */
    Map<Long, BigDecimal> getConcertPaymentStatistics(LocalDateTime fromDate, LocalDateTime toDate);

    /**
     * 특정 사용자의 결제 내역 조회
     * 
     * @param userId 사용자 ID
     * @param status 결제 상태 (선택적)
     * @param limit 조회할 결제 수
     * @return 결제 목록
     */
    List<Payment> findPaymentsByUser(Long userId, PaymentStatus status, int limit);

    /**
     * 만료된 결제 조회
     * 특정 상태의 결제 중 지정된 시간 이전에 생성된 결제 목록 조회
     * 
     * @param status 결제 상태
     * @param expirationTime 만료 기준 시간
     * @return 만료된 결제 목록
     */
    List<Payment> findExpiredPayments(PaymentStatus status, LocalDateTime expirationTime);

    /**
     * 진행 중인 결제 조회
     * PENDING 상태의 결제 중 지정된 시간 이후에 생성된 결제 목록 조회
     * 
     * @param checkTime 조회 기준 시간
     * @return 진행 중인 결제 목록
     */
    List<Payment> findPendingPayments(LocalDateTime checkTime);
}
