package concert.mania.concert.application.service;

import concert.mania.common.util.RefreshTokenHolder;
import concert.mania.exception.model.BadRequestException;
import concert.mania.exception.model.ErrorCode;
import concert.mania.exception.model.UnAuthorizedException;
import concert.mania.jwt.dto.JwtToken;
import concert.mania.jwt.service.JwtTokenService;
import concert.mania.security.service.SecurityService;
import concert.mania.concert.application.command.LoginCommand;
import concert.mania.concert.application.dto.TokenDto;
import concert.mania.concert.application.port.in.UserAuthUseCase;
import concert.mania.concert.application.port.out.query.UserQueryPort;
import concert.mania.concert.application.port.out.redis.RedisAccessTokenPort;
import concert.mania.concert.application.port.out.redis.RedisRefreshTokenPort;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.Authority;
import concert.mania.concert.domain.service.UserAuthDomainService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthApplicationService implements UserAuthUseCase {

    private final RefreshTokenHolder refreshTokenHolder;
    private final UserQueryPort userQueryPort;
    private final SecurityService securityService;
    private final JwtTokenService jwtTokenService;
    private final UserAuthDomainService userAuthDomainService;
    private final RedisAccessTokenPort redisAccessTokenPort;
    private final RedisRefreshTokenPort redisRefreshTokenPort;



    @Override
    public TokenDto login(LoginCommand command, HttpServletResponse response) {
        User user = userQueryPort.findByEmail(command.getEmail())
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.LOGIN_USER_NOT_FOUND));

        if(!user.getRole().equals(command.getRole())) {
            throw new BadRequestException(ErrorCode.USER_ROLE_MISMATCH);
        }

        securityService.createAuthenticationWithLogin(user.getEmail(), command.getPassword());

        JwtToken token = jwtTokenService.generateToken(user, command.isAutoLogin());
        redisAccessTokenPort.saveToken(user.getId(), user.getAuthority(), token.getAccessToken());
        if(command.isAutoLogin()) {
            redisRefreshTokenPort.saveToken(user.getId(), user.getAuthority(), token.getRefreshToken());
        }
        TokenDto tokenDto = token.toDto(user);
        // 쓰레드 로컬에 생성
        refreshTokenHolder.setToken(tokenDto.getRefreshToken());

        return tokenDto;
    }


    @Override
    public TokenDto regenerateAccessTokenWithRefreshToken(String refreshToken, HttpServletResponse response) {
        log.info("Refresh Token으로 토큰 재발급 시작 refreshToken={}", refreshToken);


        try {
            // 새로운 토큰 쌍 생성 (JwtTokenService에서 Refresh Token 검증 포함)
            JwtToken token = jwtTokenService.regenerateTokenWithRefreshToken(refreshToken);
            redisAccessTokenPort.saveToken(token.getUserId(), token.getAuthority(), token.getAccessToken());
            redisRefreshTokenPort.saveToken(token.getUserId(), token.getAuthority(), token.getRefreshToken());

            User user = userQueryPort.findById(token.getUserId())
                    .orElseThrow(() -> new UnAuthorizedException(ErrorCode.LOGIN_USER_NOT_FOUND));

            TokenDto tokenDto = token.toDto(user);

            // 새로운 Refresh Token을 HttpOnly 쿠키로 설정
            refreshTokenHolder.setToken(tokenDto.getRefreshToken());

            return tokenDto;
        } catch (Exception e) {
            log.warn("Refresh Token으로 토큰 재발급 실패: {}", e.getMessage());
            throw e; // Controller에서 401 처리
        }
    }


    @Override
    public void logout(HttpServletResponse response) {
        Long userId = securityService.getCurrentUserId();
        Authority authority = securityService.getCurrentAuthority();

        userAuthDomainService.performLogout(userId, authority, response);
    }



}
