package concert.mania.concert.application.port.out.redis;

/**
 * Access Token 관리 Port - 버전 관리 없음, 단순 저장/조회
 */
public interface RedisAccessTokenPort extends RedisTokenOperations {

    // Access Token은 JWT 자체로 검증하므로 추가 검증 메서드 불필요
    // 단순히 Redis에 저장하여 로그아웃 시 블랙리스트 관리용으로 사용
}

