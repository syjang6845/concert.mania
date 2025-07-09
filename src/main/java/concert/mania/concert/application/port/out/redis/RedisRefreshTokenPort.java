package concert.mania.concert.application.port.out.redis;

import concert.mania.concert.domain.model.type.Authority;

/**
 * Refresh Token 관리 Port - RTR 패턴 (버전 관리 없음)
 */
public interface RedisRefreshTokenPort extends RedisTokenOperations {

    boolean validateAndReplaceToken(Long userId, Authority authority, String token, String newToken);
}
