package concert.mania.concert.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import concert.mania.common.annotations.token.ClearRefreshToken;
import concert.mania.common.annotations.token.SetRefreshToken;
import concert.mania.exception.model.ErrorCode;
import concert.mania.exception.model.UnAuthorizedException;
import concert.mania.concert.application.dto.TokenDto;
import concert.mania.concert.application.port.in.UserAuthUseCase;
import concert.mania.concert.infrastructure.web.docs.userauth.LoginApiDoc;
import concert.mania.concert.infrastructure.web.docs.userauth.LogoutApiDoc;
import concert.mania.concert.infrastructure.web.docs.userauth.RefreshTokenApiDoc;
import concert.mania.concert.infrastructure.web.dto.request.LoginRequest;
import concert.mania.concert.infrastructure.web.dto.response.AccessTokenResponse;
import concert.mania.concert.infrastructure.web.dto.response.UserProfileResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/authentication")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthUseCase userAuthUseCase;

    @PostMapping("/login")
    @LoginApiDoc
    @SetRefreshToken
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest request,
                                                     HttpServletResponse response) {

        log.info("로그인 요청 - email: {}", request.email());
        // 로그인 처리
        TokenDto tokenDto = userAuthUseCase.login(request.toCommand(), response);
        // Access Token만 Response Body로 반환
        AccessTokenResponse accessTokenResponse = AccessTokenResponse.of(tokenDto.getAccessToken() , UserProfileResponse.of(tokenDto.getUser()));

        log.info("로그인 성공 - email: {}", request.email());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accessTokenResponse);
    }

    @PostMapping("/tokens/refresh")
    @RefreshTokenApiDoc
    @SetRefreshToken
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue(name = "${app.jwt.refresh-token-cookie-name:refreshToken}", required = false) String refreshToken,
            HttpServletResponse response) {

        log.debug("토큰 재발급 요청");

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            log.warn("Refresh Token이 쿠키에 없음");
            throw new UnAuthorizedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        // 토큰 재발급
        TokenDto tokenDto = userAuthUseCase.regenerateAccessTokenWithRefreshToken(refreshToken, response);
        // Access Token만 Response Body로 반환

        // 레코드 팩토리 메서드 사용
        AccessTokenResponse accessTokenResponse = AccessTokenResponse.of(tokenDto.getAccessToken(),   UserProfileResponse.of(tokenDto.getUser()));

        log.info("토큰 재발급 성공");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accessTokenResponse);
    }

    @PostMapping("/logout")
    @LogoutApiDoc
    @ClearRefreshToken
    public void logout(@CookieValue(name = "${app.jwt.refresh-token-cookie-name:refreshToken}", required = false) String refreshToken,
                       HttpServletResponse response) {
        log.info("로그아웃 요청");
        // 로그아웃 처리 (토큰 버전 무효화)
        userAuthUseCase.logout(response);
    }

}