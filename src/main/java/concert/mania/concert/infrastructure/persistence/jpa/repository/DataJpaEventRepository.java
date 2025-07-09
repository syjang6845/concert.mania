package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.domain.model.type.EventStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.EventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 이벤트 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaEventRepository extends JpaRepository<EventJpaEntity, Long> {
    
    /**
     * 특정 이벤트 유형으로 이벤트 목록 조회
     * 
     * @param eventType 이벤트 유형
     * @return 해당 유형의 이벤트 목록
     */
    List<EventJpaEntity> findByEventType(String eventType);
    
    /**
     * 특정 상태로 이벤트 목록 조회
     * 
     * @param status 이벤트 상태
     * @return 해당 상태의 이벤트 목록
     */
    List<EventJpaEntity> findByStatus(EventStatus status);
    
    /**
     * 특정 이벤트 유형과 상태로 이벤트 목록 조회
     * 
     * @param eventType 이벤트 유형
     * @param status 이벤트 상태
     * @return 해당 유형과 상태의 이벤트 목록
     */
    List<EventJpaEntity> findByEventTypeAndStatus(String eventType, EventStatus status);
    
    /**
     * 특정 시간 이후에 생성된 이벤트 목록 조회
     * 
     * @param createdAt 기준 시간
     * @return 해당 시간 이후에 생성된 이벤트 목록
     */
    List<EventJpaEntity> findByCreatedAtAfter(LocalDateTime createdAt);
    
    /**
     * 특정 시간 이후에 처리된 이벤트 목록 조회
     * 
     * @param processedAt 기준 시간
     * @return 해당 시간 이후에 처리된 이벤트 목록
     */
    List<EventJpaEntity> findByProcessedAtAfter(LocalDateTime processedAt);
    
    /**
     * 특정 기간 내에 생성된 이벤트 목록 조회
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 기간 내에 생성된 이벤트 목록
     */
    List<EventJpaEntity> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 처리되지 않은 이벤트 목록 조회 (상태가 PENDING인 이벤트)
     * 
     * @return 처리되지 않은 이벤트 목록
     */
    @Query("SELECT e FROM EventJpaEntity e WHERE e.status = 'PENDING' ORDER BY e.createdAt ASC")
    List<EventJpaEntity> findPendingEvents();
    
    /**
     * 실패한 이벤트 목록 조회 (상태가 FAILED인 이벤트)
     * 
     * @return 실패한 이벤트 목록
     */
    @Query("SELECT e FROM EventJpaEntity e WHERE e.status = 'FAILED' ORDER BY e.createdAt ASC")
    List<EventJpaEntity> findFailedEvents();
    
    /**
     * 특정 이벤트 유형의 처리되지 않은 이벤트 수 조회
     * 
     * @param eventType 이벤트 유형
     * @return 처리되지 않은 이벤트 수
     */
    @Query("SELECT COUNT(e) FROM EventJpaEntity e WHERE e.eventType = :eventType AND e.status = 'PENDING'")
    long countPendingEventsByType(@Param("eventType") String eventType);
    
    /**
     * 특정 이벤트 유형의 실패한 이벤트 수 조회
     * 
     * @param eventType 이벤트 유형
     * @return 실패한 이벤트 수
     */
    @Query("SELECT COUNT(e) FROM EventJpaEntity e WHERE e.eventType = :eventType AND e.status = 'FAILED'")
    long countFailedEventsByType(@Param("eventType") String eventType);
    
    /**
     * 특정 시간 이전에 생성된 처리 완료된 이벤트 삭제
     * 
     * @param dateTime 기준 시간
     * @return 삭제된 이벤트 수
     */
    @Modifying
    @Query("DELETE FROM EventJpaEntity e WHERE e.status = 'PROCESSED' AND e.createdAt < :dateTime")
    int deleteProcessedEventsBefore(@Param("dateTime") LocalDateTime dateTime);
}