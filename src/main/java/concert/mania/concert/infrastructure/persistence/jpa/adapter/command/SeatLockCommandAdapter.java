package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import concert.mania.concert.application.port.out.command.SeatLockCommandPort;
import concert.mania.concert.domain.model.SeatLock;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatLockJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatLockRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaSeatRepository;
import concert.mania.concert.infrastructure.persistence.mapper.SeatLockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 좌석 잠금 명령 영속성 어댑터
 * 좌석 잠금 관련 명령 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional
public class SeatLockCommandAdapter implements SeatLockCommandPort {

    private final DataJpaSeatLockRepository seatLockRepository;
    private final DataJpaSeatRepository seatRepository;
    private final SeatLockMapper seatLockMapper;

    @Override
    public SeatLock save(SeatLock seatLock) {
        SeatLockJpaEntity entity = seatLockMapper.toEntity(seatLock);
        SeatLockJpaEntity savedEntity = seatLockRepository.save(entity);
        return seatLockMapper.toDomain(savedEntity);
    }

    @Override
    public SeatLock lock(Long seatId, Long userId, LocalDateTime expiresAt) {
        // Find the seat
        SeatJpaEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        // Service 레이어에서 이미 검증했으므로 단순히 잠금만 생성
        LocalDateTime now = LocalDateTime.now();
        SeatLockJpaEntity seatLock = SeatLockJpaEntity.builder()
                .seat(seat)
                .userId(userId)
                .lockedAt(now)
                .expiresAt(expiresAt)
                .build();

        SeatLockJpaEntity savedEntity = seatLockRepository.save(seatLock);
        return seatLockMapper.toDomain(savedEntity);
    }

    @Override
    public SeatLock extend(Long seatId, int minutes) {
        // Find the seat
        SeatJpaEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        // Find the lock
        SeatLockJpaEntity lock = seatLockRepository.findBySeat(seat)
                .orElseThrow(() -> new NoSuchElementException("좌석 잠금 정보를 찾을 수 없습니다. 좌석 ID: " + seatId));

        // Check if the lock is expired
        if (lock.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 만료된 잠금은 연장할 수 없습니다. 좌석 ID: " + seatId);
        }

        // Create a new lock with extended expiration time
        SeatLockJpaEntity newLock = SeatLockJpaEntity.builder()
                .id(lock.getId())
                .seat(lock.getSeat())
                .userId(lock.getUserId())
                .lockedAt(lock.getLockedAt())
                .expiresAt(lock.getExpiresAt().plusMinutes(minutes))
                .build();

        SeatLockJpaEntity savedEntity = seatLockRepository.save(newLock);
        return seatLockMapper.toDomain(savedEntity);
    }

    @Override
    public void unlock(Long seatId) {
        // Find the seat
        SeatJpaEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다. ID: " + seatId));

        // Find and delete the lock
        seatLockRepository.findBySeat(seat).ifPresent(seatLockRepository::delete);
    }

    @Override
    public int unlockByUserId(Long userId) {
        return seatLockRepository.deleteByUserId(userId);
    }

    @Override
    public int unlockByConcertId(Long concertId) {
        // Find all locks for the concert and delete them
        // This is a two-step process because there's no direct method to delete by concertId
        List<SeatLockJpaEntity> locks = seatLockRepository.findByConcertId(concertId);
        locks.forEach(seatLockRepository::delete);
        return locks.size();
    }

    @Override
    public int unlockExpired() {
        return seatLockRepository.deleteExpiredLocks(LocalDateTime.now());
    }
}