package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.type.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예약 유스케이스 인터페이스
 * 예약 관련 모든 기능을 정의 (명령 및 조회)
 */
public interface ReservationUseCase {

    // === 명령(Command) 기능 ===

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

    // === 조회(Query) 기능 ===

    /**
     * ID로 예약 조회
     * @param id 예약 ID
     * @return 예약 정보
     */
    Optional<Reservation> findById(Long id);

    /**
     * 예약 번호로 예약 조회
     * @param reservationNumber 예약 번호
     * @return 예약 정보
     */
    Optional<Reservation> findByReservationNumber(String reservationNumber);

    /**
     * 사용자 ID로 예약 목록 조회
     * @param userId 사용자 ID
     * @return 예약 목록
     */
    List<Reservation> findByUserId(Long userId);

    /**
     * 콘서트 ID로 예약 목록 조회
     * @param concertId 콘서트 ID
     * @return 예약 목록
     */
    List<Reservation> findByConcertId(Long concertId);

    /**
     * 예약 상태로 예약 목록 조회
     * @param status 예약 상태
     * @return 예약 목록
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * 사용자 ID와 예약 상태로 예약 목록 조회
     * @param userId 사용자 ID
     * @param status 예약 상태
     * @return 예약 목록
     */
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    /**
     * 콘서트 ID와 예약 상태로 예약 목록 조회
     * @param concertId 콘서트 ID
     * @param status 예약 상태
     * @return 예약 목록
     */
    List<Reservation> findByConcertIdAndStatus(Long concertId, ReservationStatus status);

    /**
     * 특정 기간 내 생성된 예약 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예약 목록
     */
    List<Reservation> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 특정 기간 내 완료된 예약 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예약 목록
     */
    List<Reservation> findByCompletedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 특정 기간 내 취소된 예약 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예약 목록
     */
    List<Reservation> findByCancelledAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 콘서트 ID로 예약 수 조회
     * @param concertId 콘서트 ID
     * @return 예약 수
     */
    long countByConcertId(Long concertId);

    /**
     * 콘서트 ID와 예약 상태로 예약 수 조회
     * @param concertId 콘서트 ID
     * @param status 예약 상태
     * @return 예약 수
     */
    long countByConcertIdAndStatus(Long concertId, ReservationStatus status);
}