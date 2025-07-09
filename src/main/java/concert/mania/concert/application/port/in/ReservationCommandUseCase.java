package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.type.ReservationStatus;

import java.util.List;

/**
 * 예약 명령 유스케이스 인터페이스
 * 예약 관련 생성, 수정, 삭제 기능을 정의
 */
public interface ReservationCommandUseCase {

    /**
     * 예약 정보 저장
     * @param reservation 저장할 예약 정보
     * @return 저장된 예약 정보
     */
    Reservation save(Reservation reservation);

    /**
     * 예약 상태 변경
     * @param reservationId 예약 ID
     * @param status 변경할 상태
     * @return 변경된 예약 정보
     */
    Reservation updateStatus(Long reservationId, ReservationStatus status);

    /**
     * 예약 확정 처리
     * @param reservationId 예약 ID
     * @param userId 사용자 ID
     * @param concertId 콘서트 ID
     * @param seatIds 좌석 ID 목록
     * @return 완료 처리된 예약 정보
     */
    Reservation complete(Long reservationId, Long userId, Long concertId, List<Long> seatIds);

    /**
     * 예약 취소 처리
     * @param reservationId 예약 ID
     * @param userId 사용자 ID
     * @return 취소 처리된 예약 정보
     */
    Reservation cancel(Long reservationId, Long userId);

    /**
     * 예약 삭제
     * @param reservationId 삭제할 예약 ID
     */
    void delete(Long reservationId);

    /**
     * 예약 번호 생성
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     * @return 생성된 예약 번호
     */
    String generateReservationNumber(Long concertId, Long userId);
}