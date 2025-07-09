package concert.mania.common.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityKeyProvider {

    private final String aesKey;           // 암호화/복호화용
    private final String hmacKey;          // 해싱/검증용

    public SecurityKeyProvider(
            @Value("${aes.key}") String aesKey,
            @Value("${security.token.hmac-key}") String hmacKey) {
        this.aesKey = aesKey;
        this.hmacKey = hmacKey;
        log.info("SecurityKeyProvider initialized - AES key length: {}, HMAC key length: {}",
                aesKey.length(), hmacKey.length());
    }

    /**
     * AES 암호화/복호화용 키
     */
    public String getAesKey() {
        return aesKey;
    }

    /**
     * 토큰 해싱/검증용 HMAC 키
     */
    public String getHmacKey() {
        return hmacKey;
    }

    @PostConstruct
    public void validateKeys() {
        // AES 키 검증 (16, 24, 32 바이트)
        if (aesKey.length() != 16 && aesKey.length() != 24 && aesKey.length() != 32) {
            log.warn("AES key length should be 16, 24, or 32 bytes. Current: {}", aesKey.length());
        }

        // HMAC 키 검증 (최소 32자 권장)
        if (hmacKey.length() < 32) {
            log.warn("HMAC key should be at least 32 characters. Current: {}", hmacKey.length());
        }

        // 기본값 사용 경고
        if ("saas-project-temp-aeskey".equals(aesKey)) {
            log.warn("Using default AES key. Please set aes.key in production!");
        }
        if ("saas-project-temp-hmackey-must-be-32-chars".equals(hmacKey)) {
            log.warn("Using default HMAC key. Please set security.token.hmac-key in production!");
        }
    }
}
