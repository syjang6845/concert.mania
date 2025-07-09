package concert.mania.concert.application.service;

import concert.mania.concert.application.port.in.WaitingQueueCommandUseCase;
import concert.mania.concert.application.port.in.WaitingQueueQueryUseCase;
import concert.mania.concert.application.port.out.command.WaitingQueueCommandPort;
import concert.mania.concert.application.port.out.query.WaitingQueueQueryPort;
import concert.mania.concert.domain.model.WaitingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 대기열 서비스
 * 대기열 관련 비즈니스 로직을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueService implements WaitingQueueCommandUseCase, WaitingQueueQueryUseCase {

    private final WaitingQueueCommandPort waitingQueueCommandPort;
    private final WaitingQueueQueryPort waitingQueueQueryPort;

    // ===== Command Methods =====

    /**
     * 대기열에 사용자 등록
     */
    @Override
    @Transactional
    public WaitingQueue registerToWaitingQueue(Long concertId, Long userId) {
        // 이미 등록된 사용자인지 확인
        waitingQueueQueryPort.findByConcertIdAndUserId(concertId, userId)
                .ifPresent(queue -> {
                    throw new IllegalStateException("이미 대기열에 등록된 사용자입니다.");
                });

        // 대기열에 등록
        WaitingQueue waitingQueue = waitingQueueCommandPort.register(concertId, userId);
        log.info("대기열 등록 완료 - 콘서트 ID: {}, 사용자 ID: {}, 대기 위치: {}", 
                concertId, userId, waitingQueue.getPosition());
        
        return waitingQueue;
    }

    /**
     * 대기열에서 다음 사용자 입장 처리
     */
    @Override
    @Transactional
    public WaitingQueue processNextWaiting(Long concertId) {
        // 다음 처리할 대기열 항목 조회
        WaitingQueue nextWaiting = waitingQueueQueryPort.findNextWaiting(concertId)
                .orElseThrow(() -> new IllegalStateException("처리할 대기열이 없습니다."));

        // 처리 중 상태로 변경
        WaitingQueue processing = waitingQueueCommandPort.process(nextWaiting.getId());
        log.info("대기열 처리 중 - 콘서트 ID: {}, 사용자 ID: {}, 대기 위치: {}", 
                concertId, processing.getUserId(), processing.getPosition());
        
        return processing;
    }

    /**
     * 특정 대기열 항목 입장 처리
     */
    @Override
    @Transactional
    public WaitingQueue enterWaitingQueue(Long waitingQueueId) {
        WaitingQueue waitingQueue = waitingQueueQueryPort.findById(waitingQueueId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대기열 항목입니다."));

        // 입장 처리
        WaitingQueue entered = waitingQueueCommandPort.enter(waitingQueueId);
        log.info("대기열 입장 완료 - 대기열 ID: {}, 콘서트 ID: {}, 사용자 ID: {}", 
                waitingQueueId, entered.getConcertId(), entered.getUserId());
        
        return entered;
    }

    /**
     * 대기열에서 사용자 제거
     */
    @Override
    @Transactional
    public void removeFromWaitingQueue(Long waitingQueueId) {
        waitingQueueQueryPort.findById(waitingQueueId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대기열 항목입니다."));

        waitingQueueCommandPort.remove(waitingQueueId);
        log.info("대기열 제거 완료 - 대기열 ID: {}", waitingQueueId);
    }

    /**
     * 콘서트의 모든 대기열 초기화
     */
    @Override
    @Transactional
    public int resetWaitingQueue(Long concertId) {
        int count = waitingQueueCommandPort.resetByConcertId(concertId);
        log.info("대기열 초기화 완료 - 콘서트 ID: {}, 초기화된 대기열 수: {}", concertId, count);
        return count;
    }

    /**
     * 만료된 대기열 항목 처리
     */
    @Override
    @Transactional
    public int processExpiredWaitingQueues(Long concertId) {
        // 처리 중 상태인 항목 중 만료된 항목을 찾아 만료 처리
        List<WaitingQueue> processingQueues = waitingQueueQueryPort.findAllByConcertIdAndStatus(
                concertId, WaitingQueue.WaitingStatus.PROCESSING);
        
        int count = 0;
        for (WaitingQueue queue : processingQueues) {
            // 처리 시간이 너무 오래 걸린 경우 (예: 5분 이상) 만료 처리
            if (queue.getRegisteredAt().plusMinutes(5).isBefore(java.time.LocalDateTime.now())) {
                waitingQueueCommandPort.expire(queue.getId());
                count++;
            }
        }
        
        log.info("만료된 대기열 처리 완료 - 콘서트 ID: {}, 처리된 대기열 수: {}", concertId, count);
        return count;
    }

    // ===== Query Methods =====

    /**
     * 대기열 ID로 대기열 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public WaitingQueue getWaitingQueueById(Long id) {
        return waitingQueueQueryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대기열 항목입니다."));
    }

    /**
     * 콘서트 ID와 사용자 ID로 대기열 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public WaitingQueue getWaitingQueueByConcertIdAndUserId(Long concertId, Long userId) {
        return waitingQueueQueryPort.findByConcertIdAndUserId(concertId, userId)
                .orElseThrow(() -> new IllegalArgumentException("대기열에 등록되지 않은 사용자입니다."));
    }

    /**
     * 콘서트 ID로 모든 대기열 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<WaitingQueue> getAllWaitingQueuesByConcertId(Long concertId) {
        return waitingQueueQueryPort.findAllByConcertId(concertId);
    }

    /**
     * 콘서트 ID와 상태로 대기열 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<WaitingQueue> getWaitingQueuesByConcertIdAndStatus(Long concertId, WaitingQueue.WaitingStatus status) {
        return waitingQueueQueryPort.findAllByConcertIdAndStatus(concertId, status);
    }

    /**
     * 콘서트의 대기열 총 인원 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int getWaitingQueueCount(Long concertId) {
        return waitingQueueQueryPort.countByConcertId(concertId);
    }

    /**
     * 콘서트의 대기 상태별 인원 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int getWaitingQueueCountByStatus(Long concertId, WaitingQueue.WaitingStatus status) {
        return waitingQueueQueryPort.countByConcertIdAndStatus(concertId, status);
    }

    /**
     * 사용자의 대기열 위치 및 예상 대기 시간 조회
     */
    @Override
    @Transactional(readOnly = true)
    public WaitingQueue getWaitingQueueStatus(Long concertId, Long userId) {
        WaitingQueue waitingQueue = waitingQueueQueryPort.findByConcertIdAndUserId(concertId, userId)
                .orElseThrow(() -> new IllegalArgumentException("대기열에 등록되지 않은 사용자입니다."));
        
        // 이미 입장 완료된 경우
        if (waitingQueue.getStatus() == WaitingQueue.WaitingStatus.ENTERED) {
            return waitingQueue;
        }
        
        return waitingQueue;
    }
}