package concert.mania.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class HashUtil {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final SecurityKeyProvider securityKeyProvider;  // ← 주입받기!

    public HashUtil(SecurityKeyProvider securityKeyProvider) {
        this.securityKeyProvider = securityKeyProvider;
    }

    /**
     * 토큰 해싱 - SecurityKeyProvider의 HMAC 키 자동 사용
     */
    public String hashToken(String token) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    securityKeyProvider.getHmacKey().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("HMAC-SHA256 토큰 해싱 실패", e);
            throw new RuntimeException("토큰 해싱 실패", e);
        }
    }

    /**
     * 토큰 검증 - SecurityKeyProvider의 HMAC 키 자동 사용
     */
    public boolean validateTokenHash(String token, String storedHash) {
        if (token == null || storedHash == null) {
            return false;
        }

        String tokenHash = hashToken(token);
        log.info("Token hash: {}, Stored hash: {}", tokenHash, storedHash);
        return constantTimeEquals(tokenHash, storedHash);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
