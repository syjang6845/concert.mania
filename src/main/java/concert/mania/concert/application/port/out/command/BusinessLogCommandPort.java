package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.BusinessLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 비즈니스 로그 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface BusinessLogCommandPort {
    
    /**
     * 비즈니스 로그 저장
     * 
     * @param log 저장할 비즈니스 로그
     * @return 저장된 비즈니스 로그
     */
    BusinessLog save(BusinessLog log);
    
    /**
     * 비즈니스 로그 일괄 저장
     * 
     * @param logs 저장할 비즈니스 로그 목록
     * @return 저장된 비즈니스 로그 목록
     */
    List<BusinessLog> saveAll(List<BusinessLog> logs);
    
    /**
     * 비즈니스 로그 삭제
     * 
     * @param logId 삭제할 비즈니스 로그 ID
     */
    void delete(Long logId);
    
    /**
     * 특정 시간 이전의 비즈니스 로그 삭제
     * 
     * @param dateTime 기준 시간
     * @return 삭제된 비즈니스 로그 수
     */
    int deleteLogsBefore(LocalDateTime dateTime);
    
    /**
     * 특정 액션의 비즈니스 로그 삭제
     * 
     * @param action 비즈니스 액션
     * @return 삭제된 비즈니스 로그 수
     */
    int deleteLogsByAction(String action);
    
    /**
     * 특정 엔티티 타입의 비즈니스 로그 삭제
     * 
     * @param entityType 엔티티 타입
     * @return 삭제된 비즈니스 로그 수
     */
    int deleteLogsByEntityType(String entityType);
    
    /**
     * 비즈니스 로그 기록
     * 
     * @param action 비즈니스 액션
     * @param entityType 엔티티 타입
     * @param entityId 엔티티 ID
     * @param userId 사용자 ID
     * @param details 상세 정보 (JSON 형태)
     * @return 저장된 비즈니스 로그
     */
    BusinessLog logAction(String action, String entityType, String entityId, String userId, String details);
    
    /**
     * 비즈니스 로그 업데이트
     * 
     * @param log 업데이트할 비즈니스 로그 정보
     * @return 업데이트된 비즈니스 로그
     */
    BusinessLog update(BusinessLog log);
}