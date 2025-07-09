package concert.mania.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import concert.mania.concert.application.dto.TokenDto;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.Authority;

@Getter
@Builder
public class JwtToken {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long userId;
    private Authority authority;


    public TokenDto toDto(User user) {
        return TokenDto.builder()
                .accessToken(this.accessToken)
                .refreshToken(this.refreshToken)
                .grantType(this.grantType)
                .user(user)
                .build();
    }
}
