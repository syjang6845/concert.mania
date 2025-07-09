package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.SeatLockQueryPort;
import concert.mania.concert.domain.model.SeatLock;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatLockJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaConcertRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatLockRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatRepository;
import concert.mania.concert.infrastructure.persistence.mapper.SeatLockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 좌석 잠금 조회 영속성 어댑터
 * 좌석 잠금 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatLockQueryAdapter implements SeatLockQueryPort {

    private final DataJpaSeatLockRepository seatLockRepository;
    private final DataJpaSeatRepository seatRepository;
    private final DataJpaConcertRepository concertRepository;
    private final SeatLockMapper seatLockMapper;

    @Override
    public Optional<SeatLock> findById(Long id) {
        return seatLockRepository.findById(id)
                .map(seatLockMapper::toDomain);
    }

    @Override
    public Optional<SeatLock> findBySeatId(Long seatId) {
        Optional<SeatJpaEntity> seat = seatRepository.findById(seatId);
        if (seat.isEmpty()) {
            return Optional.empty();
        }
        
        return seatLockRepository.findBySeat(seat.get())
                .map(seatLockMapper::toDomain);
    }

    @Override
    public List<SeatLock> findByUserId(Long userId) {
        return seatLockRepository.findByUserId(userId).stream()
                .map(seatLockMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatLock> findByExpiresAtBefore(LocalDateTime dateTime) {
        return seatLockRepository.findByExpiresAtBefore(dateTime).stream()
                .map(seatLockMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatLock> findByConcertId(Long concertId) {
        return seatLockRepository.findByConcertId(concertId).stream()
                .map(seatLockMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatLock> findByUserIdAndConcertId(Long userId, Long concertId) {
        return seatLockRepository.findByUserIdAndConcertId(userId, concertId).stream()
                .map(seatLockMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isLocked(Long seatId) {
        Optional<SeatJpaEntity> seat = seatRepository.findById(seatId);
        if (seat.isEmpty()) {
            return false;
        }
        
        Optional<SeatLockJpaEntity> seatLock = seatLockRepository.findBySeat(seat.get());
        return seatLock.isPresent() && seatLock.get().getExpiresAt().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isLockedByUser(Long seatId, Long userId) {
        Optional<SeatJpaEntity> seat = seatRepository.findById(seatId);
        if (seat.isEmpty()) {
            return false;
        }
        
        Optional<SeatLockJpaEntity> seatLock = seatLockRepository.findBySeat(seat.get());
        return seatLock.isPresent() && 
               seatLock.get().getUserId().equals(userId) && 
               seatLock.get().getExpiresAt().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isExpired(Long seatId) {
        Optional<SeatJpaEntity> seat = seatRepository.findById(seatId);
        if (seat.isEmpty()) {
            return false;
        }
        
        Optional<SeatLockJpaEntity> seatLock = seatLockRepository.findBySeat(seat.get());
        return seatLock.isPresent() && seatLock.get().getExpiresAt().isBefore(LocalDateTime.now());
    }
}