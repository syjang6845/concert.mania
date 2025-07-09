package concert.mania.concert.infrastructure.persistence.jpa.querydsl;

import concert.mania.concert.domain.model.type.QueueStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.WaitingQueueEntryJpaEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 대기열 항목에 대한 동적 쿼리를 위한 커스텀 리포지토리 인터페이스
 */
public interface WaitingQueueEntryCustomRepository {
    
    /**
     * 특정 콘서트의 대기열 항목을 상태와 입장 시간 조건으로 조회
     * 
     * @param concertId 콘서트 ID
     * @param statuses 조회할 상태 목록
     * @param enteredAfter 이 시간 이후에 입장한 항목만 조회
     * @param enteredBefore 이 시간 이전에 입장한 항목만 조회
     * @return 조건에 맞는 대기열 항목 목록
     */
    List<WaitingQueueEntryJpaEntity> findByConcertIdAndStatusesAndEnteredAtRange(
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
    List<WaitingQueueEntryJpaEntity> findByConcertIdAndStatusesAndAdmittedAtRange(
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
    List<WaitingQueueEntryJpaEntity> findByConcertIdAndQueuePositionRange(
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
    List<WaitingQueueEntryJpaEntity> findNextBatchToAdmit(Long concertId, int batchSize);
    
    /**
     * 특정 시간 이후에 만료되는 대기열 항목 조회
     * 
     * @param dateTime 기준 시간
     * @return 해당 시간 이후에 만료되는 대기열 항목 목록
     */
    List<WaitingQueueEntryJpaEntity> findExpirableEntries(LocalDateTime dateTime);
    
    /**
     * 특정 콘서트의 대기열 항목 통계 조회
     * 
     * @param concertId 콘서트 ID
     * @return 상태별 대기열 항목 수를 담은 맵
     */
    java.util.Map<QueueStatus, Long> getQueueStatistics(Long concertId);
}