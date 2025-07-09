package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.PaymentCommandPort;
import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.type.PaymentStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.PaymentJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaPaymentRepository;
import concert.mania.concert.infrastructure.persistence.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 결제 명령 영속성 어댑터
 * 결제 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class PaymentCommandAdapter implements PaymentCommandPort {

    private final DataJpaPaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = paymentMapper.toEntity(payment);
        PaymentJpaEntity savedEntity = paymentRepository.save(entity);
        return paymentMapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    @Override
    public Payment complete(Long paymentId, LocalDateTime completedAt) {
        PaymentJpaEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentId));

        if (entity.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("진행 중인 결제만 완료 처리할 수 있습니다.");
        }

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        PaymentJpaEntity updatedEntity = PaymentJpaEntity.builder()
                .id(entity.getId())
                .reservation(entity.getReservation())
                .externalPaymentId(entity.getExternalPaymentId())
                .amount(entity.getAmount())
                .method(entity.getMethod())
                .status(PaymentStatus.COMPLETED)
                .completedAt(completedAt)
                .cancelledAt(entity.getCancelledAt())
                .paymentDetails(entity.getPaymentDetails())
                .build();

        PaymentJpaEntity savedEntity = paymentRepository.save(updatedEntity);
        return paymentMapper.toDomain(savedEntity);
    }

    @Override
    public Payment fail(Long paymentId) {
        PaymentJpaEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentId));

        if (entity.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("진행 중인 결제만 실패 처리할 수 있습니다.");
        }

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        PaymentJpaEntity updatedEntity = PaymentJpaEntity.builder()
                .id(entity.getId())
                .reservation(entity.getReservation())
                .externalPaymentId(entity.getExternalPaymentId())
                .amount(entity.getAmount())
                .method(entity.getMethod())
                .status(PaymentStatus.FAILED)
                .completedAt(entity.getCompletedAt())
                .cancelledAt(entity.getCancelledAt())
                .paymentDetails(entity.getPaymentDetails())
                .build();

        PaymentJpaEntity savedEntity = paymentRepository.save(updatedEntity);
        return paymentMapper.toDomain(savedEntity);
    }

    @Override
    public Payment cancel(Long paymentId, LocalDateTime cancelledAt) {
        PaymentJpaEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentId));

        if (entity.getStatus() == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }
        if (entity.getStatus() == PaymentStatus.FAILED) {
            throw new IllegalStateException("실패한 결제는 취소할 수 없습니다.");
        }

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        PaymentJpaEntity updatedEntity = PaymentJpaEntity.builder()
                .id(entity.getId())
                .reservation(entity.getReservation())
                .externalPaymentId(entity.getExternalPaymentId())
                .amount(entity.getAmount())
                .method(entity.getMethod())
                .status(PaymentStatus.CANCELLED)
                .completedAt(entity.getCompletedAt())
                .cancelledAt(cancelledAt)
                .paymentDetails(entity.getPaymentDetails())
                .build();

        PaymentJpaEntity savedEntity = paymentRepository.save(updatedEntity);
        return paymentMapper.toDomain(savedEntity);
    }

    @Override
    public Payment updateStatus(Long paymentId, PaymentStatus status) {
        PaymentJpaEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentId));

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        PaymentJpaEntity updatedEntity = PaymentJpaEntity.builder()
                .id(entity.getId())
                .reservation(entity.getReservation())
                .externalPaymentId(entity.getExternalPaymentId())
                .amount(entity.getAmount())
                .method(entity.getMethod())
                .status(status)
                .completedAt(entity.getCompletedAt())
                .cancelledAt(entity.getCancelledAt())
                .paymentDetails(entity.getPaymentDetails())
                .build();

        PaymentJpaEntity savedEntity = paymentRepository.save(updatedEntity);
        return paymentMapper.toDomain(savedEntity);
    }

    @Override
    public Payment updateExternalPaymentId(Long paymentId, String externalPaymentId) {
        PaymentJpaEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentId));

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        PaymentJpaEntity updatedEntity = PaymentJpaEntity.builder()
                .id(entity.getId())
                .reservation(entity.getReservation())
                .externalPaymentId(externalPaymentId)
                .amount(entity.getAmount())
                .method(entity.getMethod())
                .status(entity.getStatus())
                .completedAt(entity.getCompletedAt())
                .cancelledAt(entity.getCancelledAt())
                .paymentDetails(entity.getPaymentDetails())
                .build();

        PaymentJpaEntity savedEntity = paymentRepository.save(updatedEntity);
        return paymentMapper.toDomain(savedEntity);
    }

    @Override
    public Payment updatePaymentDetails(Long paymentId, String paymentDetails) {
        PaymentJpaEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentId));

        // 새로운 엔티티 생성 (Builder 패턴 사용)
        PaymentJpaEntity updatedEntity = PaymentJpaEntity.builder()
                .id(entity.getId())
                .reservation(entity.getReservation())
                .externalPaymentId(entity.getExternalPaymentId())
                .amount(entity.getAmount())
                .method(entity.getMethod())
                .status(entity.getStatus())
                .completedAt(entity.getCompletedAt())
                .cancelledAt(entity.getCancelledAt())
                .paymentDetails(paymentDetails)
                .build();

        PaymentJpaEntity savedEntity = paymentRepository.save(updatedEntity);
        return paymentMapper.toDomain(savedEntity);
    }
}