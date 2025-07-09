package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.SeatLock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 좌석 잠금 조회 포트 인터페이스
 * 좌석 잠금 관련 조회 기능을 정의
 */
public interface SeatLockQueryPort {

    /**
     * ID로 좌석 잠금 조회
     * @param id 좌석 잠금 ID
     * @return 좌석 잠금 정보
     */
    Optional<SeatLock> findById(Long id);

    /**
     * 좌석 ID로 좌석 잠금 조회
     * @param seatId 좌석 ID
     * @return 좌석 잠금 정보
     */
    Optional<SeatLock> findBySeatId(Long seatId);

    /**
     * 사용자 ID로 좌석 잠금 목록 조회
     * @param userId 사용자 ID
     * @return 좌석 잠금 목록
     */
    List<SeatLock> findByUserId(Long userId);

    /**
     * 특정 시간 이전에 만료되는 좌석 잠금 목록 조회
     * @param dateTime 기준 시간
     * @return 좌석 잠금 목록
     */
    List<SeatLock> findByExpiresAtBefore(LocalDateTime dateTime);

    /**
     * 콘서트 ID로 좌석 잠금 목록 조회
     * @param concertId 콘서트 ID
     * @return 좌석 잠금 목록
     */
    List<SeatLock> findByConcertId(Long concertId);

    /**
     * 사용자 ID와 콘서트 ID로 좌석 잠금 목록 조회
     * @param userId 사용자 ID
     * @param concertId 콘서트 ID
     * @return 좌석 잠금 목록
     */
    List<SeatLock> findByUserIdAndConcertId(Long userId, Long concertId);

    /**
     * 특정 좌석이 잠겨있는지 확인
     * @param seatId 좌석 ID
     * @return 잠금 여부
     */
    boolean isLocked(Long seatId);

    /**
     * 특정 좌석이 특정 사용자에 의해 잠겨있는지 확인
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 잠금 여부
     */
    boolean isLockedByUser(Long seatId, Long userId);

    /**
     * 특정 좌석의 잠금이 만료되었는지 확인
     * @param seatId 좌석 ID
     * @return 만료 여부
     */
    boolean isExpired(Long seatId);
}