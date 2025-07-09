package concert.mania.concert.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OneTimeTokenDto {
    private final String onetimeToken;
    private final String tokenType;
    private final String email;

    /**
     * Create a new TokenDto
     * @param onetimeToken The access token
     * @param tokenType The token type (e.g., "Bearer")
     */
    public OneTimeTokenDto(String onetimeToken, String tokenType, String email) {
        this.onetimeToken = onetimeToken;
        this.tokenType = tokenType;
        this.email = email;
    }
}
