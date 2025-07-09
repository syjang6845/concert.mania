package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.PaymentQueryPort;
import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.type.PaymentMethod;
import concert.mania.concert.domain.model.type.PaymentStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.PaymentJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.PaymentCustomRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaPaymentRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaReservationRepository;
import concert.mania.concert.infrastructure.persistence.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 결제 조회 영속성 어댑터
 * 결제 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryAdapter implements PaymentQueryPort {

    private final DataJpaPaymentRepository paymentRepository;
    private final DataJpaReservationRepository reservationRepository;
    private final PaymentCustomRepository paymentCustomRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        Optional<ReservationJpaEntity> reservation = reservationRepository.findById(reservationId);
        if (reservation.isEmpty()) {
            return Optional.empty();
        }
        return paymentRepository.findByReservation(reservation.get())
                .map(paymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByExternalPaymentId(String externalPaymentId) {
        return paymentRepository.findByExternalPaymentId(externalPaymentId)
                .map(paymentMapper::toDomain);
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByMethod(PaymentMethod method) {
        return paymentRepository.findByMethod(method).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByCompletedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return paymentRepository.findByCompletedAtBetweenAndStatus(
                startDateTime, endDateTime, PaymentStatus.COMPLETED).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByCancelledAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return paymentRepository.findByCancelledAtBetweenAndStatus(
                startDateTime, endDateTime, PaymentStatus.CANCELLED).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByAmountGreaterThanEqual(BigDecimal amount) {
        return paymentRepository.findByAmountGreaterThanEqual(amount).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByConcertId(Long concertId) {
        return paymentRepository.findByConcertId(concertId).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumAmountByCompletedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return paymentRepository.sumAmountByCompletedAtBetween(startDateTime, endDateTime);
    }

    @Override
    public Page<Payment> searchPayments(
            Long userId,
            Long concertId,
            PaymentStatus status,
            PaymentMethod method,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable) {

        Page<PaymentJpaEntity> paymentsPage = paymentCustomRepository.searchPayments(
                userId, concertId, status, method, fromDate, toDate, minAmount, maxAmount, pageable);

        List<Payment> payments = paymentsPage.getContent().stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(payments, pageable, paymentsPage.getTotalElements());
    }

    @Override
    public Map<PaymentMethod, BigDecimal> getPaymentMethodStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        return paymentCustomRepository.getPaymentMethodStatistics(fromDate, toDate);
    }

    @Override
    public Map<LocalDateTime, BigDecimal> getDailyPaymentStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        return paymentCustomRepository.getDailyPaymentStatistics(fromDate, toDate);
    }

    @Override
    public Map<Long, BigDecimal> getConcertPaymentStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        return paymentCustomRepository.getConcertPaymentStatistics(fromDate, toDate);
    }

    @Override
    public List<Payment> findPaymentsByUser(Long userId, PaymentStatus status, int limit) {
        return paymentCustomRepository.findPaymentsByUser(userId, status, limit).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findExpiredPayments(PaymentStatus status, LocalDateTime expirationTime) {
        return paymentRepository.findByStatusAndCreatedAtBefore(status, expirationTime).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findPendingPayments(LocalDateTime checkTime) {
        return paymentRepository.findByStatusAndCreatedAtAfter(PaymentStatus.PENDING, checkTime).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }
}
