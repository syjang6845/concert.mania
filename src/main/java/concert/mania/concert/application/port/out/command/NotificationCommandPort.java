package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.Notification;
import concert.mania.concert.domain.model.type.NotificationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface NotificationCommandPort {
    
    /**
     * 알림 저장
     * 
     * @param notification 저장할 알림
     * @return 저장된 알림
     */
    Notification save(Notification notification);
    
    /**
     * 알림 일괄 저장
     * 
     * @param notifications 저장할 알림 목록
     * @return 저장된 알림 목록
     */
    List<Notification> saveAll(List<Notification> notifications);
    
    /**
     * 알림 삭제
     * 
     * @param notificationId 삭제할 알림 ID
     */
    void delete(Long notificationId);
    
    /**
     * 특정 시간 이전의 발송 완료된 알림 삭제
     * 
     * @param dateTime 기준 시간
     * @return 삭제된 알림 수
     */
    int deleteSentNotificationsBefore(LocalDateTime dateTime);
    
    /**
     * 알림 상태 업데이트
     * 
     * @param notificationId 알림 ID
     * @param status 변경할 상태
     * @return 업데이트된 알림
     */
    Notification updateStatus(Long notificationId, NotificationStatus status);
    
    /**
     * 알림 발송 완료로 표시
     * 
     * @param notificationId 알림 ID
     * @return 업데이트된 알림
     */
    Notification markAsSent(Long notificationId);
    
    /**
     * 알림 발송 실패로 표시
     * 
     * @param notificationId 알림 ID
     * @return 업데이트된 알림
     */
    Notification markAsFailed(Long notificationId);
    
    /**
     * 실패한 알림 재발송을 위해 상태 초기화
     * 
     * @param notificationId 알림 ID
     * @return 업데이트된 알림
     */
    Notification resetForRetry(Long notificationId);
    
    /**
     * 이메일 알림 생성
     * 
     * @param userId 사용자 ID
     * @param title 알림 제목
     * @param content 알림 내용
     * @return 저장된 알림
     */
    Notification createEmailNotification(Long userId, String title, String content);
    
    /**
     * SMS 알림 생성
     * 
     * @param userId 사용자 ID
     * @param title 알림 제목
     * @param content 알림 내용
     * @return 저장된 알림
     */
    Notification createSmsNotification(Long userId, String title, String content);
    
    /**
     * 알림 업데이트
     * 
     * @param notification 업데이트할 알림 정보
     * @return 업데이트된 알림
     */
    Notification update(Notification notification);
}