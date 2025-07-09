
package concert.mania.concert.infrastructure.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import concert.mania.config.properties.JwtProperties;
import concert.mania.common.util.HashUtil;
import concert.mania.concert.application.port.out.redis.RedisAccessTokenPort;

import java.time.Duration;

@Component
public class RedisAccessTokenAdapter extends AbstractRedisTokenAdapter implements RedisAccessTokenPort {
    private static final String KEY_PREFIX = "access_token:";
    private static final String TOKEN_TYPE_NAME = "Access Token";

    public RedisAccessTokenAdapter(StringRedisTemplate redisTemplate, JwtProperties jwtProperties, HashUtil hashUtil) {
        super(redisTemplate, jwtProperties, hashUtil);
    }

    @Override
    protected String getKeyPrefix() {
        return KEY_PREFIX;
    }

    @Override
    protected Duration getDefaultTtl() {
        return Duration.ofSeconds(jwtProperties.getAccessTokenValidity());
    }

    @Override
    protected String getTokenTypeName() {
        return TOKEN_TYPE_NAME;
    }
}
