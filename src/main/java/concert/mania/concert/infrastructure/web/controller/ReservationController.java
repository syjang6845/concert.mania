package concert.mania.concert.infrastructure.web.controller;

import concert.mania.concert.application.port.in.ReservationUseCase;
import concert.mania.concert.domain.model.Reservation;
import concert.mania.concert.domain.model.type.ReservationStatus;
import concert.mania.concert.infrastructure.web.dto.request.ReservationConfirmRequest;
import concert.mania.concert.infrastructure.web.dto.response.ReservationResponse;
import concert.mania.concert.infrastructure.web.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import concert.mania.concert.infrastructure.web.docs.reservation.ConfirmReservationApiDoc;
import concert.mania.concert.infrastructure.web.docs.reservation.GetReservationApiDoc;
import concert.mania.concert.infrastructure.web.docs.reservation.GetReservationByNumberApiDoc;
import concert.mania.concert.infrastructure.web.docs.reservation.GetUserReservationsApiDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 예약 컨트롤러
 * 예약 관련 API를 제공
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationUseCase reservationUseCase;

    /**
     * 예약 확정 API
     * 예약 상태를 PENDING에서 COMPLETED로 변경
     * 콘서트 ID와 좌석 ID를 함께 받아서 처리
     */
    @PostMapping("/confirm")
    @ConfirmReservationApiDoc
    public ResponseEntity<SuccessResponse> confirmReservation(@RequestBody ReservationConfirmRequest request) {
        log.info("예약 확정 요청 - 예약 ID: {}, 사용자 ID: {}, 콘서트 ID: {}, 좌석 ID 목록: {}", 
                request.reservationId(), request.userId(), request.concertId(), request.seatIds());

        // 예약 확정 처리 - 비즈니스 로직은 서비스 레이어에서 처리
        Reservation confirmedReservation = reservationUseCase.complete(
                request.reservationId(), 
                request.userId(), 
                request.concertId(), 
                request.seatIds()
        );

        ReservationResponse response = ReservationResponse.from(confirmedReservation);

        return ResponseEntity.ok(SuccessResponse.of("예약 확정 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 예약 조회 API
     * 예약 ID로 예약 정보 조회
     */
    @GetMapping("/{reservationId}")
    @GetReservationApiDoc
    public ResponseEntity<SuccessResponse> getReservation(
            @Parameter(description = "예약 ID", required = true) @PathVariable Long reservationId,
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId) {

        log.info("예약 조회 요청 - 예약 ID: {}, 사용자 ID: {}", reservationId, userId);

        // 예약 정보 조회
        Reservation reservation = reservationUseCase.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 사용자 검증
        if (!reservation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("예약한 사용자만 조회할 수 있습니다.");
        }

        ReservationResponse response = ReservationResponse.from(reservation);
        return ResponseEntity.ok(SuccessResponse.of("예약 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 예약 번호로 예약 조회 API
     */
    @GetMapping("/number/{reservationNumber}")
    @GetReservationByNumberApiDoc
    public ResponseEntity<SuccessResponse> getReservationByNumber(
            @Parameter(description = "예약 번호", required = true) @PathVariable String reservationNumber,
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId) {

        log.info("예약 번호로 예약 조회 요청 - 예약 번호: {}, 사용자 ID: {}", reservationNumber, userId);

        // 예약 정보 조회
        Reservation reservation = reservationUseCase.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 사용자 검증
        if (!reservation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("예약한 사용자만 조회할 수 있습니다.");
        }

        ReservationResponse response = ReservationResponse.from(reservation);
        return ResponseEntity.ok(SuccessResponse.of("예약 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 사용자의 예약 목록 조회 API
     */
    @GetMapping("/user/{userId}")
    @GetUserReservationsApiDoc
    public ResponseEntity<SuccessResponse> getUserReservations(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "예약 상태", required = false) @RequestParam(required = false) ReservationStatus status) {

        log.info("사용자의 예약 목록 조회 요청 - 사용자 ID: {}, 상태: {}", userId, status);

        List<Reservation> reservations;
        if (status != null) {
            reservations = reservationUseCase.findByUserIdAndStatus(userId, status);
        } else {
            reservations = reservationUseCase.findByUserId(userId);
        }

        List<ReservationResponse> response = reservations.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(SuccessResponse.of("예약 목록 조회 성공", HttpStatus.OK.value(), response));
    }
}
