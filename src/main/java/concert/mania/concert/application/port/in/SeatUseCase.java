package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.SeatLock;
import concert.mania.concert.domain.model.type.SeatStatus;

import java.util.List;
import java.util.Optional;

/**
 * 좌석 유스케이스 인터페이스
 * 좌석 관련 모든 기능을 정의 (명령 및 조회)
 */
public interface SeatUseCase {

    // === 명령(Command) 기능 ===

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

    /**
     * 좌석 잠금 시간 연장
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 연장된 좌석 잠금 정보
     */
    SeatLock extendSeatLock(Long seatId, Long userId);

    // === 조회(Query) 기능 ===

    /**
     * ID로 좌석 조회
     * @param id 좌석 ID
     * @return 좌석 정보
     */
    Optional<Seat> getSeatById(Long id);

    /**
     * 콘서트 ID로 좌석 목록 조회
     * @param concertId 콘서트 ID
     * @return 좌석 목록
     */
    List<Seat> getSeatsByConcertId(Long concertId);

    /**
     * 콘서트 ID와 좌석 상태로 좌석 목록 조회
     * @param concertId 콘서트 ID
     * @param status 좌석 상태
     * @return 좌석 목록
     */
    List<Seat> getSeatsByConcertIdAndStatus(Long concertId, SeatStatus status);

    /**
     * 좌석 등급 ID로 좌석 목록 조회
     * @param seatGradeId 좌석 등급 ID
     * @return 좌석 목록
     */
    List<Seat> getSeatsBySeatGradeId(Long seatGradeId);

    /**
     * 좌석 등급 ID와 좌석 상태로 좌석 목록 조회
     * @param seatGradeId 좌석 등급 ID
     * @param status 좌석 상태
     * @return 좌석 목록
     */
    List<Seat> getSeatsBySeatGradeIdAndStatus(Long seatGradeId, SeatStatus status);

    /**
     * 콘서트 ID와 좌석 등급 ID로 좌석 목록 조회
     * @param concertId 콘서트 ID
     * @param seatGradeId 좌석 등급 ID
     * @return 좌석 목록
     */
    List<Seat> getSeatsByConcertIdAndSeatGradeId(Long concertId, Long seatGradeId);

    /**
     * 콘서트 ID와 좌석 번호로 좌석 조회
     * @param concertId 콘서트 ID
     * @param seatNumber 좌석 번호
     * @return 좌석 정보
     */
    Optional<Seat> getSeatByConcertIdAndSeatNumber(Long concertId, String seatNumber);

    /**
     * 콘서트 ID로 예매 가능한 좌석 수 조회
     * @param concertId 콘서트 ID
     * @return 예매 가능한 좌석 수
     */
    long countAvailableSeatsByConcertId(Long concertId);

    /**
     * 콘서트 ID와 좌석 등급 ID로 예매 가능한 좌석 수 조회
     * @param concertId 콘서트 ID
     * @param seatGradeId 좌석 등급 ID
     * @return 예매 가능한 좌석 수
     */
    long countAvailableSeatsByConcertIdAndSeatGradeId(Long concertId, Long seatGradeId);
}