package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.WaitingQueueEntry;
import concert.mania.concert.domain.model.type.QueueStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 대기열 항목 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface WaitingQueueEntryQueryPort {
    
    /**
     * ID로 대기열 항목 조회
     * 
     * @param id 대기열 항목 ID
     * @return 대기열 항목 (Optional)
     */
    Optional<WaitingQueueEntry> findById(Long id);
    
    /**
     * 특정 콘서트의 모든 대기열 항목 조회
     * 
     * @param concertId 콘서트 ID
     * @return 대기열 항목 목록
     */
    List<WaitingQueueEntry> findByConcertId(Long concertId);
    
    /**
     * 특정 콘서트의 특정 상태의 대기열 항목 조회
     * 
     * @param concertId 콘서트 ID
     * @param status 대기열 상태
     * @return 대기열 항목 목록
     */
    List<WaitingQueueEntry> findByConcertIdAndStatus(Long concertId, QueueStatus status);
    
    /**
     * 특정 사용자의 특정 콘서트에 대한 대기열 항목 조회
     * 
     * @param userId 사용자 ID
     * @param concertId 콘서트 ID
     * @return 대기열 항목 (Optional)
     */
    Optional<WaitingQueueEntry> findByUserIdAndConcertId(Long userId, Long concertId);
    
    /**
     * 특정 콘서트의 대기열 항목을 대기 순번 순으로 조회
     * 
     * @param concertId 콘서트 ID
     * @return 대기 순번 순으로 정렬된 대기열 항목 목록
     */
    List<WaitingQueueEntry> findByConcertIdOrderByQueuePosition(Long concertId);
    
    /**
     * 특정 콘서트의 특정 상태의 대기열 항목을 대기 순번 순으로 조회
     * 
     * @param concertId 콘서트 ID
     * @param status 대기열 상태
     * @return 대기 순번 순으로 정렬된 대기열 항목 목록
     */
    List<WaitingQueueEntry> findByConcertIdAndStatusOrderByQueuePosition(Long concertId, QueueStatus status);
    
    /**
     * 특정 콘서트의 대기열 항목 수 조회
     * 
     * @param concertId 콘서트 ID
     * @return 대기열 항목 수
     */
    long countByConcertId(Long concertId);
    
    /**
     * 특정 콘서트의 특정 상태의 대기열 항목 수 조회
     * 
     * @param concertId 콘서트 ID
     * @param status 대기열 상태
     * @return 해당 상태의 대기열 항목 수
     */
    long countByConcertIdAndStatus(Long concertId, QueueStatus status);
    
    /**
     * 특정 콘서트의 특정 사용자보다 앞에 있는 대기열 항목 수 조회
     * 
     * @param concertId 콘서트 ID
     * @param queuePosition 대기 순번
     * @return 앞에 있는 대기열 항목 수
     */
    long countQueueAhead(Long concertId, Integer queuePosition);
    
    /**
     * 특정 콘서트의 대기열 항목을 상태와 입장 시간 조건으로 조회
     * 
     * @param concertId 콘서트 ID
     * @param statuses 조회할 상태 목록
     * @param enteredAfter 이 시간 이후에 입장한 항목만 조회
     * @param enteredBefore 이 시간 이전에 입장한 항목만 조회
     * @return 조건에 맞는 대기열 항목 목록
     */
    List<WaitingQueueEntry> findByConcertIdAndStatusesAndEnteredAtRange(
            Long concertId, 
            List<QueueStatus> statuses, 
            LocalDateTime enteredAfter, 
            LocalDateTime enteredBefore);
    
    /**
     * 특정 콘서트의 대기열 항목을 상태와 입장 허용 시간 조건으로 조회
     * 
     * @param concertId 콘서트 ID
     * @param statuses 조회할 상태 목록
     * @param admittedAfter 이 시간 이후에 입장 허용된 항목만 조회
     * @param admittedBefore 이 시간 이전에 입장 허용된 항목만 조회
     * @return 조건에 맞는 대기열 항목 목록
     */
    List<WaitingQueueEntry> findByConcertIdAndStatusesAndAdmittedAtRange(
            Long concertId, 
            List<QueueStatus> statuses, 
            LocalDateTime admittedAfter, 
            LocalDateTime admittedBefore);
    
    /**
     * 특정 콘서트의 대기열 항목을 대기 순번 범위로 조회
     * 
     * @param concertId 콘서트 ID
     * @param fromPosition 시작 순번 (포함)
     * @param toPosition 종료 순번 (포함)
     * @return 순번 범위에 해당하는 대기열 항목 목록
     */
    List<WaitingQueueEntry> findByConcertIdAndQueuePositionRange(
            Long concertId, 
            Integer fromPosition, 
            Integer toPosition);
    
    /**
     * 특정 콘서트의 대기열에서 다음 입장 허용할 항목 조회
     * 
     * @param concertId 콘서트 ID
     * @param batchSize 한 번에 조회할 항목 수
     * @return 다음 입장 허용할 대기열 항목 목록
     */
    List<WaitingQueueEntry> findNextBatchToAdmit(Long concertId, int batchSize);
    
    /**
     * 특정 시간 이후에 만료되는 대기열 항목 조회
     * 
     * @param dateTime 기준 시간
     * @return 해당 시간 이후에 만료되는 대기열 항목 목록
     */
    List<WaitingQueueEntry> findExpirableEntries(LocalDateTime dateTime);
    
    /**
     * 특정 콘서트의 대기열 항목 통계 조회
     * 
     * @param concertId 콘서트 ID
     * @return 상태별 대기열 항목 수를 담은 맵
     */
    Map<QueueStatus, Long> getQueueStatistics(Long concertId);
}