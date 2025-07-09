package concert.mania.concert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 좌석 잠금 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SeatLock {
    
    private Long id; // 좌석 잠금 고유 식별자
    private Long userId; // 좌석을 잠근 사용자 ID
    private LocalDateTime lockedAt; // 좌석 잠금 시작 시간
    private LocalDateTime expiresAt; // 좌석 잠금 만료 시간 (10분 후 자동 해제)
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    // 관계
    private Seat seat; // 잠금 대상 좌석
    
    /**
     * 잠금이 만료되었는지 확인
     * @return 만료 여부
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * 잠금 시간 연장
     * @param minutes 연장할 시간(분)
     */
    public void extend(int minutes) {
        if (isExpired()) {
            throw new IllegalStateException("만료된 잠금은 연장할 수 없습니다.");
        }
        this.expiresAt = this.expiresAt.plusMinutes(minutes);
    }
    
    /**
     * 특정 사용자의 잠금인지 확인
     * @param userId 확인할 사용자 ID
     * @return 해당 사용자의 잠금 여부
     */
    public boolean isLockedByUser(Long userId) {
        return this.userId.equals(userId);
    }
    
    /**
     * 잠금 생성 후 경과 시간(분) 계산
     * @return 경과 시간(분)
     */
    public long getElapsedMinutes() {
        return java.time.Duration.between(lockedAt, LocalDateTime.now()).toMinutes();
    }
    
    /**
     * 잠금 만료까지 남은 시간(분) 계산
     * @return 남은 시간(분)
     */
    public long getRemainingMinutes() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }
}