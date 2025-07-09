
package concert.mania.concert.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import concert.mania.config.properties.JwtProperties;
import concert.mania.common.util.HashUtil;
import concert.mania.concert.application.port.out.redis.RedisOneTimeTokenPort;
import concert.mania.concert.domain.model.type.Authority;

import java.time.Duration;

@Slf4j
@Component
public class RedisOneTimeTokenAdapter extends AbstractRedisTokenAdapter implements RedisOneTimeTokenPort {


    private static final String KEY_PREFIX = "onetime_token:";
    private static final String TOKEN_TYPE_NAME = "OneTime Token";

    public RedisOneTimeTokenAdapter(StringRedisTemplate redisTemplate, JwtProperties jwtProperties, HashUtil hashUtil) {
        super(redisTemplate, jwtProperties, hashUtil);
    }

    @Override
    protected String getKeyPrefix() {
        return KEY_PREFIX;
    }

    @Override
    protected Duration getDefaultTtl() {
        return Duration.ofSeconds(jwtProperties.getOneTimeTokenValidity());
    }

    @Override
    protected String getTokenTypeName() {
        return TOKEN_TYPE_NAME;
    }

    // 🔥 일회성 토큰 - 검증 후 소비 (버전 없이!)
    @Override
    public boolean validateAndConsumeToken(Long userId, Authority authority, String token) {
        // 1. 저장된 토큰 확인
        if (validateToken(userId, authority, token)) {
            log.warn("{} 토큰 불일치 - userId: {}, authority: {}", getTokenTypeName(), userId, authority);
            return false;
        }

        // 2. 검증 성공 시 즉시 삭제 (일회성)
        deleteToken(userId, authority);
        log.info("{} 검증 후 소비 완료 - userId: {}, authority: {}",
                getTokenTypeName(), userId, authority);
        return true;
    }
}