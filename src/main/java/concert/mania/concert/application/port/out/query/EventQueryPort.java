package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.Event;
import concert.mania.concert.domain.model.type.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 이벤트 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface EventQueryPort {
    
    /**
     * ID로 이벤트 조회
     * 
     * @param id 이벤트 ID
     * @return 이벤트 (Optional)
     */
    Optional<Event> findById(Long id);
    
    /**
     * 모든 이벤트 조회
     * 
     * @return 이벤트 목록
     */
    List<Event> findAll();
    
    /**
     * 특정 이벤트 유형으로 이벤트 목록 조회
     * 
     * @param eventType 이벤트 유형
     * @return 해당 유형의 이벤트 목록
     */
    List<Event> findByEventType(String eventType);
    
    /**
     * 특정 상태로 이벤트 목록 조회
     * 
     * @param status 이벤트 상태
     * @return 해당 상태의 이벤트 목록
     */
    List<Event> findByStatus(EventStatus status);
    
    /**
     * 특정 이벤트 유형과 상태로 이벤트 목록 조회
     * 
     * @param eventType 이벤트 유형
     * @param status 이벤트 상태
     * @return 해당 유형과 상태의 이벤트 목록
     */
    List<Event> findByEventTypeAndStatus(String eventType, EventStatus status);
    
    /**
     * 특정 시간 이후에 생성된 이벤트 목록 조회
     * 
     * @param createdAt 기준 시간
     * @return 해당 시간 이후에 생성된 이벤트 목록
     */
    List<Event> findByCreatedAtAfter(LocalDateTime createdAt);
    
    /**
     * 특정 시간 이후에 처리된 이벤트 목록 조회
     * 
     * @param processedAt 기준 시간
     * @return 해당 시간 이후에 처리된 이벤트 목록
     */
    List<Event> findByProcessedAtAfter(LocalDateTime processedAt);
    
    /**
     * 특정 기간 내에 생성된 이벤트 목록 조회
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 기간 내에 생성된 이벤트 목록
     */
    List<Event> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 처리되지 않은 이벤트 목록 조회 (상태가 PENDING인 이벤트)
     * 
     * @return 처리되지 않은 이벤트 목록
     */
    List<Event> findPendingEvents();
    
    /**
     * 실패한 이벤트 목록 조회 (상태가 FAILED인 이벤트)
     * 
     * @return 실패한 이벤트 목록
     */
    List<Event> findFailedEvents();
    
    /**
     * 특정 이벤트 유형의 처리되지 않은 이벤트 수 조회
     * 
     * @param eventType 이벤트 유형
     * @return 처리되지 않은 이벤트 수
     */
    long countPendingEventsByType(String eventType);
    
    /**
     * 특정 이벤트 유형의 실패한 이벤트 수 조회
     * 
     * @param eventType 이벤트 유형
     * @return 실패한 이벤트 수
     */
    long countFailedEventsByType(String eventType);
}