package concert.mania.concert.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import concert.mania.config.properties.JwtProperties;
import concert.mania.common.util.HashUtil;
import concert.mania.concert.application.port.out.redis.RedisTokenOperations;
import concert.mania.concert.domain.model.type.Authority;

import java.time.Duration;


@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRedisTokenAdapter implements RedisTokenOperations {

    protected final StringRedisTemplate redisTemplate;
    protected final JwtProperties jwtProperties;
    protected final HashUtil hashUtil;

    // 🔧 키 생성 헬퍼 메서드들
    protected String generateTokenKey(Long userId, Authority authority) {
        return getKeyPrefix() + userId + ":" + authority;
    }

    // 💾 토큰 저장/조회 구현
    @Override
    public void saveToken(Long userId, Authority authority, String token) {
        saveToken(userId, authority, token, getDefaultTtl());
    }

    @Override
    public void saveToken(Long userId, Authority authority, String token, Duration ttl) {
        String tokenKey = generateTokenKey(userId, authority);
        String tokenHash = hashUtil.hashToken(token); // 🔐 SHA-256 해시
        redisTemplate.opsForValue().set(tokenKey, tokenHash, ttl);
        log.info("{} 저장 - userId: {}, authority: {}, TTL: {}", getTokenTypeName(), userId, authority, ttl);
    }

    @Override
    public boolean validateToken(Long userId, Authority authority, String token) {
        String tokenKey = generateTokenKey(userId, authority);
        String storedTokenHash =  redisTemplate.opsForValue().get(tokenKey);// Redis에서 해시값 가져옴
        log.info("storedTokenHash: {}", storedTokenHash);
        log.info("token: {}", token);
        if (storedTokenHash == null) {
            return false;
        }
        return hashUtil.validateTokenHash(token, storedTokenHash);
    }

    @Override
    public String getToken(Long userId, Authority authority) {
        String tokenKey = generateTokenKey(userId, authority);
        return redisTemplate.opsForValue().get(tokenKey);
    }

    @Override
    public boolean existsToken(Long userId, Authority authority) {
        String tokenKey = generateTokenKey(userId, authority);
        return redisTemplate.hasKey(tokenKey);
    }

    @Override
    public void replaceToken(Long userId, Authority authority, String newToken) {
        String key = generateTokenKey(userId, authority);

        try {
            String tokenHash = hashUtil.hashToken(newToken);
            // 🔄 동일한 키로 새 토큰 덮어쓰기 (메모리 효율적)
            redisTemplate.opsForValue().set(key, tokenHash, getDefaultTtl());

            log.debug("Access Token 교체 완료 - userId: {}, authority: {}, key: {}",
                    userId, authority, key);

        } catch (Exception e) {
            log.error("Access Token 교체 실패 - userId: {}, authority: {}, error: {}",
                    userId, authority, e.getMessage(), e);
            throw new RuntimeException("토큰 교체에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteToken(Long userId, Authority authority) {
        String tokenKey = generateTokenKey(userId, authority);
        redisTemplate.delete(tokenKey);
        log.info("{} 삭제 - userId: {}, authority: {}", getTokenTypeName(), userId, authority);
    }

    // ⏰ TTL 관리 구현
    @Override
    public void extendTokenTtl(Long userId, Authority authority) {
        extendTokenTtl(userId, authority, getDefaultTtl());
    }

    @Override
    public void extendTokenTtl(Long userId, Authority authority, Duration ttl) {
        String currentToken = getToken(userId, authority);
        if (currentToken != null) {
            saveToken(userId, authority, currentToken, ttl);
            log.info("{} TTL 연장 - userId: {}, authority: {}, TTL: {}", getTokenTypeName(), userId, authority, ttl);
        }
    }

    // 🗑️ 전체 삭제 구현 (Access Token용 - 버전 관리 없음)
    @Override
    public void deleteAll(Long userId, Authority authority) {
        deleteToken(userId, authority);
    }

    // 🔧 추상 메서드들 - 하위 클래스에서 구현 필요
    protected abstract String getKeyPrefix();
    protected abstract Duration getDefaultTtl();
    protected abstract String getTokenTypeName();
}
