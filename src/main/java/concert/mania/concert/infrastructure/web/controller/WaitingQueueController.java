package concert.mania.concert.infrastructure.web.controller;

import concert.mania.concert.application.port.in.WaitingQueueCommandUseCase;
import concert.mania.concert.application.port.in.WaitingQueueQueryUseCase;
import concert.mania.concert.domain.model.WaitingQueue;
import concert.mania.concert.infrastructure.messaging.producer.WaitingQueueProducer;
import concert.mania.concert.infrastructure.web.dto.request.WaitingQueueRegisterRequest;
import concert.mania.concert.infrastructure.web.dto.response.SuccessResponse;
import concert.mania.concert.infrastructure.web.dto.response.WaitingQueueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 대기열 컨트롤러
 * 대기열 관련 API를 제공
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueController {

    private final WaitingQueueCommandUseCase waitingQueueCommandUseCase;
    private final WaitingQueueQueryUseCase waitingQueueQueryUseCase;
    private final WaitingQueueProducer waitingQueueProducer;

    /**
     * 대기열 등록
     */
    @PostMapping("/concerts/{concertId}/waiting-queue")
    public ResponseEntity<SuccessResponse> registerToWaitingQueue(
            @Parameter(description = "콘서트 ID", required = true) @PathVariable Long concertId,
            @RequestBody WaitingQueueRegisterRequest request) {

        log.info("대기열 등록 요청 - 콘서트 ID: {}, 사용자 ID: {}", concertId, request.userId());

        // 비동기 처리를 위해 메시지 큐에 전송
        waitingQueueProducer.sendRegisterMessage(concertId, request.userId());

        return ResponseEntity.ok(SuccessResponse.of("대기열 등록 요청이 접수되었습니다.", HttpStatus.OK.value(), null));
    }

    /**
     * 대기열 상태 조회
     */
    @GetMapping("/concerts/{concertId}/waiting-queue/status")
    public ResponseEntity<SuccessResponse> getWaitingQueueStatus(
            @Parameter(description = "콘서트 ID", required = true) @PathVariable Long concertId,
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId) {

        log.info("대기열 상태 조회 요청 - 콘서트 ID: {}, 사용자 ID: {}", concertId, userId);

        WaitingQueue waitingQueue = waitingQueueQueryUseCase.getWaitingQueueStatus(concertId, userId);
        WaitingQueueResponse response = WaitingQueueResponse.from(waitingQueue);

        return ResponseEntity.ok(SuccessResponse.of("대기열 상태 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 대기열 목록 조회 (관리자용)
     */
    @GetMapping("/admin/concerts/{concertId}/waiting-queue")
    public ResponseEntity<SuccessResponse> getAllWaitingQueues(
            @Parameter(description = "콘서트 ID", required = true) @PathVariable Long concertId,
            @Parameter(description = "대기 상태 (선택사항)") @RequestParam(required = false) WaitingQueue.WaitingStatus status) {

        log.info("대기열 목록 조회 요청 - 콘서트 ID: {}, 상태: {}", concertId, status);

        List<WaitingQueue> waitingQueues;
        if (status != null) {
            waitingQueues = waitingQueueQueryUseCase.getWaitingQueuesByConcertIdAndStatus(concertId, status);
        } else {
            waitingQueues = waitingQueueQueryUseCase.getAllWaitingQueuesByConcertId(concertId);
        }

        List<WaitingQueueResponse> response = waitingQueues.stream()
                .map(WaitingQueueResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(SuccessResponse.of("대기열 목록 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 다음 대기열 처리 (관리자용)
     */
    @PostMapping("/admin/concerts/{concertId}/waiting-queue/process-next")
    public ResponseEntity<SuccessResponse> processNextWaiting(
            @Parameter(description = "콘서트 ID", required = true) @PathVariable Long concertId) {

        log.info("다음 대기열 처리 요청 - 콘서트 ID: {}", concertId);

        // 비동기 처리를 위해 메시지 큐에 전송
        waitingQueueProducer.sendProcessMessage(concertId);

        return ResponseEntity.ok(SuccessResponse.of("다음 대기열 처리 요청이 접수되었습니다.", HttpStatus.OK.value(), null));
    }

    /**
     * 대기열 입장 처리 (관리자용)
     */
    @PostMapping("/admin/waiting-queue/{waitingQueueId}/enter")
    public ResponseEntity<SuccessResponse> enterWaitingQueue(
            @Parameter(description = "대기열 ID", required = true) @PathVariable Long waitingQueueId) {

        log.info("대기열 입장 처리 요청 - 대기열 ID: {}", waitingQueueId);

        // 비동기 처리를 위해 메시지 큐에 전송
        waitingQueueProducer.sendEnterMessage(waitingQueueId);

        return ResponseEntity.ok(SuccessResponse.of("대기열 입장 처리 요청이 접수되었습니다.", HttpStatus.OK.value(), null));
    }

    /**
     * 대기열 초기화 (관리자용)
     */
    @PostMapping("/admin/concerts/{concertId}/waiting-queue/reset")
    public ResponseEntity<SuccessResponse> resetWaitingQueue(
            @Parameter(description = "콘서트 ID", required = true) @PathVariable Long concertId) {

        log.info("대기열 초기화 요청 - 콘서트 ID: {}", concertId);

        int count = waitingQueueCommandUseCase.resetWaitingQueue(concertId);

        return ResponseEntity.ok(SuccessResponse.of("대기열 초기화 성공", HttpStatus.OK.value(), count));
    }
}