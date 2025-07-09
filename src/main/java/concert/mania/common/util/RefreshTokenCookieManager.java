package concert.mania.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import concert.mania.config.properties.JwtProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCookieManager {
    private final JwtProperties jwtProperties;

    public void setCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtProperties.getRefreshTokenValidity().intValue());
        response.addCookie(cookie);

        log.debug("Refresh Token 쿠키 설정 완료 - name: {}, maxAge: {}초", jwtProperties.getRefreshTokenCookieName(), jwtProperties.getRefreshTokenValidity());
    }

    public void clearCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshTokenCookieName(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        log.debug("Refresh Token 쿠키 삭제 완료 - name: {}", jwtProperties.getRefreshTokenCookieName());
    }


}
