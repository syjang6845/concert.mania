package concert.mania.concert.infrastructure.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static concert.mania.common.constants.JwtConstants.*;

@Schema(description = "액세스 토큰 응답")
public record AccessTokenResponse(
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,

        @Schema(description = "토큰 타입", example = "Bearer")
        String grantType,

        UserProfileResponse userInfo
) {

    /**
     * AccessTokenResponse 생성 팩토리 메서드
     */
    public static AccessTokenResponse of(String accessToken, UserProfileResponse userInfo) {
        return new AccessTokenResponse(accessToken, BEARER_TYPE, userInfo);
    }
}
