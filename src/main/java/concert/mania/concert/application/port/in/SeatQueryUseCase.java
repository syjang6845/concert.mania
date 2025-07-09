package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.type.SeatStatus;

import java.util.List;
import java.util.Optional;

/**
 * 좌석 조회 유스케이스 인터페이스
 * 좌석 관련 조회 기능을 정의
 */
public interface SeatQueryUseCase {

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