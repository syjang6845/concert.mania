package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.Notification;
import concert.mania.concert.domain.model.type.NotificationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface NotificationQueryPort {
    
    /**
     * ID로 알림 조회
     * 
     * @param id 알림 ID
     * @return 알림 (Optional)
     */
    Optional<Notification> findById(Long id);
    
    /**
     * 모든 알림 조회
     * 
     * @return 알림 목록
     */
    List<Notification> findAll();
    
    /**
     * 특정 사용자의 알림 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 알림 목록
     */
    List<Notification> findByUserId(Long userId);
    
    /**
     * 특정 알림 유형으로 알림 목록 조회
     * 
     * @param type 알림 유형 (EMAIL, SMS)
     * @return 해당 유형의 알림 목록
     */
    List<Notification> findByType(String type);
    
    /**
     * 특정 상태로 알림 목록 조회
     * 
     * @param status 알림 상태
     * @return 해당 상태의 알림 목록
     */
    List<Notification> findByStatus(NotificationStatus status);
    
    /**
     * 특정 사용자와 상태로 알림 목록 조회
     * 
     * @param userId 사용자 ID
     * @param status 알림 상태
     * @return 해당 사용자와 상태의 알림 목록
     */
    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
    
    /**
     * 특정 유형과 상태로 알림 목록 조회
     * 
     * @param type 알림 유형
     * @param status 알림 상태
     * @return 해당 유형과 상태의 알림 목록
     */
    List<Notification> findByTypeAndStatus(String type, NotificationStatus status);
    
    /**
     * 특정 시간 이후에 생성된 알림 목록 조회
     * 
     * @param createdAt 기준 시간
     * @return 해당 시간 이후에 생성된 알림 목록
     */
    List<Notification> findByCreatedAtAfter(LocalDateTime createdAt);
    
    /**
     * 특정 시간 이후에 발송된 알림 목록 조회
     * 
     * @param sentAt 기준 시간
     * @return 해당 시간 이후에 발송된 알림 목록
     */
    List<Notification> findBySentAtAfter(LocalDateTime sentAt);
    
    /**
     * 특정 기간 내에 생성된 알림 목록 조회
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 해당 기간 내에 생성된 알림 목록
     */
    List<Notification> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 발송 대기 중인 알림 목록 조회 (상태가 PENDING인 알림)
     * 
     * @return 발송 대기 중인 알림 목록
     */
    List<Notification> findPendingNotifications();
    
    /**
     * 발송 실패한 알림 목록 조회 (상태가 FAILED인 알림)
     * 
     * @return 발송 실패한 알림 목록
     */
    List<Notification> findFailedNotifications();
    
    /**
     * 특정 사용자의 발송 대기 중인 알림 수 조회
     * 
     * @param userId 사용자 ID
     * @return 발송 대기 중인 알림 수
     */
    long countPendingNotificationsByUserId(Long userId);
}