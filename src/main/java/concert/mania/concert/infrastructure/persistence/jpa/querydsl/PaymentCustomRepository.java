package concert.mania.concert.infrastructure.persistence.jpa.querydsl;

import concert.mania.concert.domain.model.type.PaymentMethod;
import concert.mania.concert.domain.model.type.PaymentStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.PaymentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 결제 엔티티에 대한 사용자 정의 쿼리 인터페이스
 * QueryDSL을 사용한 동적 쿼리 메서드를 정의합니다.
 */
public interface PaymentCustomRepository {
    
    /**
     * 다양한 조건으로 결제를 검색합니다.
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
    Page<PaymentJpaEntity> searchPayments(
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
     * 특정 기간 동안의 결제 방식별 통계를 조회합니다.
     * 
     * @param fromDate 시작일
     * @param toDate 종료일
     * @return 결제 방식별 금액 맵 (결제 방식 -> 총 금액)
     */
    Map<PaymentMethod, BigDecimal> getPaymentMethodStatistics(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * 특정 기간 동안의 일별 결제 통계를 조회합니다.
     * 
     * @param fromDate 시작일
     * @param toDate 종료일
     * @return 일별 결제 금액 맵 (날짜 -> 총 금액)
     */
    Map<LocalDateTime, BigDecimal> getDailyPaymentStatistics(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * 특정 기간 동안의 콘서트별 결제 통계를 조회합니다.
     * 
     * @param fromDate 시작일
     * @param toDate 종료일
     * @return 콘서트별 결제 금액 맵 (콘서트 ID -> 총 금액)
     */
    Map<Long, BigDecimal> getConcertPaymentStatistics(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * 특정 사용자의 결제 내역을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param status 결제 상태 (선택적)
     * @param limit 조회할 결제 수
     * @return 결제 목록
     */
    List<PaymentJpaEntity> findPaymentsByUser(Long userId, PaymentStatus status, int limit);
}