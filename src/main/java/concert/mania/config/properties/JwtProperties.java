package concert.mania.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Data
@Component
public class JwtProperties {
    /**
     * Access Token 유효시간 (초)
     */
    private Long accessTokenValidity = 3600L;

    /**
     * Refresh Token 유효시간 (초)
     */
    private Long refreshTokenValidity = 604800L;
    private String refreshTokenCookieName = "refreshToken";

    /**
     * One Time Token 유효시간 (초)
     */
    private Long oneTimeTokenValidity = 600L;

}
