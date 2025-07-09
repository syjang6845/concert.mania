package concert.mania.concert.application.service;

import concert.mania.concert.application.port.in.SeatUseCase;
import concert.mania.concert.application.port.out.command.SeatCommandPort;
import concert.mania.concert.application.port.out.command.SeatLockCommandPort;
import concert.mania.concert.application.port.out.query.SeatLockQueryPort;
import concert.mania.concert.application.port.out.query.SeatQueryPort;
import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.SeatLock;
import concert.mania.concert.domain.model.type.SeatStatus;
import concert.mania.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static concert.mania.exception.model.ErrorCode.ALREADY_SEAT;

/**
 * 좌석 애플리케이션 서비스
 * 좌석 관련 모든 유스케이스를 구현 (명령 및 조회)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatApplicationService implements SeatUseCase {

    private final SeatQueryPort seatQueryPort;
    private final SeatCommandPort seatCommandPort;
    private final SeatLockCommandPort seatLockCommandPort;
    private final SeatLockQueryPort seatLockQueryPort;

    private static final int LOCK_DURATION_MINUTES = 10;

    @Override
    public Optional<Seat> getSeatById(Long id) {
        log.info("좌석 조회: {}", id);
        return seatQueryPort.findById(id);
    }

    @Override
    public List<Seat> getSeatsByConcertId(Long concertId) {
        log.info("콘서트 ID로 좌석 목록 조회: {}", concertId);
        return seatQueryPort.findByConcertId(concertId);
    }

    @Override
    public List<Seat> getSeatsByConcertIdAndStatus(Long concertId, SeatStatus status) {
        log.info("콘서트 ID와 좌석 상태로 좌석 목록 조회: {}, {}", concertId, status);
        return seatQueryPort.findByConcertIdAndStatus(concertId, status);
    }

    @Override
    public List<Seat> getSeatsBySeatGradeId(Long seatGradeId) {
        log.info("좌석 등급 ID로 좌석 목록 조회: {}", seatGradeId);
        return seatQueryPort.findBySeatGradeId(seatGradeId);
    }

    @Override
    public List<Seat> getSeatsBySeatGradeIdAndStatus(Long seatGradeId, SeatStatus status) {
        log.info("좌석 등급 ID와 좌석 상태로 좌석 목록 조회: {}, {}", seatGradeId, status);
        return seatQueryPort.findBySeatGradeIdAndStatus(seatGradeId, status);
    }

    @Override
    public List<Seat> getSeatsByConcertIdAndSeatGradeId(Long concertId, Long seatGradeId) {
        log.info("콘서트 ID와 좌석 등급 ID로 좌석 목록 조회: {}, {}", concertId, seatGradeId);
        // 콘서트 ID로 모든 좌석을 조회한 후 좌석 등급 ID로 필터링
        return seatQueryPort.findByConcertIdAndSeatGradeId(concertId, seatGradeId).stream()
//                .filter(seat -> seat.getSeatGrade().getId().equals(seatGradeId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Seat> getSeatByConcertIdAndSeatNumber(Long concertId, String seatNumber) {
        log.info("콘서트 ID와 좌석 번호로 좌석 조회: {}, {}", concertId, seatNumber);
        return seatQueryPort.findByConcertIdAndSeatNumber(concertId, seatNumber);
    }

    @Override
    public long countAvailableSeatsByConcertId(Long concertId) {
        log.info("콘서트 ID로 예매 가능한 좌석 수 조회: {}", concertId);
        return seatQueryPort.countAvailableSeatsByConcertId(concertId);
    }

    @Override
    public long countAvailableSeatsByConcertIdAndSeatGradeId(Long concertId, Long seatGradeId) {
        log.info("콘서트 ID와 좌석 등급 ID로 예매 가능한 좌석 수 조회: {}, {}", concertId, seatGradeId);
        return seatQueryPort.countAvailableSeatsByConcertIdAndSeatGradeId(concertId, seatGradeId);
    }

    // === 명령(Command) 기능 구현 ===

    /**
     * 좌석 선택 (임시 점유)
     * 좌석을 선택하면 SELECTED 상태로 변경되고 10분 동안 타이머가 동작함
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 선택된 좌석 정보
     */
    @Override
    @Transactional
    public Seat selectSeat(Long seatId, Long userId) {
        // 1. 좌석 조회
        Seat seat = seatQueryPort.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

        // 2. 좌석이 이미 선택되었는지 확인
        Optional<SeatLock> existingLock = seatLockQueryPort.findBySeatId(seatId);
        if (existingLock.isPresent()) {
            SeatLock lock = existingLock.get();

            // 이미 같은 사용자가 선택한 경우
            if (lock.isLockedByUser(userId)) {
                // 만료되지 않은 경우 - 기존 선택 유지 (연장하지 않음)
                if (!lock.isExpired()) {
                    log.info("이미 선택된 좌석 - 좌석 ID: {}, 사용자 ID: {}, 기존 만료 시간: {}",
                            seatId, userId, lock.getExpiresAt());
                    return seat; // 기존 상태 그대로 반환
                }
                // 만료된 경우에만 기존 잠금 해제하고 새로 생성
                seatLockCommandPort.unlock(seatId);
            } else {
                // 다른 사용자가 선택했고 아직 만료되지 않은 경우
                if (!lock.isExpired()) {
                    throw new BadRequestException(ALREADY_SEAT);
                }
                // 만료된 경우 기존 잠금 해제
                seatLockCommandPort.unlock(seatId);
            }
        }

        // 3. 좌석 잠금 생성 (10분 후 만료)
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
        seatLockCommandPort.lock(seatId, userId, expiresAt);

        // 4. 좌석 상태 변경 (SELECTED)
        Seat selectedSeat = seatCommandPort.select(seatId, userId);

        log.info("좌석 선택 완료 - 좌석 ID: {}, 사용자 ID: {}, 만료 시간: {}", seatId, userId, expiresAt);
        return selectedSeat;
    }

    /**
     * 좌석 선택 취소
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 취소된 좌석 정보
     */
    @Override
    @Transactional
    public Seat cancelSeatSelection(Long seatId, Long userId) {
        // 좌석 잠금 조회
        Optional<SeatLock> existingLock = seatLockQueryPort.findBySeatId(seatId);
        if (existingLock.isEmpty()) {
            throw new IllegalStateException("선택되지 않은 좌석입니다.");
        }

        SeatLock lock = existingLock.get();
        // 다른 사용자의 좌석인 경우
        if (!lock.isLockedByUser(userId)) {
            throw new IllegalStateException("다른 사용자가 선택한 좌석은 취소할 수 없습니다.");
        }

        // 좌석 잠금 해제
        seatLockCommandPort.unlock(seatId);

        // 좌석 상태 초기화 (AVAILABLE)
        Seat resetSeat = seatCommandPort.reset(seatId);

        log.info("좌석 선택 취소 완료 - 좌석 ID: {}, 사용자 ID: {}", seatId, userId);
        return resetSeat;
    }

    /**
     * 좌석 잠금 시간 연장
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 연장된 좌석 잠금 정보
     */
    @Override
    @Transactional
    public SeatLock extendSeatLock(Long seatId, Long userId) {
        // 좌석 잠금 조회
        Optional<SeatLock> existingLock = seatLockQueryPort.findBySeatId(seatId);
        if (existingLock.isEmpty()) {
            throw new IllegalStateException("선택되지 않은 좌석입니다.");
        }

        SeatLock lock = existingLock.get();
        // 다른 사용자의 좌석인 경우
        if (!lock.isLockedByUser(userId)) {
            throw new IllegalStateException("다른 사용자가 선택한 좌석은 연장할 수 없습니다.");
        }

        // 만료된 경우
        if (lock.isExpired()) {
            throw new IllegalStateException("만료된 좌석 선택은 연장할 수 없습니다.");
        }

        // 잠금 시간 연장
        SeatLock extendedLock = seatLockCommandPort.extend(seatId, LOCK_DURATION_MINUTES);

        log.info("좌석 잠금 연장 완료 - 좌석 ID: {}, 사용자 ID: {}, 새 만료 시간: {}",
                seatId, userId, extendedLock.getExpiresAt());
        return extendedLock;
    }

    /**
     * 좌석 잠금 정보 조회
     * @param seatId 좌석 ID
     * @return 좌석 잠금 정보
     */
    @Override
    @Transactional(readOnly = true)
    public SeatLock getSeatLock(Long seatId) {
        return seatLockQueryPort.findBySeatId(seatId)
                .orElseThrow(() -> new IllegalStateException("선택되지 않은 좌석입니다."));
    }

    /**
     * 사용자의 모든 좌석 선택 취소
     * @param userId 사용자 ID
     * @return 취소된 좌석 수
     */
    @Override
    @Transactional
    public int cancelAllSeatSelectionsByUser(Long userId) {
        // 사용자가 선택한 모든 좌석 잠금 해제
        int count = seatLockCommandPort.unlockByUserId(userId);
        log.info("사용자의 모든 좌석 선택 취소 완료 - 사용자 ID: {}, 취소된 좌석 수: {}", userId, count);
        return count;
    }

    /**
     * 만료된 모든 좌석 선택 취소
     * @return 취소된 좌석 수
     */
    @Override
    @Transactional
    public int cancelExpiredSeatSelections() {
        // 만료된 모든 좌석 잠금 해제
        int count = seatLockCommandPort.unlockExpired();
        log.info("만료된 모든 좌석 선택 취소 완료 - 취소된 좌석 수: {}", count);
        return count;
    }

    /**
     * 좌석 예약 확정
     * 결제 완료 후 좌석을 영구적으로 예약 확정
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 확정된 좌석 정보
     */
    @Override
    @Transactional
    public Seat confirmSeat(Long seatId, Long userId) {
        // 1. 좌석 잠금 조회
        Optional<SeatLock> existingLock = seatLockQueryPort.findBySeatId(seatId);

        // 2. 좌석 잠금이 없는 경우 (이미 확정되었거나 선택되지 않은 경우)
        if (existingLock.isEmpty()) {
            // 좌석 상태 확인
            Seat seat = seatQueryPort.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

            // 이미 판매된 경우 그대로 반환
            if (seat.isSold()) {
                log.info("이미 판매된 좌석 - 좌석 ID: {}", seatId);
                return seat;
            }

            // 선택되지 않은 경우 오류
            throw new IllegalStateException("선택되지 않은 좌석은 확정할 수 없습니다.");
        }

        // 3. 다른 사용자의 좌석인 경우
        SeatLock lock = existingLock.get();
        if (!lock.isLockedByUser(userId)) {
            throw new IllegalStateException("다른 사용자가 선택한 좌석은 확정할 수 없습니다.");
        }

        // 4. 좌석 잠금 해제 (더 이상 필요 없음)
        seatLockCommandPort.unlock(seatId);

        // 5. 좌석 상태를 판매 완료로 변경
        Seat confirmedSeat = seatCommandPort.sell(seatId);

        log.info("좌석 예약 확정 완료 - 좌석 ID: {}, 사용자 ID: {}", seatId, userId);
        return confirmedSeat;
    }
}
