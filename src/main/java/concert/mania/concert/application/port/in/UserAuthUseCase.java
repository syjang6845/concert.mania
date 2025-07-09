package concert.mania.concert.application.port.in;

import jakarta.servlet.http.HttpServletResponse;
import concert.mania.concert.application.command.LoginCommand;
import concert.mania.concert.application.dto.TokenDto;

/**
 * Use case for user authentication
 */
public interface UserAuthUseCase {
    /**
     * 사용자 인증 후 액세스 토큰과 리프레시 토큰 생성
     * @param command 로그인 자격 증명이 포함된 명령
     * @return 생성된 토큰들
     */
    TokenDto login(LoginCommand command, HttpServletResponse response);

    /**
     * 리프레시 토큰을 사용하여 액세스 토큰 재생성
     * @param refreshToken 쿠키의 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰
     */
    TokenDto regenerateAccessTokenWithRefreshToken(String refreshToken, HttpServletResponse response);

    /**
     * 현재 사용자 로그아웃
     * @return 로그아웃된 사용자의 ID
     */
    void logout(HttpServletResponse response);
}