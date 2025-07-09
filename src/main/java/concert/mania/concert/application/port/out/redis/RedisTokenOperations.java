package concert.mania.concert.application.port.out.redis;

import concert.mania.concert.domain.model.type.Authority;

import java.time.Duration;

/**
 * Redis 토큰 관리 기본 연산 인터페이스
 */
public interface RedisTokenOperations {

    // 💾 토큰 저장/조회/삭제
    void saveToken(Long userId, Authority authority, String token);
    void saveToken(Long userId, Authority authority, String token, Duration ttl);
    String getToken(Long userId, Authority authority);
    boolean existsToken(Long userId, Authority authority);
    void deleteToken(Long userId, Authority authority);
    void replaceToken(Long userId, Authority authority, String newToken);
    boolean validateToken(Long userId, Authority authority, String token);

    // ⏰ TTL 관리
    void extendTokenTtl(Long userId, Authority authority);
    void extendTokenTtl(Long userId, Authority authority, Duration ttl);

    // 🗑️ 전체 삭제
    void deleteAll(Long userId, Authority authority);
}
