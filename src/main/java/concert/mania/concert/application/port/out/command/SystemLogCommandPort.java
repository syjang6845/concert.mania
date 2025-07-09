package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.SystemLog;
import concert.mania.concert.domain.model.type.LogLevel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 시스템 로그 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface SystemLogCommandPort {
    
    /**
     * 시스템 로그 저장
     * 
     * @param log 저장할 시스템 로그
     * @return 저장된 시스템 로그
     */
    SystemLog save(SystemLog log);
    
    /**
     * 시스템 로그 일괄 저장
     * 
     * @param logs 저장할 시스템 로그 목록
     * @return 저장된 시스템 로그 목록
     */
    List<SystemLog> saveAll(List<SystemLog> logs);
    
    /**
     * 시스템 로그 삭제
     * 
     * @param logId 삭제할 시스템 로그 ID
     */
    void delete(Long logId);
    
    /**
     * 특정 시간 이전의 INFO 레벨 로그 삭제
     * 
     * @param dateTime 기준 시간
     * @return 삭제된 시스템 로그 수
     */
    int deleteInfoLogsBefore(LocalDateTime dateTime);
    
    /**
     * 특정 로그 레벨의 로그 삭제
     * 
     * @param logLevel 로그 레벨
     * @param dateTime 기준 시간
     * @return 삭제된 시스템 로그 수
     */
    int deleteLogsByLevelAndBefore(LogLevel logLevel, LocalDateTime dateTime);
    
    /**
     * INFO 레벨 로그 기록
     * 
     * @param logger 로거 이름
     * @param message 로그 메시지
     * @param userId 사용자 ID (선택)
     * @param requestUri 요청 URI (선택)
     * @return 저장된 시스템 로그
     */
    SystemLog logInfo(String logger, String message, String userId, String requestUri);
    
    /**
     * WARN 레벨 로그 기록
     * 
     * @param logger 로거 이름
     * @param message 로그 메시지
     * @param userId 사용자 ID (선택)
     * @param requestUri 요청 URI (선택)
     * @return 저장된 시스템 로그
     */
    SystemLog logWarn(String logger, String message, String userId, String requestUri);
    
    /**
     * ERROR 레벨 로그 기록
     * 
     * @param logger 로거 이름
     * @param message 로그 메시지
     * @param stackTrace 스택 트레이스
     * @param userId 사용자 ID (선택)
     * @param requestUri 요청 URI (선택)
     * @return 저장된 시스템 로그
     */
    SystemLog logError(String logger, String message, String stackTrace, String userId, String requestUri);
    
    /**
     * 시스템 로그 업데이트
     * 
     * @param log 업데이트할 시스템 로그 정보
     * @return 업데이트된 시스템 로그
     */
    SystemLog update(SystemLog log);
}