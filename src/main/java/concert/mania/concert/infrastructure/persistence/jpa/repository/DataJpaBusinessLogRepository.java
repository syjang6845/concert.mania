package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.infrastructure.persistence.jpa.entity.BusinessLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 비즈니스 로그 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaBusinessLogRepository extends JpaRepository<BusinessLogJpaEntity, Long> {
    
    /**
     * 특정 액션으로 로그 목록 조회
     * 
     * @param action 비즈니스 액션
     * @return 해당 액션의 로그 목록
     */
    List<BusinessLogJpaEntity> findByAction(String action);
    
    /**
     * 특정 엔티티 타입으로 로그 목록 조회
     * 
     * @param entityType 엔티티 타입
     * @return 해당 엔티티 타입의 로그 목록
     */
    List<BusinessLogJpaEntity> findByEntityType(String entityType);
    
    /**
     * 특정 엔티티 ID로 로그 목록 조회
     * 
     * @param entityId 엔티티 ID
     * @return 해당 엔티티 ID의 로그 목록
     */
    List<BusinessLogJpaEntity> findByEntityId(String entityId);
    
    /**
     * 특정 사용자 ID로 로그 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자 ID의 로그 목록
     */
    List<BusinessLogJpaEntity> findByUserId(String userId);
    
    /**
     * 특정 시간 이후의 로그 목록 조회
     * 
     * @param timestamp 기준 시간
     * @return 해당 시간 이후의 로그 목록
     */
    List<BusinessLogJpaEntity> findByTimestampAfter(LocalDateTime timestamp);
    
    /**
     * 특정 시간 이전의 로그 목록 조회
     * 
     * @param timestamp 기준 시간
     * @return 해당 시간 이전의 로그 목록
     */
    List<BusinessLogJpaEntity> findByTimestampBefore(LocalDateTime timestamp);
    
    /**
     * 특정 기간 내의 로그 목록 조회
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 기간 내의 로그 목록
     */
    List<BusinessLogJpaEntity> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 특정 액션과 엔티티 타입으로 로그 목록 조회
     * 
     * @param action 비즈니스 액션
     * @param entityType 엔티티 타입
     * @return 해당 액션과 엔티티 타입의 로그 목록
     */
    List<BusinessLogJpaEntity> findByActionAndEntityType(String action, String entityType);
    
    /**
     * 특정 엔티티 타입과 ID로 로그 목록 조회
     * 
     * @param entityType 엔티티 타입
     * @param entityId 엔티티 ID
     * @return 해당 엔티티 타입과 ID의 로그 목록
     */
    List<BusinessLogJpaEntity> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    /**
     * 특정 액션의 로그 수 조회
     * 
     * @param action 비즈니스 액션
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 로그 수
     */
    @Query("SELECT COUNT(b) FROM BusinessLogJpaEntity b WHERE b.action = :action AND b.timestamp BETWEEN :startTime AND :endTime")
    long countByActionAndTimestampBetween(
            @Param("action") String action,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}