package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.type.SeatStatus;

import java.util.List;

/**
 * 좌석 명령 포트 인터페이스
 * 좌석 관련 생성, 수정, 잠금 기능을 정의
 */
public interface SeatCommandPort {

    /**
     * 좌석 정보 저장
     * @param seat 저장할 좌석 정보
     * @return 저장된 좌석 정보
     */
    Seat save(Seat seat);

    /**
     * 좌석 목록 저장
     * @param seats 저장할 좌석 목록
     * @return 저장된 좌석 목록
     */
    List<Seat> saveAll(List<Seat> seats);

    /**
     * 좌석 상태 변경
     * @param seatId 좌석 ID
     * @param status 변경할 상태
     * @return 변경된 좌석 정보
     */
    Seat updateStatus(Long seatId, SeatStatus status);

    /**
     * 좌석 선택 (임시 점유)
     * @param seatId 좌석 ID
     * @param userId 사용자 ID
     * @return 선택된 좌석 정보
     */
    Seat select(Long seatId, Long userId);

    /**
     * 좌석 예매 진행
     * @param seatId 좌석 ID
     * @return 예매 진행 중인 좌석 정보
     */
    Seat reserve(Long seatId);

    /**
     * 좌석 판매 완료
     * @param seatId 좌석 ID
     * @return 판매 완료된 좌석 정보
     */
    Seat sell(Long seatId);

    /**
     * 좌석 상태 초기화 (예매 취소 등)
     * @param seatId 좌석 ID
     * @return 초기화된 좌석 정보
     */
    Seat reset(Long seatId);

    /**
     * 좌석 삭제
     * @param seatId 삭제할 좌석 ID
     */
    void delete(Long seatId);

    /**
     * 콘서트의 모든 좌석 삭제
     * @param concertId 콘서트 ID
     */
    void deleteByConcertId(Long concertId);
}