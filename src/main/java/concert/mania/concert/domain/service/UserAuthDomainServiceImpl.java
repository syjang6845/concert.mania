package concert.mania.concert.domain.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import concert.mania.concert.application.port.out.redis.RedisAccessTokenPort;
import concert.mania.concert.application.port.out.redis.RedisRefreshTokenPort;
import concert.mania.concert.domain.model.type.Authority;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthDomainServiceImpl implements UserAuthDomainService {
    private final RedisAccessTokenPort redisAccessTokenPort;
    private final RedisRefreshTokenPort redisRefreshTokenPort;

    @Override
    public void performLogout(Long userId, Authority authority, HttpServletResponse response) {
        redisAccessTokenPort.deleteAll(userId, authority);
        redisRefreshTokenPort.deleteAll(userId, authority);
    }





}
