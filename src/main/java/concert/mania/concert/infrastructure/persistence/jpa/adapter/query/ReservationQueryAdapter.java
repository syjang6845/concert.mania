package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.ReservationQueryPort;
import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.type.ReservationStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaConcertRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaReservationRepository;
import concert.mania.concert.infrastructure.persistence.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 예매 조회 영속성 어댑터
 * 예매 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryAdapter implements ReservationQueryPort {

    private final DataJpaReservationRepository reservationRepository;
    private final DataJpaConcertRepository concertRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::toDomain);
    }

    @Override
    public Optional<Reservation> findByReservationNumber(String reservationNumber) {
        return reservationRepository.findByReservationNumber(reservationNumber)
                .map(reservationMapper::toDomain);
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(reservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByConcertId(Long concertId) {
        Optional<ConcertJpaEntity> concert = concertRepository.findById(concertId);
        if (concert.isEmpty()) {
            return List.of();
        }

        return reservationRepository.findByConcert(concert.get()).stream()
                .map(reservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(reservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status) {
        return reservationRepository.findByUserIdAndStatus(userId, status).stream()
                .map(reservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByConcertIdAndStatus(Long concertId, ReservationStatus status) {
        Optional<ConcertJpaEntity> concert = concertRepository.findById(concertId);
        if (concert.isEmpty()) {
            return List.of();
        }

        return reservationRepository.findByConcertAndStatus(concert.get(), status).stream()
                .map(reservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return reservationRepository.findByCreatedAtBetween(startDateTime, endDateTime).stream()
                .map(reservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByCompletedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Using JPQL query to find reservations completed between the given dates
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.COMPLETED)
                .filter(reservation -> reservation.getCompletedAt() != null)
                .filter(reservation -> !reservation.getCompletedAt().isBefore(startDateTime) 
                        && !reservation.getCompletedAt().isAfter(endDateTime))
                .map(reservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByCancelledAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Using JPQL query to find reservations cancelled between the given dates
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CANCELLED)
                .filter(reservation -> reservation.getCancelledAt() != null)
                .filter(reservation -> !reservation.getCancelledAt().isBefore(startDateTime) 
                        && !reservation.getCancelledAt().isAfter(endDateTime))
                .map(reservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByConcertId(Long concertId) {
        // Using the existing method to count all reservations for a concert
        return reservationRepository.findByConcert(
                concertRepository.findById(concertId).orElse(null)
            ).size();
    }

    @Override
    public long countByConcertIdAndStatus(Long concertId, ReservationStatus status) {
        // Using the existing method to count reservations with specific status for a concert
        Optional<ConcertJpaEntity> concert = concertRepository.findById(concertId);
        if (concert.isEmpty()) {
            return 0;
        }

        return reservationRepository.findByConcertAndStatus(concert.get(), status).size();
    }
}
