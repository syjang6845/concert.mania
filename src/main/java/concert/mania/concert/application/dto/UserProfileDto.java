package concert.mania.concert.application.dto;

import lombok.Builder;
import lombok.Getter;
import concert.mania.concert.domain.model.type.RoleType;

import java.time.LocalDateTime;

/**
 * Data transfer object for user profile information
 */
@Getter
@Builder
public class UserProfileDto {
    private final Long id;
    private final String email;
    private final String name;
    private final String affiliationName;
    private final String imageUrl;
    private final String phoneNumber;
    private final String introduction;
    private final RoleType role;
    private final boolean marketingAgree;
    private final boolean receiveAgree;
    private final boolean isWithdrawn;
    private final LocalDateTime createdAt;

    /**
     * Create a new UserProfileDto
     * @param id The user ID
     * @param email The user's email
     * @param name The user's name
     * @param affiliationName The user's affiliation name
     * @param imageUrl The user's profile image URL
     * @param phoneNumber The user's phone number
     * @param introduction The user's introduction/bio
     * @param role The user's role
     * @param registerType The user's registration type
     * @param marketingAgree Whether the user agrees to marketing communications
     * @param receiveAgree Whether the user agrees to receive notifications
     * @param isWithdrawn Whether the user is withdrawn
     * @param createdAt The date and time the user was created
     */
    public UserProfileDto(Long id, String email, String name, String affiliationName, String imageUrl, 
                          String phoneNumber, String introduction, RoleType role,
                          boolean marketingAgree, boolean receiveAgree, boolean isWithdrawn, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.affiliationName = affiliationName;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.introduction = introduction;
        this.role = role;
        this.marketingAgree = marketingAgree;
        this.receiveAgree = receiveAgree;
        this.isWithdrawn = isWithdrawn;
        this.createdAt = createdAt;
    }
}
