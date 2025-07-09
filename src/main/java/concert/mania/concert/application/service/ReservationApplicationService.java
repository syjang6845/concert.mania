package concert.mania.concert.application.service;

import concert.mania.concert.application.port.in.ReservationUseCase;
import concert.mania.concert.application.port.out.command.ReservationCommandPort;
import concert.mania.concert.application.port.out.query.ReservationQueryPort;
import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 예약 애플리케이션 서비스
 * 예약 관련 모든 유스케이스를 구현 (명령 및 조회)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationApplicationService implements ReservationUseCase {

    private final ReservationCommandPort reservationCommandPort;
    private final ReservationQueryPort reservationQueryPort;

    // === 명령(Command) 기능 구현 ===

    /**
     * 예약 정보 저장
     * @param reservation 저장할 예약 정보
     * @return 저장된 예약 정보
     */
    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        log.info("예약 정보 저장 - 사용자 ID: {}, 콘서트 ID: {}", 
                reservation.getUserId(), reservation.getConcert().getId());
        return reservationCommandPort.save(reservation);
    }

    /**
     * 예약 상태 변경
     * @param reservationId 예약 ID
     * @param status 변경할 상태
     * @return 변경된 예약 정보
     */
    @Override
    @Transactional
    public Reservation updateStatus(Long reservationId, ReservationStatus status) {
        log.info("예약 상태 변경 - 예약 ID: {}, 상태: {}", reservationId, status);
        return reservationCommandPort.updateStatus(reservationId, status);
    }

    /**
     * 예약 확정 처리
     * @param reservationId 예약 ID
     * @param userId 사용자 ID
     * @param concertId 콘서트 ID
     * @param seatIds 좌석 ID 목록
     * @return 완료 처리된 예약 정보
     */
    @Override
    @Transactional
    public Reservation complete(Long reservationId, Long userId, Long concertId, List<Long> seatIds) {
        log.info("예약 확정 처리 - 예약 ID: {}, 사용자 ID: {}, 콘서트 ID: {}, 좌석 ID 목록: {}", 
                reservationId, userId, concertId, seatIds);

        // 예약 정보 조회
        Reservation reservation = findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 사용자 검증
        if (!reservation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("예약한 사용자만 확정할 수 있습니다.");
        }

        // 콘서트 검증
        if (!reservation.getConcert().getId().equals(concertId)) {
            throw new IllegalArgumentException("예약한 콘서트 정보가 일치하지 않습니다.");
        }

        // 좌석 검증 - 예약 상세 정보에 포함된 좌석 ID와 요청으로 받은 좌석 ID 비교
        List<Long> reservationSeatIds = reservation.getReservationDetails().stream()
                .map(detail -> detail.getSeat().getId())
                .collect(Collectors.toList());
        
        if (!reservationSeatIds.containsAll(seatIds) || !seatIds.containsAll(reservationSeatIds)) {
            throw new IllegalArgumentException("예약한 좌석 정보가 일치하지 않습니다.");
        }

        // 예약 상태 검증
        if (!reservation.isPending()) {
            throw new IllegalStateException("진행 중인 예약만 확정할 수 있습니다.");
        }

        // 예약 확정 처리
        return reservationCommandPort.complete(reservationId);
    }

    /**
     * 예약 취소 처리
     * @param reservationId 예약 ID
     * @param userId 사용자 ID
     * @return 취소 처리된 예약 정보
     */
    @Override
    @Transactional
    public Reservation cancel(Long reservationId, Long userId) {
        log.info("예약 취소 처리 - 예약 ID: {}, 사용자 ID: {}", reservationId, userId);

        // 예약 정보 조회
        Reservation reservation = findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 사용자 검증
        if (!reservation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("예약한 사용자만 취소할 수 있습니다.");
        }

        // 예약 취소 처리
        return reservationCommandPort.cancel(reservationId);
    }

    /**
     * 예약 삭제
     * @param reservationId 삭제할 예약 ID
     */
    @Override
    @Transactional
    public void delete(Long reservationId) {
        log.info("예약 삭제 - 예약 ID: {}", reservationId);
        reservationCommandPort.delete(reservationId);
    }

    /**
     * 예약 번호 생성
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     * @return 생성된 예약 번호
     */
    @Override
    public String generateReservationNumber(Long concertId, Long userId) {
        log.info("예약 번호 생성 - 콘서트 ID: {}, 사용자 ID: {}", concertId, userId);
        return reservationCommandPort.generateReservationNumber(concertId, userId);
    }

    // === 조회(Query) 기능 구현 ===

    /**
     * ID로 예약 조회
     * @param id 예약 ID
     * @return 예약 정보
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> findById(Long id) {
        log.info("ID로 예약 조회 - 예약 ID: {}", id);
        return reservationQueryPort.findById(id);
    }

    /**
     * 예약 번호로 예약 조회
     * @param reservationNumber 예약 번호
     * @return 예약 정보
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> findByReservationNumber(String reservationNumber) {
        log.info("예약 번호로 예약 조회 - 예약 번호: {}", reservationNumber);
        return reservationQueryPort.findByReservationNumber(reservationNumber);
    }

    /**
     * 사용자 ID로 예약 목록 조회
     * @param userId 사용자 ID
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByUserId(Long userId) {
        log.info("사용자 ID로 예약 목록 조회 - 사용자 ID: {}", userId);
        return reservationQueryPort.findByUserId(userId);
    }

    /**
     * 콘서트 ID로 예약 목록 조회
     * @param concertId 콘서트 ID
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByConcertId(Long concertId) {
        log.info("콘서트 ID로 예약 목록 조회 - 콘서트 ID: {}", concertId);
        return reservationQueryPort.findByConcertId(concertId);
    }

    /**
     * 예약 상태로 예약 목록 조회
     * @param status 예약 상태
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByStatus(ReservationStatus status) {
        log.info("예약 상태로 예약 목록 조회 - 상태: {}", status);
        return reservationQueryPort.findByStatus(status);
    }

    /**
     * 사용자 ID와 예약 상태로 예약 목록 조회
     * @param userId 사용자 ID
     * @param status 예약 상태
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status) {
        log.info("사용자 ID와 예약 상태로 예약 목록 조회 - 사용자 ID: {}, 상태: {}", userId, status);
        return reservationQueryPort.findByUserIdAndStatus(userId, status);
    }

    /**
     * 콘서트 ID와 예약 상태로 예약 목록 조회
     * @param concertId 콘서트 ID
     * @param status 예약 상태
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByConcertIdAndStatus(Long concertId, ReservationStatus status) {
        log.info("콘서트 ID와 예약 상태로 예약 목록 조회 - 콘서트 ID: {}, 상태: {}", concertId, status);
        return reservationQueryPort.findByConcertIdAndStatus(concertId, status);
    }

    /**
     * 특정 기간 내 생성된 예약 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("특정 기간 내 생성된 예약 목록 조회 - 시작: {}, 종료: {}", startDateTime, endDateTime);
        return reservationQueryPort.findByCreatedAtBetween(startDateTime, endDateTime);
    }

    /**
     * 특정 기간 내 완료된 예약 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByCompletedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("특정 기간 내 완료된 예약 목록 조회 - 시작: {}, 종료: {}", startDateTime, endDateTime);
        return reservationQueryPort.findByCompletedAtBetween(startDateTime, endDateTime);
    }

    /**
     * 특정 기간 내 취소된 예약 목록 조회
     * @param startDateTime 시작 일시
     * @param endDateTime 종료 일시
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByCancelledAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("특정 기간 내 취소된 예약 목록 조회 - 시작: {}, 종료: {}", startDateTime, endDateTime);
        return reservationQueryPort.findByCancelledAtBetween(startDateTime, endDateTime);
    }

    /**
     * 콘서트 ID로 예약 수 조회
     * @param concertId 콘서트 ID
     * @return 예약 수
     */
    @Override
    @Transactional(readOnly = true)
    public long countByConcertId(Long concertId) {
        log.info("콘서트 ID로 예약 수 조회 - 콘서트 ID: {}", concertId);
        return reservationQueryPort.countByConcertId(concertId);
    }

    /**
     * 콘서트 ID와 예약 상태로 예약 수 조회
     * @param concertId 콘서트 ID
     * @param status 예약 상태
     * @return 예약 수
     */
    @Override
    @Transactional(readOnly = true)
    public long countByConcertIdAndStatus(Long concertId, ReservationStatus status) {
        log.info("콘서트 ID와 예약 상태로 예약 수 조회 - 콘서트 ID: {}, 상태: {}", concertId, status);
        return reservationQueryPort.countByConcertIdAndStatus(concertId, status);
    }
}