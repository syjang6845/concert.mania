package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.ReservationDetailQueryPort;
import concert.mania.concert.domain.model.ReservationDetail;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationDetailJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaReservationDetailRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaReservationRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatRepository;
import concert.mania.concert.infrastructure.persistence.mapper.ReservationDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 예매 상세 조회 영속성 어댑터
 * 예매 상세 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationDetailQueryAdapter implements ReservationDetailQueryPort {

    private final DataJpaReservationDetailRepository reservationDetailRepository;
    private final DataJpaReservationRepository reservationRepository;
    private final DataJpaSeatRepository seatRepository;
    private final ReservationDetailMapper reservationDetailMapper;

    @Override
    public Optional<ReservationDetail> findById(Long id) {
        return reservationDetailRepository.findById(id)
                .map(reservationDetailMapper::toDomain);
    }

    @Override
    public List<ReservationDetail> findByReservationId(Long reservationId) {
        Optional<ReservationJpaEntity> reservation = reservationRepository.findById(reservationId);
        if (reservation.isEmpty()) {
            return List.of();
        }
        
        return reservationDetailRepository.findByReservation(reservation.get()).stream()
                .map(reservationDetailMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReservationDetail> findBySeatId(Long seatId) {
        Optional<SeatJpaEntity> seat = seatRepository.findById(seatId);
        if (seat.isEmpty()) {
            return Optional.empty();
        }
        
        return reservationDetailRepository.findBySeat(seat.get())
                .map(reservationDetailMapper::toDomain);
    }

    @Override
    public long countByReservationId(Long reservationId) {
        return reservationDetailRepository.countByReservationId(reservationId);
    }

    @Override
    public BigDecimal sumPriceByReservationId(Long reservationId) {
        BigDecimal sum = reservationDetailRepository.sumPriceByReservationId(reservationId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public List<ReservationDetail> findBySeatGradeId(Long seatGradeId) {
        return reservationDetailRepository.findBySeatGradeId(seatGradeId).stream()
                .map(reservationDetailMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDetail> findByConcertId(Long concertId) {
        return reservationDetailRepository.findByConcertId(concertId).stream()
                .map(reservationDetailMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDetail> findByUserId(Long userId) {
        return reservationDetailRepository.findByUserId(userId).stream()
                .map(reservationDetailMapper::toDomain)
                .collect(Collectors.toList());
    }
}