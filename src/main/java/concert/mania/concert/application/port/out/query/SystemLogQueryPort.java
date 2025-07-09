package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.SystemLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 시스템 로그 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface SystemLogQueryPort {
    
    /**
     * ID로 시스템 로그 조회
     * 
     * @param id 시스템 로그 ID
     * @return 시스템 로그 (Optional)
     */
    Optional<SystemLog> findById(Long id);
    
    /**
     * 모든 시스템 로그 조회
     * 
     * @return 시스템 로그 목록
     */
    List<SystemLog> findAll();
    
    /**
     * 특정 로그 레벨의 시스템 로그 조회
     * 
     * @param logLevel 로그 레벨
     * @return 시스템 로그 목록
     */
    List<SystemLog> findByLogLevel(String logLevel);
    
    /**
     * 특정 로거의 시스템 로그 조회
     * 
     * @param logger 로거 이름
     * @return 시스템 로그 목록
     */
    List<SystemLog> findByLogger(String logger);
    
    /**
     * 특정 기간 내의 시스템 로그 조회
     * 
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 시스템 로그 목록
     */
    List<SystemLog> findByTimestampBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * 특정 로그 레벨과 기간 내의 시스템 로그 조회
     * 
     * @param logLevel 로그 레벨
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 시스템 로그 목록
     */
    List<SystemLog> findByLogLevelAndTimestampBetween(String logLevel, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * 특정 사용자의 시스템 로그 조회
     * 
     * @param userId 사용자 ID
     * @return 시스템 로그 목록
     */
    List<SystemLog> findByUserId(String userId);
    
    /**
     * 특정 요청 URI의 시스템 로그 조회
     * 
     * @param requestUri 요청 URI
     * @return 시스템 로그 목록
     */
    List<SystemLog> findByRequestUri(String requestUri);
    
    /**
     * 메시지에 특정 텍스트가 포함된 시스템 로그 조회
     * 
     * @param text 검색할 텍스트
     * @return 시스템 로그 목록
     */
    List<SystemLog> findByMessageContaining(String text);
    
    /**
     * 스택 트레이스가 있는 시스템 로그 조회
     * 
     * @return 시스템 로그 목록
     */
    List<SystemLog> findByStackTraceIsNotNull();
    
    /**
     * 특정 로그 레벨의 시스템 로그 수 조회
     * 
     * @param logLevel 로그 레벨
     * @return 시스템 로그 수
     */
    long countByLogLevel(String logLevel);
    
    /**
     * 특정 기간 내의 시스템 로그 수 조회
     * 
     * @param startDateTime 시작 날짜시간
     * @param endDateTime 종료 날짜시간
     * @return 시스템 로그 수
     */
    long countByTimestampBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}