package concert.mania.concert.infrastructure.web.controller;

import concert.mania.concert.application.port.in.SeatUseCase;
import concert.mania.concert.application.port.in.WaitingQueueQueryUseCase;
import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.SeatLock;
import concert.mania.concert.domain.model.WaitingQueue;
import concert.mania.concert.infrastructure.web.docs.seat.*;
import concert.mania.concert.infrastructure.web.dto.request.SeatSelectionRequest;
import concert.mania.concert.infrastructure.web.dto.response.SeatLockResponse;
import concert.mania.concert.infrastructure.web.dto.response.SeatResponse;
import concert.mania.concert.infrastructure.web.dto.response.SuccessResponse;
import concert.mania.exception.model.BadRequestException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 좌석 컨트롤러
 * 좌석 관련 API를 제공
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class SeatController {

    private final SeatUseCase seatUseCase;
    private final WaitingQueueQueryUseCase waitingQueueQueryUseCase;

    /**
     * 특정 콘서트의 모든 좌석 조회
     */
    @GetMapping("/concerts/{concertId}/seats")
    @GetSeatsByConcertIdApiDoc
    public ResponseEntity<SuccessResponse> getSeatsByConcertId(
            @Parameter(description = "콘서트 ID", required = true) @PathVariable Long concertId,
            @Parameter(description = "좌석 등급 ID (선택사항)") @RequestParam(required = false) Long seatGradeId,
            @Parameter(description = "좌석 상태 (선택사항)") @RequestParam(required = false) String status) {

        log.info("콘서트 좌석 목록 조회 - 콘서트 ID: {}, 좌석 등급 ID: {}, 상태: {}", concertId, seatGradeId, status);

        List<Seat> seats;
        if (seatGradeId != null) {
            seats = seatUseCase.getSeatsByConcertIdAndSeatGradeId(concertId, seatGradeId);
        } else {
            seats = seatUseCase.getSeatsByConcertId(concertId);
        }

        List<SeatResponse> response = SeatResponse.fromList(seats);
        return ResponseEntity.ok(SuccessResponse.of("좌석 목록 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 특정 좌석 등급의 좌석 목록 조회
     */
    @GetMapping("/concerts/{concertId}/seat-grades/{seatGradeId}/seats")
    @GetSeatsByGradeApiDoc
    public ResponseEntity<SuccessResponse> getSeatsByGrade(
            @Parameter(description = "콘서트 ID", required = true) @PathVariable Long concertId,
            @Parameter(description = "좌석 등급 ID", required = true) @PathVariable Long seatGradeId) {

        log.info("좌석 등급별 좌석 목록 조회 - 콘서트 ID: {}, 좌석 등급 ID: {}", concertId, seatGradeId);

        List<Seat> seats = seatUseCase.getSeatsByConcertIdAndSeatGradeId(concertId, seatGradeId);
        List<SeatResponse> response = SeatResponse.fromList(seats);

        return ResponseEntity.ok(SuccessResponse.of("좌석 등급별 좌석 목록 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 좌석 등급 목록 조회
     */
    @GetMapping("/seat-grades/{seatGradeId}/seats")
    @GetSeatsBySeatGradeIdApiDoc
    public ResponseEntity<SuccessResponse> getSeatsBySeatGradeId(
            @Parameter(description = "좌석 등급 ID", required = true) @PathVariable Long seatGradeId) {

        log.info("좌석 등급 좌석 목록 조회 - 좌석 등급 ID: {}", seatGradeId);

        List<Seat> seats = seatUseCase.getSeatsBySeatGradeId(seatGradeId);
        List<SeatResponse> response = SeatResponse.fromList(seats);

        return ResponseEntity.ok(SuccessResponse.of("좌석 등급 좌석 목록 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 좌석 선택 (임시 점유)
     */
    @PostMapping("/seats/{seatId}/select")
    @SelectSeatApiDoc
    public ResponseEntity<SuccessResponse> selectSeat(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long seatId,
            @RequestBody SeatSelectionRequest request) {

        log.info("좌석 선택 요청 - 좌석 ID: {}, 사용자 ID: {}", seatId, request.userId());

        // 좌석 정보 조회하여 콘서트 ID 확인
        Seat seat = seatUseCase.getSeatById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));
        Long concertId = seat.getConcert().getId();

        try {
            // 대기열 상태 확인
            WaitingQueue waitingQueue = waitingQueueQueryUseCase.getWaitingQueueByConcertIdAndUserId(concertId, request.userId());

            // 입장 완료 상태가 아니면 예외 발생
            if (waitingQueue.getStatus() != WaitingQueue.WaitingStatus.ENTERED) {
                throw new BadRequestException("대기열 입장이 완료되지 않은 사용자입니다.");
            }

            // 좌석 선택 처리
            Seat selectedSeat = seatUseCase.selectSeat(seatId, request.userId());
            SeatResponse response = SeatResponse.from(selectedSeat);

            return ResponseEntity.ok(SuccessResponse.of("좌석 선택 성공", HttpStatus.OK.value(), response));
        } catch (IllegalArgumentException e) {
            // 대기열에 등록되지 않은 경우
            throw new BadRequestException("대기열에 등록되지 않은 사용자입니다. 대기열에 먼저 등록해주세요.");
        }
    }


    /**
     * 좌석 잠금 시간 연장
     */
    @PostMapping("/seats/{seatId}/extend")
    @ExtendSeatLockApiDoc
    public ResponseEntity<SuccessResponse> extendSeatLock(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long seatId,
            @RequestBody SeatSelectionRequest request) {

        log.info("좌석 잠금 시간 연장 요청 - 좌석 ID: {}, 사용자 ID: {}", seatId, request.userId());

        // 좌석 정보 조회하여 콘서트 ID 확인
        Seat seat = seatUseCase.getSeatById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));
        Long concertId = seat.getConcert().getId();

        try {
            // 대기열 상태 확인
            WaitingQueue waitingQueue = waitingQueueQueryUseCase.getWaitingQueueByConcertIdAndUserId(concertId, request.userId());

            // 입장 완료 상태가 아니면 예외 발생
            if (waitingQueue.getStatus() != WaitingQueue.WaitingStatus.ENTERED) {
                throw new BadRequestException("대기열 입장이 완료되지 않은 사용자입니다.");
            }

            // 좌석 잠금 시간 연장 처리
            SeatLock extendedLock = seatUseCase.extendSeatLock(seatId, request.userId());
            SeatLockResponse response = SeatLockResponse.from(extendedLock);

            return ResponseEntity.ok(SuccessResponse.of("좌석 잠금 시간 연장 성공", HttpStatus.OK.value(), response));
        } catch (IllegalArgumentException e) {
            // 대기열에 등록되지 않은 경우
            throw new BadRequestException("대기열에 등록되지 않은 사용자입니다. 대기열에 먼저 등록해주세요.");
        }
    }

    /**
     * 좌석 선택 취소
     */
    @PostMapping("/seats/{seatId}/cancel")
    @CancelSeatSelectionApiDoc
    public ResponseEntity<SuccessResponse> cancelSeatSelection(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long seatId,
            @RequestBody SeatSelectionRequest request) {

        log.info("좌석 선택 취소 요청 - 좌석 ID: {}, 사용자 ID: {}", seatId, request.userId());

        // 좌석 정보 조회하여 콘서트 ID 확인
        Seat seat = seatUseCase.getSeatById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));
        Long concertId = seat.getConcert().getId();

        try {
            // 대기열 상태 확인
            WaitingQueue waitingQueue = waitingQueueQueryUseCase.getWaitingQueueByConcertIdAndUserId(concertId, request.userId());

            // 입장 완료 상태가 아니면 예외 발생
            if (waitingQueue.getStatus() != WaitingQueue.WaitingStatus.ENTERED) {
                throw new BadRequestException("대기열 입장이 완료되지 않은 사용자입니다.");
            }

            // 좌석 선택 취소 처리
            Seat canceledSeat = seatUseCase.cancelSeatSelection(seatId, request.userId());
            SeatResponse response = SeatResponse.from(canceledSeat);

            return ResponseEntity.ok(SuccessResponse.of("좌석 선택 취소 성공", HttpStatus.OK.value(), response));
        } catch (IllegalArgumentException e) {
            // 대기열에 등록되지 않은 경우
            throw new BadRequestException("대기열에 등록되지 않은 사용자입니다. 대기열에 먼저 등록해주세요.");
        }
    }

    /**
     * 좌석 잠금 정보 조회
     */
    @GetMapping("/seats/{seatId}/lock")
    @GetSeatLockApiDoc
    public ResponseEntity<SuccessResponse> getSeatLock(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long seatId) {

        log.info("좌석 잠금 정보 조회 요청 - 좌석 ID: {}", seatId);

        SeatLock seatLock = seatUseCase.getSeatLock(seatId);
        SeatLockResponse response = SeatLockResponse.from(seatLock);

        return ResponseEntity.ok(SuccessResponse.of("좌석 잠금 정보 조회 성공", HttpStatus.OK.value(), response));
    }
}
