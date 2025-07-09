package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.WaitingQueue;

/**
 * 대기열 명령 포트
 * 대기열 등록, 상태 변경 등의 명령을 처리
 */
public interface WaitingQueueCommandPort {
    
    /**
     * 대기열에 사용자 등록
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     * @return 등록된 대기열 정보
     */
    WaitingQueue register(Long concertId, Long userId);
    
    /**
     * 대기열 상태를 처리 중으로 변경
     * @param waitingQueueId 대기열 ID
     * @return 업데이트된 대기열 정보
     */
    WaitingQueue process(Long waitingQueueId);
    
    /**
     * 대기열 상태를 입장 완료로 변경
     * @param waitingQueueId 대기열 ID
     * @return 업데이트된 대기열 정보
     */
    WaitingQueue enter(Long waitingQueueId);
    
    /**
     * 대기열 상태를 만료로 변경
     * @param waitingQueueId 대기열 ID
     * @return 업데이트된 대기열 정보
     */
    WaitingQueue expire(Long waitingQueueId);
    
    /**
     * 대기열에서 사용자 제거
     * @param waitingQueueId 대기열 ID
     */
    void remove(Long waitingQueueId);
    
    /**
     * 콘서트의 모든 대기열 초기화
     * @param concertId 콘서트 ID
     * @return 초기화된 대기열 수
     */
    int resetByConcertId(Long concertId);
}