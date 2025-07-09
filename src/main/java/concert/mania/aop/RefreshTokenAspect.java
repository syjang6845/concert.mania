package concert.mania.aop;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import concert.mania.common.annotations.token.ClearRefreshToken;
import concert.mania.common.annotations.token.SetRefreshToken;
import concert.mania.common.util.RefreshTokenCookieManager;
import concert.mania.common.util.RefreshTokenHolder;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RefreshTokenAspect {
    private final RefreshTokenCookieManager cookieManager;
    private final RefreshTokenHolder refreshTokenHolder;


    @AfterReturning("@annotation(clearRefreshToken)")
    public void clearRefreshTokenCookie(JoinPoint joinPoint, ClearRefreshToken clearRefreshToken) {
        HttpServletResponse response = getCurrentResponse();
        if (response != null) {
            cookieManager.clearCookie(response);
        }
    }


    @AfterReturning(value = "@annotation(setRefreshToken)", returning = "result")
    public void setRefreshTokenCookie(JoinPoint joinPoint, SetRefreshToken setRefreshToken, Object result) {
        HttpServletResponse response = getCurrentResponse();
        if (response == null) {
            refreshTokenHolder.clear(); // 실패해도 정리
            return;
        }
        try {
            // ThreadLocal에서 refreshToken 가져오기
            String refreshToken = refreshTokenHolder.getToken();
            if (refreshToken != null) {
                cookieManager.setCookie(response, refreshToken);
                log.debug("RefreshToken 쿠키 설정 완료");
            }
        } catch (Exception e) {
            log.warn("RefreshToken 쿠키 설정 실패: {}", e.getMessage());
        } finally {
            refreshTokenHolder.clear(); // 👈 반드시 정리!
        }
    }


    private HttpServletResponse getCurrentResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getResponse();
        }
        return null;
    }

}
