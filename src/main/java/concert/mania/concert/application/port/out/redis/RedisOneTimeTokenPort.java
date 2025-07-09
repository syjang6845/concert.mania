package concert.mania.concert.application.port.out.redis;

import concert.mania.concert.domain.model.type.Authority;

/**
 * One Time Token 관리 Port - 버전 관리 포함 + 일회성 검증
 */
/**
 * One Time Token 관리 Port - 일회성 토큰 (버전 관리 불필요)
 */
public interface RedisOneTimeTokenPort extends RedisTokenOperations {

    // 🔥 일회성 토큰 - 검증 후 소비 (버전 없이)
    boolean validateAndConsumeToken(Long userId, Authority authority, String token);
}


