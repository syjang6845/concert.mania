package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.SeatLock;

import java.time.LocalDateTime;

/**
 * 좌석 잠금 명령 포트 인터페이스
 * 좌석 잠금 관련 생성, 수정, 삭제 기능을 정의
 */
public interface SeatLockCommandPort {

    /**
     * 좌석 잠금 정보 저장
     * @param seatLock 저장할 좌석 잠금 정보
     * @return 저장된 좌석 잠금 정보
     */
    SeatLock save(SeatLock seatLock);

    /**
     * 좌석 잠금 생성
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @param expiresAt 만료 시간
     * @return 생성된 좌석 잠금 정보
     */
    SeatLock lock(Long seatId, Long userId, LocalDateTime expiresAt);

    /**
     * 좌석 잠금 시간 연장
     * @param seatId 좌석 ID
     * @param minutes 연장할 시간(분)
     * @return 연장된 좌석 잠금 정보
     */
    SeatLock extend(Long seatId, int minutes);

    /**
     * 좌석 잠금 해제
     * @param seatId 좌석 ID
     */
    void unlock(Long seatId);

    /**
     * 사용자의 모든 좌석 잠금 해제
     * @param userId 사용자 ID
     * @return 해제된 좌석 잠금 수
     */
    int unlockByUserId(Long userId);

    /**
     * 콘서트의 모든 좌석 잠금 해제
     * @param concertId 콘서트 ID
     * @return 해제된 좌석 잠금 수
     */
    int unlockByConcertId(Long concertId);

    /**
     * 만료된 모든 좌석 잠금 해제
     * @return 해제된 좌석 잠금 수
     */
    int unlockExpired();
}