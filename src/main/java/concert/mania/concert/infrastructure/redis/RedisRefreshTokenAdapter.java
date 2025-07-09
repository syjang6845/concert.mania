package concert.mania.concert.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import concert.mania.config.properties.JwtProperties;
import concert.mania.common.util.HashUtil;
import concert.mania.concert.application.port.out.redis.RedisRefreshTokenPort;
import concert.mania.concert.domain.model.type.Authority;

import java.time.Duration;

@Slf4j
@Component
public class RedisRefreshTokenAdapter extends AbstractRedisTokenAdapter implements RedisRefreshTokenPort {


    private static final String KEY_PREFIX = "refresh_token:";
    private static final String TOKEN_TYPE_NAME = "Refresh Token";

    public RedisRefreshTokenAdapter(StringRedisTemplate redisTemplate, JwtProperties jwtProperties, HashUtil hashUtil) {
        super(redisTemplate, jwtProperties, hashUtil);
    }

    @Override
    protected String getKeyPrefix() {
        return KEY_PREFIX;
    }

    @Override
    protected Duration getDefaultTtl() {
        return Duration.ofSeconds(jwtProperties.getRefreshTokenValidity());
    }

    @Override
    protected String getTokenTypeName() {
        return TOKEN_TYPE_NAME;
    }

    @Override
    public boolean validateAndReplaceToken(Long userId, Authority authority, String token, String newToken) {
        // 1. 저장된 토큰 확인
        if (!validateToken(userId, authority, token)) {
            log.warn("{} 토큰 불일치 - userId: {}, authority: {}", getTokenTypeName(), userId, authority);
            return false;
        }

        // 2. 검증 성공 시 즉시 삭제 (RTR 패턴)
        replaceToken(userId, authority, newToken);
        log.info("{} 검증 후 소비 완료 - userId: {}, authority: {}", getTokenTypeName(), userId, authority);
        return true;
    }
}
