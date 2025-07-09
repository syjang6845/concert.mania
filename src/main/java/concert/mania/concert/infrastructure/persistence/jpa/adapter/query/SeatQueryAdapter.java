package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.SeatQueryPort;
import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.type.SeatStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatGradeJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaConcertRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatGradeRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatRepository;
import concert.mania.concert.infrastructure.persistence.mapper.SeatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 좌석 조회 영속성 어댑터
 * 좌석 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatQueryAdapter implements SeatQueryPort {

    private final DataJpaSeatRepository seatRepository;
    private final DataJpaConcertRepository concertRepository;
    private final DataJpaSeatGradeRepository seatGradeRepository;
    private final SeatMapper seatMapper;

    @Override
    public Optional<Seat> findById(Long id) {
        return seatRepository.findById(id)
                .map(seatMapper::toDomain);
    }

    @Override
    public List<Seat> findByConcertId(Long concertId) {
        Optional<ConcertJpaEntity> concert = concertRepository.findById(concertId);
        if (concert.isEmpty()) {
            return List.of();
        }

        return seatRepository.findByConcert(concert.get()).stream()
                .map(seatMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findByConcertIdAndSeatGradeId(Long concertId, Long seatGradeId) {
        return seatRepository.findSeatsByConcertIdAndSeatGradeId(concertId, seatGradeId)
                .stream().map(seatMapper::toDomain)
                .toList();
    }

    @Override
    public List<Seat> findByConcertIdAndStatus(Long concertId, SeatStatus status) {
        Optional<ConcertJpaEntity> concert = concertRepository.findById(concertId);
        if (concert.isEmpty()) {
            return List.of();
        }

        return seatRepository.findByConcertAndStatus(concert.get(), status).stream()
                .map(seatMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findBySeatGradeId(Long seatGradeId) {
        Optional<SeatGradeJpaEntity> seatGrade = seatGradeRepository.findById(seatGradeId);
        if (seatGrade.isEmpty()) {
            return List.of();
        }

        return seatRepository.findBySeatGrade(seatGrade.get()).stream()
                .map(seatMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findBySeatGradeIdAndStatus(Long seatGradeId, SeatStatus status) {
        Optional<SeatGradeJpaEntity> seatGrade = seatGradeRepository.findById(seatGradeId);
        if (seatGrade.isEmpty()) {
            return List.of();
        }

        return seatRepository.findBySeatGradeAndStatus(seatGrade.get(), status).stream()
                .map(seatMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Seat> findByConcertIdAndSeatNumber(Long concertId, String seatNumber) {
        Optional<ConcertJpaEntity> concert = concertRepository.findById(concertId);
        if (concert.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(seatRepository.findByConcertAndSeatNumber(concert.get(), seatNumber))
                .map(seatMapper::toDomain);
    }

    @Override
    public long countAvailableSeatsByConcertId(Long concertId) {
        return seatRepository.countAvailableSeatsByConcertId(concertId);
    }

    @Override
    public long countAvailableSeatsByConcertIdAndSeatGradeId(Long concertId, Long seatGradeId) {
        return seatRepository.countAvailableSeatsByConcertIdAndSeatGradeId(concertId, seatGradeId);
    }
}