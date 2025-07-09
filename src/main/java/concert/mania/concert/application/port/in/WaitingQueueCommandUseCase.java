package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.WaitingQueue;

/**
 * 대기열 명령 유스케이스
 * 대기열 등록, 상태 변경 등의 명령을 정의
 */
public interface WaitingQueueCommandUseCase {
    
    /**
     * 대기열에 사용자 등록
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     * @return 등록된 대기열 정보
     */
    WaitingQueue registerToWaitingQueue(Long concertId, Long userId);
    
    /**
     * 대기열에서 다음 사용자 입장 처리
     * @param concertId 콘서트 ID
     * @return 입장 처리된 대기열 정보
     */
    WaitingQueue processNextWaiting(Long concertId);
    
    /**
     * 특정 대기열 항목 입장 처리
     * @param waitingQueueId 대기열 ID
     * @return 입장 처리된 대기열 정보
     */
    WaitingQueue enterWaitingQueue(Long waitingQueueId);
    
    /**
     * 대기열에서 사용자 제거
     * @param waitingQueueId 대기열 ID
     */
    void removeFromWaitingQueue(Long waitingQueueId);
    
    /**
     * 콘서트의 모든 대기열 초기화
     * @param concertId 콘서트 ID
     * @return 초기화된 대기열 수
     */
    int resetWaitingQueue(Long concertId);
    
    /**
     * 만료된 대기열 항목 처리
     * @param concertId 콘서트 ID
     * @return 처리된 대기열 수
     */
    int processExpiredWaitingQueues(Long concertId);
}