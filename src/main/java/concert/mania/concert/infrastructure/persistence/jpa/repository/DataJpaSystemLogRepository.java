package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.infrastructure.persistence.jpa.entity.SystemLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 시스템 로그 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaSystemLogRepository extends JpaRepository<SystemLogJpaEntity, Long> {
    
    /**
     * 특정 로그 레벨로 로그 목록 조회
     * 
     * @param logLevel 로그 레벨 (INFO, WARN, ERROR)
     * @return 해당 레벨의 로그 목록
     */
    List<SystemLogJpaEntity> findByLogLevel(String logLevel);
    
    /**
     * 특정 로거로 로그 목록 조회
     * 
     * @param logger 로거 이름
     * @return 해당 로거의 로그 목록
     */
    List<SystemLogJpaEntity> findByLogger(String logger);
    
    /**
     * 특정 사용자 ID로 로그 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자 ID의 로그 목록
     */
    List<SystemLogJpaEntity> findByUserId(String userId);
    
    /**
     * 특정 요청 URI로 로그 목록 조회
     * 
     * @param requestUri 요청 URI
     * @return 해당 요청 URI의 로그 목록
     */
    List<SystemLogJpaEntity> findByRequestUri(String requestUri);
    
    /**
     * 특정 시간 이후의 로그 목록 조회
     * 
     * @param timestamp 기준 시간
     * @return 해당 시간 이후의 로그 목록
     */
    List<SystemLogJpaEntity> findByTimestampAfter(LocalDateTime timestamp);
    
    /**
     * 특정 시간 이전의 로그 목록 조회
     * 
     * @param timestamp 기준 시간
     * @return 해당 시간 이전의 로그 목록
     */
    List<SystemLogJpaEntity> findByTimestampBefore(LocalDateTime timestamp);
    
    /**
     * 특정 기간 내의 로그 목록 조회
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 기간 내의 로그 목록
     */
    List<SystemLogJpaEntity> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 특정 로그 레벨과 기간으로 로그 목록 조회
     * 
     * @param logLevel 로그 레벨
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 레벨과 기간의 로그 목록
     */
    List<SystemLogJpaEntity> findByLogLevelAndTimestampBetween(
            String logLevel, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 특정 로거와 로그 레벨로 로그 목록 조회
     * 
     * @param logger 로거 이름
     * @param logLevel 로그 레벨
     * @return 해당 로거와 레벨의 로그 목록
     */
    List<SystemLogJpaEntity> findByLoggerAndLogLevel(String logger, String logLevel);
    
    /**
     * 특정 메시지 내용을 포함하는 로그 목록 조회
     * 
     * @param messageContent 메시지 내용
     * @return 해당 내용을 포함하는 로그 목록
     */
    @Query("SELECT s FROM SystemLogJpaEntity s WHERE s.message LIKE %:content%")
    List<SystemLogJpaEntity> findByMessageContaining(@Param("content") String messageContent);
    
    /**
     * 스택 트레이스가 있는 로그 목록 조회
     * 
     * @return 스택 트레이스가 있는 로그 목록
     */
    @Query("SELECT s FROM SystemLogJpaEntity s WHERE s.stackTrace IS NOT NULL AND s.stackTrace <> ''")
    List<SystemLogJpaEntity> findWithStackTrace();
    
    /**
     * 특정 로그 레벨의 로그 수 조회
     * 
     * @param logLevel 로그 레벨
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 로그 수
     */
    @Query("SELECT COUNT(s) FROM SystemLogJpaEntity s WHERE s.logLevel = :logLevel AND s.timestamp BETWEEN :startTime AND :endTime")
    long countByLogLevelAndTimestampBetween(
            @Param("logLevel") String logLevel,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 특정 시간 이전의 INFO 레벨 로그 삭제
     * 
     * @param dateTime 기준 시간
     * @return 삭제된 로그 수
     */
    @Modifying
    @Query("DELETE FROM SystemLogJpaEntity s WHERE s.logLevel = 'INFO' AND s.timestamp < :dateTime")
    int deleteInfoLogsBefore(@Param("dateTime") LocalDateTime dateTime);
}