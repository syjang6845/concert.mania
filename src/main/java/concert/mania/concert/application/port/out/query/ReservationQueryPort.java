package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.type.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예매 조회 포트 인터페이스
 * 예매 관련 조회 기능을 정의
 */
public interface ReservationQueryPort {

    /**
     * ID로 예매 조회
     * @param id 예매 ID
     * @return 예매 정보
     */
    Optional<Reservation> findById(Long id);

    /**
     * 예매 번호로 예매 조회
     * @param reservationNumber 예매 번호
     * @return 예매 정보
     */
    Optional<Reservation> findByReservationNumber(String reservationNumber);

    /**
     * 사용자 ID로 예매 목록 조회
     * @param userId 사용자 ID
     * @return 예매 목록
     */
    List<Reservation> findByUserId(Long userId);

    /**
     * 콘서트 ID로 예매 목록 조회
     * @param concertId 콘서트 ID
     * @return 예매 목록
     */
    List<Reservation> findByConcertId(Long concertId);

    /**
     * 예매 상태로 예매 목록 조회
     * @param status 예매 상태
     * @return 예매 목록
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * 사용자 ID와 예매 상태로 예매 목록 조회
     * @param userId 사용자 ID
     * @param status 예매 상태
     * @return 예매 목록
     */
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    /**
     * 콘서트 ID와 예매 상태로 예매 목록 조회
     * @param concertId 콘서트 ID
     * @param status 예매 상태
     * @return 예매 목록
     */
    List<Reservation> findByConcertIdAndStatus(Long concertId, ReservationStatus status);

    /**
     * 특정 기간 내 생성된 예매 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예매 목록
     */
    List<Reservation> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 특정 기간 내 완료된 예매 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예매 목록
     */
    List<Reservation> findByCompletedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 특정 기간 내 취소된 예매 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예매 목록
     */
    List<Reservation> findByCancelledAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 콘서트 ID로 예매 수 조회
     * @param concertId 콘서트 ID
     * @return 예매 수
     */
    long countByConcertId(Long concertId);

    /**
     * 콘서트 ID와 예매 상태로 예매 수 조회
     * @param concertId 콘서트 ID
     * @param status 예매 상태
     * @return 예매 수
     */
    long countByConcertIdAndStatus(Long concertId, ReservationStatus status);
}