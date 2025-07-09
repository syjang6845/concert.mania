package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.BusinessLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 비즈니스 로그 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface BusinessLogQueryPort {
    
    /**
     * ID로 비즈니스 로그 조회
     * 
     * @param id 비즈니스 로그 ID
     * @return 비즈니스 로그 (Optional)
     */
    Optional<BusinessLog> findById(Long id);
    
    /**
     * 모든 비즈니스 로그 조회
     * 
     * @return 비즈니스 로그 목록
     */
    List<BusinessLog> findAll();
    
    /**
     * 특정 액션의 비즈니스 로그 조회
     * 
     * @param action 비즈니스 액션
     * @return 해당 액션의 비즈니스 로그 목록
     */
    List<BusinessLog> findByAction(String action);
    
    /**
     * 특정 엔티티 타입의 비즈니스 로그 조회
     * 
     * @param entityType 엔티티 타입
     * @return 해당 엔티티 타입의 비즈니스 로그 목록
     */
    List<BusinessLog> findByEntityType(String entityType);
    
    /**
     * 특정 엔티티 ID의 비즈니스 로그 조회
     * 
     * @param entityId 엔티티 ID
     * @return 해당 엔티티 ID의 비즈니스 로그 목록
     */
    List<BusinessLog> findByEntityId(String entityId);
    
    /**
     * 특정 사용자 ID의 비즈니스 로그 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자 ID의 비즈니스 로그 목록
     */
    List<BusinessLog> findByUserId(String userId);
    
    /**
     * 특정 시간 이후의 비즈니스 로그 조회
     * 
     * @param timestamp 기준 시간
     * @return 해당 시간 이후의 비즈니스 로그 목록
     */
    List<BusinessLog> findByTimestampAfter(LocalDateTime timestamp);
    
    /**
     * 특정 시간 이전의 비즈니스 로그 조회
     * 
     * @param timestamp 기준 시간
     * @return 해당 시간 이전의 비즈니스 로그 목록
     */
    List<BusinessLog> findByTimestampBefore(LocalDateTime timestamp);
    
    /**
     * 특정 기간 내의 비즈니스 로그 조회
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 기간 내의 비즈니스 로그 목록
     */
    List<BusinessLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 특정 액션과 엔티티 타입의 비즈니스 로그 조회
     * 
     * @param action 비즈니스 액션
     * @param entityType 엔티티 타입
     * @return 해당 액션과 엔티티 타입의 비즈니스 로그 목록
     */
    List<BusinessLog> findByActionAndEntityType(String action, String entityType);
    
    /**
     * 특정 엔티티 타입과 ID의 비즈니스 로그 조회
     * 
     * @param entityType 엔티티 타입
     * @param entityId 엔티티 ID
     * @return 해당 엔티티 타입과 ID의 비즈니스 로그 목록
     */
    List<BusinessLog> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    /**
     * 특정 액션의 비즈니스 로그 수 조회
     * 
     * @param action 비즈니스 액션
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 비즈니스 로그 수
     */
    long countByActionAndTimestampBetween(String action, LocalDateTime startTime, LocalDateTime endTime);
}