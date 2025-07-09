package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.WaitingQueueEntry;
import concert.mania.concert.domain.model.type.QueueStatus;

import java.util.List;

/**
 * 대기열 항목 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface WaitingQueueEntryCommandPort {
    
    /**
     * 대기열 항목 저장
     * 
     * @param waitingQueueEntry 저장할 대기열 항목
     * @return 저장된 대기열 항목
     */
    WaitingQueueEntry save(WaitingQueueEntry waitingQueueEntry);
    
    /**
     * 대기열 항목 일괄 저장
     * 
     * @param waitingQueueEntries 저장할 대기열 항목 목록
     * @return 저장된 대기열 항목 목록
     */
    List<WaitingQueueEntry> saveAll(List<WaitingQueueEntry> waitingQueueEntries);
    
    /**
     * 대기열 항목 삭제
     * 
     * @param waitingQueueEntryId 삭제할 대기열 항목 ID
     */
    void delete(Long waitingQueueEntryId);
    
    /**
     * 특정 콘서트의 대기열 항목 상태 일괄 변경
     * 
     * @param concertId 콘서트 ID
     * @param fromStatus 변경 전 상태
     * @param toStatus 변경 후 상태
     * @return 변경된 항목 수
     */
    int updateStatusByConcertId(Long concertId, QueueStatus fromStatus, QueueStatus toStatus);
    
    /**
     * 대기열 항목 입장 허용 처리
     * 
     * @param waitingQueueEntryId 대기열 항목 ID
     * @return 업데이트된 대기열 항목
     */
    WaitingQueueEntry admit(Long waitingQueueEntryId);
    
    /**
     * 대기열 항목 만료 처리
     * 
     * @param waitingQueueEntryId 대기열 항목 ID
     * @return 업데이트된 대기열 항목
     */
    WaitingQueueEntry expire(Long waitingQueueEntryId);
    
    /**
     * 대기열 항목 취소 처리
     * 
     * @param waitingQueueEntryId 대기열 항목 ID
     * @return 업데이트된 대기열 항목
     */
    WaitingQueueEntry cancel(Long waitingQueueEntryId);
}