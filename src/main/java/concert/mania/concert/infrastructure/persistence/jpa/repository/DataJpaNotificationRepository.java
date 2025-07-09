package concert.mania.concert.infrastructure.persistence.jpa.repository;

import concert.mania.concert.domain.model.type.NotificationStatus;
import concert.mania.concert.infrastructure.persistence.jpa.entity.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 JPA 엔티티에 대한 Spring Data JPA 리포지토리
 */
public interface DataJpaNotificationRepository extends JpaRepository<NotificationJpaEntity, Long> {
    
    /**
     * 특정 사용자의 알림 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 알림 목록
     */
    List<NotificationJpaEntity> findByUserId(Long userId);
    
    /**
     * 특정 알림 유형으로 알림 목록 조회
     * 
     * @param type 알림 유형 (EMAIL, SMS)
     * @return 해당 유형의 알림 목록
     */
    List<NotificationJpaEntity> findByType(String type);
    
    /**
     * 특정 상태로 알림 목록 조회
     * 
     * @param status 알림 상태
     * @return 해당 상태의 알림 목록
     */
    List<NotificationJpaEntity> findByStatus(NotificationStatus status);
    
    /**
     * 특정 사용자와 상태로 알림 목록 조회
     * 
     * @param userId 사용자 ID
     * @param status 알림 상태
     * @return 해당 사용자와 상태의 알림 목록
     */
    List<NotificationJpaEntity> findByUserIdAndStatus(Long userId, NotificationStatus status);
    
    /**
     * 특정 유형과 상태로 알림 목록 조회
     * 
     * @param type 알림 유형
     * @param status 알림 상태
     * @return 해당 유형과 상태의 알림 목록
     */
    List<NotificationJpaEntity> findByTypeAndStatus(String type, NotificationStatus status);
    
    /**
     * 특정 시간 이후에 생성된 알림 목록 조회
     * 
     * @param createdAt 기준 시간
     * @return 해당 시간 이후에 생성된 알림 목록
     */
    List<NotificationJpaEntity> findByCreatedAtAfter(LocalDateTime createdAt);
    
    /**
     * 특정 시간 이후에 발송된 알림 목록 조회
     * 
     * @param sentAt 기준 시간
     * @return 해당 시간 이후에 발송된 알림 목록
     */
    List<NotificationJpaEntity> findBySentAtAfter(LocalDateTime sentAt);
    
    /**
     * 특정 기간 내에 생성된 알림 목록 조회
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 기간 내에 생성된 알림 목록
     */
    List<NotificationJpaEntity> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 발송 대기 중인 알림 목록 조회 (상태가 PENDING인 알림)
     * 
     * @return 발송 대기 중인 알림 목록
     */
    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.status = 'PENDING' ORDER BY n.createdAt ASC")
    List<NotificationJpaEntity> findPendingNotifications();
    
    /**
     * 발송 실패한 알림 목록 조회 (상태가 FAILED인 알림)
     * 
     * @return 발송 실패한 알림 목록
     */
    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.status = 'FAILED' ORDER BY n.createdAt ASC")
    List<NotificationJpaEntity> findFailedNotifications();
    
    /**
     * 특정 사용자의 발송 대기 중인 알림 수 조회
     * 
     * @param userId 사용자 ID
     * @return 발송 대기 중인 알림 수
     */
    @Query("SELECT COUNT(n) FROM NotificationJpaEntity n WHERE n.userId = :userId AND n.status = 'PENDING'")
    long countPendingNotificationsByUserId(@Param("userId") Long userId);
    
    /**
     * 특정 시간 이전에 발송 완료된 알림 삭제
     * 
     * @param dateTime 기준 시간
     * @return 삭제된 알림 수
     */
    @Modifying
    @Query("DELETE FROM NotificationJpaEntity n WHERE n.status = 'SENT' AND n.sentAt < :dateTime")
    int deleteSentNotificationsBefore(@Param("dateTime") LocalDateTime dateTime);
}