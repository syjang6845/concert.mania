package concert.mania.jwt.service;

import io.jsonwebtoken.Claims;
import concert.mania.jwt.dto.JwtToken;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.Authority;

import java.time.Duration;

/**
 * JWT 토큰 서비스 인터페이스
 * 토큰 생성, 검증, 정보 추출 등의 JWT 관련 기능 정의
 */
public interface JwtTokenService {

    // ========== 토큰 생성 ==========

    /**
     * 사용자 정보로 JWT 토큰 생성
     */
    JwtToken generateToken(User user, boolean autoLogin);

    /**
     * Refresh Token으로 새로운 토큰 생성
     */
    JwtToken regenerateTokenWithRefreshToken(String refreshToken);

    /**
     * 패스워드 재설정용 일회성 토큰 생성
     */
    String createPasswordResetToken(Long userId, Authority authority);

    // ========== 토큰 검증 및 정보 추출 ==========

    /**
     * 토큰 유효성 검증
     */
    boolean isTokenValid(String token);

    /**
     * 토큰에서 사용자 ID 추출
     */
    Long extractUserIdFromToken(String token);

    /**
     * Access Token에서 JTI 추출
     */
    String extractJtiFromAccessToken(String accessToken);

    /**
     * Access Token 남은 유효시간 조회
     */
    Duration getAccessTokenRemainingTtl(String accessToken);

    /**
     * 패스워드 재설정 토큰 검증 및 소비
     */
    boolean validateAndConsumePasswordResetToken(Long userId, Authority authority, String token);

    /**
     * JWT 토큰 파싱하여 Claims 반환
     */
    Claims parseClaims(String token);

    // ========== 로그아웃 ==========

    /**
     * 로그아웃 처리
     */
    void logout(Long userId, Authority authority);
}