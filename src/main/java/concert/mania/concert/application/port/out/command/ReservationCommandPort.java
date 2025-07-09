package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.type.ReservationStatus;

/**
 * 예매 명령 포트 인터페이스
 * 예매 관련 생성, 수정, 삭제 기능을 정의
 */
public interface ReservationCommandPort {

    /**
     * 예매 정보 저장
     * @param reservation 저장할 예매 정보
     * @return 저장된 예매 정보
     */
    Reservation save(Reservation reservation);

    /**
     * 예매 상태 변경
     * @param reservationId 예매 ID
     * @param status 변경할 상태
     * @return 변경된 예매 정보
     */
    Reservation updateStatus(Long reservationId, ReservationStatus status);

    /**
     * 예매 완료 처리
     * @param reservationId 예매 ID
     * @return 완료 처리된 예매 정보
     */
    Reservation complete(Long reservationId);

    /**
     * 예매 취소 처리
     * @param reservationId 예매 ID
     * @return 취소 처리된 예매 정보
     */
    Reservation cancel(Long reservationId);

    /**
     * 예매 삭제
     * @param reservationId 삭제할 예매 ID
     */
    void delete(Long reservationId);

    /**
     * 예매 번호 생성
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     * @return 생성된 예매 번호
     */
    String generateReservationNumber(Long concertId, Long userId);
}