package concert.mania.concert.infrastructure.scheduler;

import concert.mania.concert.application.port.in.WaitingQueueCommandUseCase;
import concert.mania.concert.application.port.in.WaitingQueueQueryUseCase;
import concert.mania.concert.domain.model.WaitingQueue;
import concert.mania.concert.infrastructure.messaging.producer.WaitingQueueProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 대기열 스케줄러
 * 일정 간격으로 대기열을 처리하는 스케줄러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueScheduler {

    private final WaitingQueueCommandUseCase waitingQueueCommandUseCase;
    private final WaitingQueueQueryUseCase waitingQueueQueryUseCase;
    private final WaitingQueueProducer waitingQueueProducer;
    
    // 활성화된 콘서트 ID 목록 (실제로는 DB에서 조회하거나 설정 파일에서 관리)
    private static final Long[] ACTIVE_CONCERT_IDS = {1L, 2L, 3L};
    
    // 한 번에 처리할 대기열 수
    private static final int BATCH_SIZE = 10;
    
    /**
     * 대기열 처리 스케줄러
     * 10초마다 실행
     */
    @Scheduled(fixedRate = 10000)
    public void processWaitingQueue() {
        log.info("대기열 처리 스케줄러 실행");
        
        for (Long concertId : ACTIVE_CONCERT_IDS) {
            try {
                // 만료된 대기열 처리
                int expiredCount = waitingQueueCommandUseCase.processExpiredWaitingQueues(concertId);
                if (expiredCount > 0) {
                    log.info("만료된 대기열 처리 완료 - 콘서트 ID: {}, 처리된 수: {}", concertId, expiredCount);
                }
                
                // 대기 중인 사용자 수 확인
                int waitingCount = waitingQueueQueryUseCase.getWaitingQueueCountByStatus(
                        concertId, WaitingQueue.WaitingStatus.WAITING);
                
                if (waitingCount == 0) {
                    continue;
                }
                
                // 처리 중인 사용자 수 확인
                int processingCount = waitingQueueQueryUseCase.getWaitingQueueCountByStatus(
                        concertId, WaitingQueue.WaitingStatus.PROCESSING);
                
                // 처리 중인 사용자가 너무 많으면 스킵
                if (processingCount >= BATCH_SIZE) {
                    log.info("처리 중인 사용자가 너무 많음 - 콘서트 ID: {}, 처리 중인 수: {}", concertId, processingCount);
                    continue;
                }
                
                // 처리할 수 있는 수 계산
                int processCount = Math.min(BATCH_SIZE - processingCount, waitingCount);
                
                // 대기열 처리 메시지 전송
                for (int i = 0; i < processCount; i++) {
                    waitingQueueProducer.sendProcessMessage(concertId);
                }
                
                log.info("대기열 처리 메시지 전송 완료 - 콘서트 ID: {}, 전송 수: {}", concertId, processCount);
            } catch (Exception e) {
                log.error("대기열 처리 중 오류 발생 - 콘서트 ID: {}, 오류: {}", concertId, e.getMessage());
            }
        }
    }
    
    /**
     * 대기열 상태 모니터링 스케줄러
     * 1분마다 실행
     */
    @Scheduled(fixedRate = 60000)
    public void monitorWaitingQueue() {
        log.info("대기열 상태 모니터링 스케줄러 실행");
        
        for (Long concertId : ACTIVE_CONCERT_IDS) {
            try {
                // 대기열 상태별 인원 조회
                int totalCount = waitingQueueQueryUseCase.getWaitingQueueCount(concertId);
                int waitingCount = waitingQueueQueryUseCase.getWaitingQueueCountByStatus(
                        concertId, WaitingQueue.WaitingStatus.WAITING);
                int processingCount = waitingQueueQueryUseCase.getWaitingQueueCountByStatus(
                        concertId, WaitingQueue.WaitingStatus.PROCESSING);
                int enteredCount = waitingQueueQueryUseCase.getWaitingQueueCountByStatus(
                        concertId, WaitingQueue.WaitingStatus.ENTERED);
                int expiredCount = waitingQueueQueryUseCase.getWaitingQueueCountByStatus(
                        concertId, WaitingQueue.WaitingStatus.EXPIRED);
                
                log.info("대기열 상태 - 콘서트 ID: {}, 총 인원: {}, 대기 중: {}, 처리 중: {}, 입장 완료: {}, 만료: {}",
                        concertId, totalCount, waitingCount, processingCount, enteredCount, expiredCount);
            } catch (Exception e) {
                log.error("대기열 상태 모니터링 중 오류 발생 - 콘서트 ID: {}, 오류: {}", concertId, e.getMessage());
            }
        }
    }
}