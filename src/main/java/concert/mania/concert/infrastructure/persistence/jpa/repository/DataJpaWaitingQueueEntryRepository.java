package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.domain.model.type.QueueStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.WaitingQueueEntryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 대기열 항목 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaWaitingQueueEntryRepository extends JpaRepository<WaitingQueueEntryJpaEntity, Long> {
    
    /**
     * 특정 콘서트의 모든 대기열 항목 조회
     * 
     * @param concert 콘서트 엔티티
     * @return 해당 콘서트의 모든 대기열 항목 목록
     */
    List<WaitingQueueEntryJpaEntity> findByConcert(ConcertJpaEntity concert);
    
    /**
     * 특정 콘서트의 특정 상태의 대기열 항목 조회
     * 
     * @param concert 콘서트 엔티티
     * @param status 대기열 상태
     * @return 해당 콘서트의 해당 상태의 대기열 항목 목록
     */
    List<WaitingQueueEntryJpaEntity> findByConcertAndStatus(ConcertJpaEntity concert, QueueStatus status);
    
    /**
     * 특정 사용자의 특정 콘서트에 대한 대기열 항목 조회
     * 
     * @param userId 사용자 ID
     * @param concert 콘서트 엔티티
     * @return 해당 사용자의 해당 콘서트에 대한 대기열 항목 (Optional)
     */
    Optional<WaitingQueueEntryJpaEntity> findByUserIdAndConcert(Long userId, ConcertJpaEntity concert);
    
    /**
     * 특정 콘서트의 대기열 항목을 대기 순번 순으로 조회
     * 
     * @param concert 콘서트 엔티티
     * @return 대기 순번 순으로 정렬된 대기열 항목 목록
     */
    List<WaitingQueueEntryJpaEntity> findByConcertOrderByQueuePositionAsc(ConcertJpaEntity concert);
    
    /**
     * 특정 콘서트의 특정 상태의 대기열 항목을 대기 순번 순으로 조회
     * 
     * @param concert 콘서트 엔티티
     * @param status 대기열 상태
     * @return 대기 순번 순으로 정렬된 대기열 항목 목록
     */
    List<WaitingQueueEntryJpaEntity> findByConcertAndStatusOrderByQueuePositionAsc(ConcertJpaEntity concert, QueueStatus status);
    
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
    @Query("SELECT COUNT(w) FROM WaitingQueueEntryJpaEntity w WHERE w.concert.id = :concertId AND w.queuePosition < :queuePosition AND w.status = 'WAITING'")
    long countQueueAhead(@Param("concertId") Long concertId, @Param("queuePosition") Integer queuePosition);
    
    /**
     * 특정 시간 이전에 입장 허용된 만료되지 않은 대기열 항목 조회
     * 
     * @param dateTime 기준 시간
     * @return 해당 시간 이전에 입장 허용된 대기열 항목 목록
     */
    List<WaitingQueueEntryJpaEntity> findByAdmittedAtBeforeAndStatus(LocalDateTime dateTime, QueueStatus status);
    
    /**
     * 특정 콘서트의 대기열 항목 상태 일괄 변경
     * 
     * @param concertId 콘서트 ID
     * @param fromStatus 변경 전 상태
     * @param toStatus 변경 후 상태
     * @return 변경된 레코드 수
     */
    @Modifying
    @Query("UPDATE WaitingQueueEntryJpaEntity w SET w.status = :toStatus WHERE w.concert.id = :concertId AND w.status = :fromStatus")
    int updateStatusByConcertId(
            @Param("concertId") Long concertId,
            @Param("fromStatus") QueueStatus fromStatus,
            @Param("toStatus") QueueStatus toStatus);
}