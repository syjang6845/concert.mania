package concert.mania.concert.infrastructure.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static concert.mania.common.constants.JwtConstants.BEARER_TYPE;

@Builder
@Schema(description = "액세스 토큰 응답")
public record OneTimeTokenResponse(
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
        String onetimeToken,

        @Schema(description = "토큰 타입", example = "Bearer")
        String grantType,

        @Schema(description = "이메일주소")
        String email
) {

    public static OneTimeTokenResponse of(String onetimeToken, String email) {
        return OneTimeTokenResponse.builder()
                .onetimeToken(onetimeToken)
                .grantType(BEARER_TYPE)
                .email(email)
                .build();
    }
}
