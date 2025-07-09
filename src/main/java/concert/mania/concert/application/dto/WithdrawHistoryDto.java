package concert.mania.concert.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Data transfer object for withdraw history information
 */
@Getter
@Builder
public class WithdrawHistoryDto {
    private final Long id;
    private final Long userId;
    private final String email;
    private final String reason;
    private final LocalDateTime createdAt;
    
    /**
     * Create a new WithdrawHistoryDto
     * @param id The withdraw history ID
     * @param userId The user ID
     * @param email The user's email
     * @param reason The reason for withdrawal (optional)
     * @param createdAt The date and time of withdrawal
     */
    public WithdrawHistoryDto(Long id, Long userId, String email, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.reason = reason;
        this.createdAt = createdAt;
    }
}