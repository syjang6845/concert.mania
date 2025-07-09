package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.ReservationCommandPort;
import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.type.ReservationStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaReservationRepository;
import concert.mania.concert.infrastructure.persistence.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 예매 명령 영속성 어댑터
 * 예매 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class ReservationCommandAdapter implements ReservationCommandPort {

    private final DataJpaReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity entity = reservationMapper.toEntity(reservation);
        ReservationJpaEntity savedEntity = reservationRepository.save(entity);
        return reservationMapper.toDomain(savedEntity);
    }

    @Override
    public Reservation updateStatus(Long reservationId, ReservationStatus status) {
        ReservationJpaEntity oldEntity = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("예매 정보를 찾을 수 없습니다. ID: " + reservationId));

        // Create a new entity with updated status using the builder pattern
        ReservationJpaEntity.ReservationJpaEntityBuilder builder = ReservationJpaEntity.builder()
                .id(oldEntity.getId())
                .reservationNumber(oldEntity.getReservationNumber())
                .userId(oldEntity.getUserId())
                .concert(oldEntity.getConcert())
                .totalAmount(oldEntity.getTotalAmount())
                .status(status)
                .completedAt(oldEntity.getCompletedAt())
                .cancelledAt(oldEntity.getCancelledAt())
                .reservationDetails(oldEntity.getReservationDetails());

        // Update completedAt or cancelledAt based on the new status
        if (status == ReservationStatus.COMPLETED) {
            builder.completedAt(LocalDateTime.now());
        } else if (status == ReservationStatus.CANCELLED) {
            builder.cancelledAt(LocalDateTime.now());
        }

        ReservationJpaEntity newEntity = builder.build();
        ReservationJpaEntity savedEntity = reservationRepository.save(newEntity);
        return reservationMapper.toDomain(savedEntity);
    }

    @Override
    public Reservation complete(Long reservationId) {
        return updateStatus(reservationId, ReservationStatus.COMPLETED);
    }

    @Override
    public Reservation cancel(Long reservationId) {
        return updateStatus(reservationId, ReservationStatus.CANCELLED);
    }

    @Override
    public void delete(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    @Override
    public String generateReservationNumber(Long concertId, Long userId) {
        // 예매 번호 형식: 날짜(YYYYMMDD) + 콘서트ID + 사용자ID + 랜덤문자(4자리)
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        return String.format("%s-%d-%d-%s", dateStr, concertId, userId, randomStr);
    }
}
