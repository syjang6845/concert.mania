package concert.mania.concert.domain.model;

import concert.mania.concert.domain.model.type.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Notification {
    
    private Long id; // 알림 고유 식별자
    private Long userId; // 알림 수신자 ID
    private String type; // 알림 유형 (EMAIL, SMS)
    private String title; // 알림 제목
    private String content; // 알림 내용
    private LocalDateTime sentAt; // 알림 발송 시간
    private NotificationStatus status; // 알림 상태 (PENDING, SENT, FAILED)
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    /**
     * 알림이 발송 대기 중인지 확인
     * @return 발송 대기 중 여부
     */
    public boolean isPending() {
        return status == NotificationStatus.PENDING;
    }
    
    /**
     * 알림이 발송 완료되었는지 확인
     * @return 발송 완료 여부
     */
    public boolean isSent() {
        return status == NotificationStatus.SENT;
    }
    
    /**
     * 알림 발송이 실패했는지 확인
     * @return 발송 실패 여부
     */
    public boolean isFailed() {
        return status == NotificationStatus.FAILED;
    }
    
    /**
     * 알림 발송 완료 처리
     */
    public void markAsSent() {
        if (this.status != NotificationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 알림만 발송 완료로 변경할 수 있습니다.");
        }
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }
    
    /**
     * 알림 발송 실패 처리
     */
    public void markAsFailed() {
        if (this.status != NotificationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 알림만 발송 실패로 변경할 수 있습니다.");
        }
        this.status = NotificationStatus.FAILED;
    }
    
    /**
     * 알림 재발송을 위해 상태 초기화
     */
    public void resetForRetry() {
        if (this.status != NotificationStatus.FAILED) {
            throw new IllegalStateException("실패한 알림만 재발송을 위해 초기화할 수 있습니다.");
        }
        this.status = NotificationStatus.PENDING;
    }
    
    /**
     * 이메일 알림인지 확인
     * @return 이메일 알림 여부
     */
    public boolean isEmail() {
        return "EMAIL".equals(this.type);
    }
    
    /**
     * SMS 알림인지 확인
     * @return SMS 알림 여부
     */
    public boolean isSms() {
        return "SMS".equals(this.type);
    }
}