package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.SeatCommandPort;
import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.SeatLock;
import concert.mania.concert.domain.model.type.SeatStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatLockJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaConcertRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatLockRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatRepository;
import concert.mania.concert.infrastructure.persistence.mapper.SeatLockMapper;
import concert.mania.concert.infrastructure.persistence.mapper.SeatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 좌석 명령 영속성 어댑터
 * 좌석 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class SeatCommandAdapter implements SeatCommandPort {
    private final DataJpaSeatRepository seatRepository;
    private final DataJpaSeatLockRepository seatLockRepository;
    private final DataJpaConcertRepository concertRepository;
    private final SeatMapper seatMapper;
    private final SeatLockMapper seatLockMapper;

    @Override
    public Seat save(Seat seat) {
        SeatJpaEntity entity = seatMapper.toEntity(seat);
        SeatJpaEntity savedEntity = seatRepository.save(entity);
        return seatMapper.toDomain(savedEntity);
    }

    @Override
    public List<Seat> saveAll(List<Seat> seats) {
        List<SeatJpaEntity> entities = seats.stream()
                .map(seatMapper::toEntity)
                .collect(Collectors.toList());
        List<SeatJpaEntity> savedEntities = seatRepository.saveAll(entities);
        return savedEntities.stream()
                .map(seatMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Seat updateStatus(Long seatId, SeatStatus status) {
        SeatJpaEntity oldEntity = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        // Create a new entity with updated status using the builder pattern
        SeatJpaEntity newEntity = SeatJpaEntity.builder()
                .id(oldEntity.getId())
                .concert(oldEntity.getConcert())
                .seatGrade(oldEntity.getSeatGrade())
                .seatNumber(oldEntity.getSeatNumber())
                .seatRow(oldEntity.getSeatRow())
                .seatCol(oldEntity.getSeatCol())
                .status(status)
                .build();

        SeatJpaEntity savedEntity = seatRepository.save(newEntity);
        return seatMapper.toDomain(savedEntity);
    }

    @Override
    public Seat select(Long seatId, Long userId) {
        SeatJpaEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 선택되었거나 판매된 좌석입니다. ID: " + seatId);
        }

        // Update seat status to SELECTED
        updateStatus(seatId, SeatStatus.SELECTED);
        SeatJpaEntity updatedSeatEntity = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));


        return seatMapper.toDomain(updatedSeatEntity);
    }

    @Override
    public Seat reserve(Long seatId) {
        SeatJpaEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        if (seat.getStatus() != SeatStatus.SELECTED && seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("선택되지 않았거나 이미 판매된 좌석입니다. ID: " + seatId);
        }

        return updateStatus(seatId, SeatStatus.RESERVED);
    }

    @Override
    public Seat sell(Long seatId) {
        SeatJpaEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        if (seat.getStatus() != SeatStatus.RESERVED) {
            throw new IllegalStateException("예매 진행 중이 아닌 좌석입니다. ID: " + seatId);
        }

        // Remove any existing lock
        seatLockRepository.findBySeat(seat).ifPresent(seatLockRepository::delete);

        return updateStatus(seatId, SeatStatus.SOLD);
    }

    @Override
    public Seat reset(Long seatId) {
        SeatJpaEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new IllegalStateException("이미 판매 완료된 좌석은 초기화할 수 없습니다. ID: " + seatId);
        }

        // Remove any existing lock
        seatLockRepository.findBySeat(seat).ifPresent(seatLockRepository::delete);

        return updateStatus(seatId, SeatStatus.AVAILABLE);
    }

    @Override
    public void delete(Long seatId) {
        // Find the seat first
        SeatJpaEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        // Remove any existing lock
        seatLockRepository.findBySeat(seat).ifPresent(seatLockRepository::delete);

        seatRepository.deleteById(seatId);
    }

    @Override
    public void deleteByConcertId(Long concertId) {
        // Find the concert first
        ConcertJpaEntity concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new NoSuchElementException("콘서트 정보를 찾을 수 없습니다. ID: " + concertId));

        // Find all seats for the concert
        List<SeatJpaEntity> seats = seatRepository.findByConcert(concert);

        // Remove locks for all seats in the concert
        seats.forEach(seat -> seatLockRepository.findBySeat(seat).ifPresent(seatLockRepository::delete));

        // Delete all seats individually
        seats.forEach(seatRepository::delete);
    }
}
