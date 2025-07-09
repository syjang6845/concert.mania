package concert.mania.concert.application.dto;

import lombok.Builder;
import lombok.Getter;
import concert.mania.concert.domain.model.User;

/**
 * Data transfer object for authentication tokens
 */
@Getter
@Builder
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;
    private final String grantType;
    private final User user;
    
    /**
     * Create a new TokenDto
     * @param accessToken The access token
     * @param refreshToken The refresh token
     * @param grantType The token type (e.g., "Bearer")
     */
    public TokenDto(String accessToken, String refreshToken, String grantType, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.user = user;
    }
}