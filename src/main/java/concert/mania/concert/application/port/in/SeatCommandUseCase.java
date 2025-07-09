package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.SeatLock;

/**
 * 좌석 명령 유스케이스 인터페이스
 * 좌석 선택 및 상태 변경 기능을 정의
 */
public interface SeatCommandUseCase {

    /**
     * 좌석 선택 (임시 점유)
     * 좌석을 선택하면 SELECTED 상태로 변경되고 10분 동안 타이머가 동작함
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 선택된 좌석 정보
     */
    Seat selectSeat(Long seatId, Long userId);

    /**
     * 좌석 선택 취소
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 취소된 좌석 정보
     */
    Seat cancelSeatSelection(Long seatId, Long userId);

    /**
     * 좌석 잠금 정보 조회
     * @param seatId 좌석 ID
     * @return 좌석 잠금 정보
     */
    SeatLock getSeatLock(Long seatId);

    /**
     * 사용자의 모든 좌석 선택 취소
     * @param userId 사용자 ID
     * @return 취소된 좌석 수
     */
    int cancelAllSeatSelectionsByUser(Long userId);

    /**
     * 만료된 모든 좌석 선택 취소
     * @return 취소된 좌석 수
     */
    int cancelExpiredSeatSelections();

    /**
     * 좌석 예약 확정
     * 결제 완료 후 좌석을 영구적으로 예약 확정
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 확정된 좌석 정보
     */
    Seat confirmSeat(Long seatId, Long userId);
}
