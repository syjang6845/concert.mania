package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.Event;
import concert.mania.concert.domain.model.type.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 이벤트 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface EventCommandPort {
    
    /**
     * 이벤트 저장
     * 
     * @param event 저장할 이벤트
     * @return 저장된 이벤트
     */
    Event save(Event event);
    
    /**
     * 이벤트 일괄 저장
     * 
     * @param events 저장할 이벤트 목록
     * @return 저장된 이벤트 목록
     */
    List<Event> saveAll(List<Event> events);
    
    /**
     * 이벤트 삭제
     * 
     * @param eventId 삭제할 이벤트 ID
     */
    void delete(Long eventId);
    
    /**
     * 특정 시간 이전의 처리 완료된 이벤트 삭제
     * 
     * @param dateTime 기준 시간
     * @return 삭제된 이벤트 수
     */
    int deleteProcessedEventsBefore(LocalDateTime dateTime);
    
    /**
     * 이벤트 상태 업데이트
     * 
     * @param eventId 이벤트 ID
     * @param status 변경할 상태
     * @return 업데이트된 이벤트
     */
    Event updateStatus(Long eventId, EventStatus status);
    
    /**
     * 이벤트 처리 완료로 표시
     * 
     * @param eventId 이벤트 ID
     * @return 업데이트된 이벤트
     */
    Event markAsProcessed(Long eventId);
    
    /**
     * 이벤트 처리 실패로 표시
     * 
     * @param eventId 이벤트 ID
     * @return 업데이트된 이벤트
     */
    Event markAsFailed(Long eventId);
    
    /**
     * 실패한 이벤트 재처리를 위해 상태 초기화
     * 
     * @param eventId 이벤트 ID
     * @return 업데이트된 이벤트
     */
    Event resetForRetry(Long eventId);
    
    /**
     * 이벤트 발행
     * 
     * @param eventType 이벤트 유형
     * @param payload 이벤트 데이터 (JSON 형태)
     * @return 저장된 이벤트
     */
    Event publishEvent(String eventType, String payload);
    
    /**
     * 이벤트 업데이트
     * 
     * @param event 업데이트할 이벤트 정보
     * @return 업데이트된 이벤트
     */
    Event update(Event event);
}