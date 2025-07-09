package concert.mania.common.util;

import org.springframework.stereotype.Component;

// ThreadLocal로 refreshToken 임시 저장
@Component
public class RefreshTokenHolder {
    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    public void setToken(String token) {
        tokenHolder.set(token);
    }

    public String getToken() {
        return tokenHolder.get();
    }

    public void clear() {
        tokenHolder.remove();
    }
}
