
package concert.mania.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import concert.mania.config.properties.JwtProperties;
import concert.mania.exception.model.ErrorCode;
import concert.mania.exception.model.InternalServerErrorException;
import concert.mania.exception.model.UnAuthorizedException;
import concert.mania.security.model.CustomUserDetails;
import concert.mania.jwt.dto.JwtToken;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.Authority;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static concert.mania.common.constants.JwtConstants.BEARER_TYPE;

/**
 * JWT 토큰 생성 및 검증 서비스
 * 순수 JWT 관련 기능만 담당하고, 토큰 저장/관리는 별도 서비스에서 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtProperties jwtProperties;

    @Value("${extends.jwt.secret-key}")
    private String jwtSecret;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        String encodedKey = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(encodedKey.getBytes());
        log.info("JWT SecretKey가 초기화되었습니다.");
    }

    // ========== 토큰 생성 ==========

    @Override
    public JwtToken generateToken(User user, boolean autoLogin) {
        log.info("토큰 생성 시작 - userId: {}, autoLogin: {}", user.getId(), autoLogin);

        // JWT 토큰 생성
        String accessToken = createAccessToken(user.getId(), user.getAuthority().name());
        String refreshToken = autoLogin ? createRefreshToken(user.getId(), user.getAuthority().name()) : null;

        log.info("토큰 생성 완료 - userId: {}, hasRefreshToken: {}", user.getId(), refreshToken != null);

        return JwtToken.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public JwtToken regenerateTokenWithRefreshToken(String refreshToken) {
        validateTokenInput(refreshToken, "Refresh Token");

        log.debug("Refresh Token으로 토큰 재생성 시작");

        try {
            // Refresh Token 검증 및 파싱
            Claims claims = parseAndValidateRefreshToken(refreshToken);
            // 토큰에서 정보 추출
            Long userId = extractUserIdFromClaims(claims);
            String authority = extractAuthorityFromClaims(claims);

            log.debug("토큰 재생성 대상 사용자 - userId: {}, authority: {}", userId, authority);

            // 새 토큰 생성
            String newAccessToken = createAccessToken(userId, authority);
            String newRefreshToken = createRefreshToken(userId, authority);

            log.info("토큰 재생성 완료 - userId: {}", userId);

            return JwtToken.builder()
                    .grantType(BEARER_TYPE)
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .userId(userId)
                    .authority(Authority.valueOf(authority))
                    .build();

        } catch (ExpiredJwtException e) {
            log.warn("만료된 Refresh Token: {}", e.getMessage());
            throw new UnAuthorizedException(ErrorCode.JWT_EXPIRED);
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("유효하지 않은 Refresh Token: {}", e.getMessage());
            throw new UnAuthorizedException(ErrorCode.JWT_INVALID);
        } catch (Exception e) {
            log.error("Refresh Token 처리 중 오류 발생", e);
            throw new InternalServerErrorException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

    @Override
    public String createPasswordResetToken(Long userId, Authority authority) {
        validateUserInfo(userId, authority);

        log.info("패스워드 재설정 토큰 생성 시작 - userId: {}, authority: {}", userId, authority);

        String oneTimeToken = createOneTimeToken(userId, authority.name());

        log.info("패스워드 재설정 토큰 생성 완료 - userId: {}", userId);
        return oneTimeToken;
    }

    // ========== 토큰 검증 및 정보 추출 ==========

    @Override
    public String extractJtiFromAccessToken(String accessToken) {
        validateTokenInput(accessToken, "Access Token");

        try {
            Claims claims = parseClaims(accessToken);
            validateTokenType(claims, "access", "Access Token");

            String jti = claims.getId();
            if (Objects.isNull(jti) || jti.trim().isEmpty()) {
                throw new IllegalArgumentException("토큰에 JTI가 없습니다.");
            }

            log.debug("Access Token JTI 추출 성공");
            return jti;

        } catch (Exception e) {
            log.warn("Access Token에서 JTI 추출 실패: {}", e.getMessage());
            throw new IllegalArgumentException("토큰에서 JTI를 추출할 수 없습니다.", e);
        }
    }

    @Override
    public Duration getAccessTokenRemainingTtl(String accessToken) {
        validateTokenInput(accessToken, "Access Token");

        try {
            Claims claims = parseClaims(accessToken);
            validateTokenType(claims, "access", "Access Token");

            Date expiration = claims.getExpiration();
            Date now = new Date();
            long remainingMillis = expiration.getTime() - now.getTime();

            Duration remainingTtl = Duration.ofMillis(Math.max(0, remainingMillis));
            log.debug("Access Token 남은 시간: {}", remainingTtl);

            return remainingTtl;

        } catch (Exception e) {
            log.warn("Access Token 만료시간 계산 실패: {}", e.getMessage());
            return Duration.ZERO;
        }
    }

    @Override
    public boolean validateAndConsumePasswordResetToken(Long userId, Authority authority, String token) {
        validateUserInfo(userId, authority);
        validateTokenInput(token, "Password Reset Token");

        log.debug("패스워드 재설정 토큰 검증 - userId: {}, authority: {}", userId, authority);

        try {
            // JWT 기본 검증 (서명, 만료시간)
            Claims claims = parseClaims(token);

            // 토큰 타입 및 용도 검증
            String tokenType = claims.get("type", String.class);
            String purpose = claims.get("purpose", String.class);

            if (!"onetime".equals(tokenType) || !"password_reset".equals(purpose)) {
                log.warn("잘못된 토큰 타입 또는 용도 - type: {}, purpose: {}", tokenType, purpose);
                return false;
            }

            log.info("패스워드 재설정 토큰 검증 완료 - userId: {}", userId);
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("만료된 패스워드 재설정 토큰 - userId: {}", userId);
            return false;
        } catch (Exception e) {
            log.error("패스워드 재설정 토큰 검증 중 오류 - userId: {}, error: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * 토큰의 유효성 검증 (만료시간, 서명 등)
     *
     * @param token 검증할 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean isTokenValid(String token) {
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            return false;
        }

        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long extractUserIdFromToken(String token) {
        validateTokenInput(token, "Token");

        try {
            Claims claims = parseClaims(token);
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID", e);
        }
    }

    // ========== 로그아웃 (토큰 저장소와 분리) ==========

    @Override
    public void logout(Long userId, Authority authority) {
        log.info("로그아웃 처리 - userId: {}, authority: {} (토큰 저장소는 별도 처리)", userId, authority);
        // 실제 토큰 삭제는 TokenStorageService에서 처리
        // 여기서는 로깅만 수행
    }

    // ========== Private Helper Methods ==========

    /**
     * Authentication 객체 유효성 검사
     */
    private void validateAuthentication(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            throw new IllegalArgumentException("Authentication 객체는 null일 수 없습니다");
        }
        if (Objects.isNull(authentication.getPrincipal())) {
            throw new IllegalArgumentException("Authentication Principal은 null일 수 없습니다");
        }
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalArgumentException("Authentication Principal은 CustomUserDetails 타입이어야 합니다");
        }
    }

    /**
     * 토큰 입력값 유효성 검사
     */
    private void validateTokenInput(String token, String tokenType) {
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            throw new IllegalArgumentException(tokenType + "은 null이거나 빈 값일 수 없습니다");
        }
    }

    /**
     * 사용자 정보 유효성 검사
     */
    private void validateUserInfo(Long userId, Authority authority) {
        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다");
        }
        if (Objects.isNull(authority)) {
            throw new IllegalArgumentException("authority는 null일 수 없습니다");
        }
    }

    /**
     * 토큰 타입 검증
     */
    private void validateTokenType(Claims claims, String expectedType, String tokenName) {
        String tokenType = claims.get("type", String.class);
        if (!expectedType.equals(tokenType)) {
            throw new IllegalArgumentException(tokenName + "이 아닙니다. 실제 타입: " + tokenType);
        }
    }

    /**
     * 권한 정보 추출
     */
    private String extractAuthorities(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    /**
     * Claims에서 사용자 ID 추출
     */
    private Long extractUserIdFromClaims(Claims claims) {
        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            log.error("Claims에서 사용자 ID 변환 실패: {}", claims.getSubject());
            throw new IllegalArgumentException("유효하지 않은 사용자 ID", e);
        }
    }

    /**
     * Claims에서 권한 정보 추출
     */
    private String extractAuthorityFromClaims(Claims claims) {
        String authority = claims.get("authority", String.class);
        if (Objects.isNull(authority) || authority.trim().isEmpty()) {
            throw new IllegalArgumentException("토큰에 권한 정보가 없습니다");
        }
        return authority;
    }

    /**
     * Refresh Token 검증 및 파싱
     */
    private Claims parseAndValidateRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        validateTokenType(claims, "refresh", "Refresh Token");
        return claims;
    }

    /**
     * Access Token 생성
     */
    private String createAccessToken(Long userId, String authority) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("authority", authority);
        claims.put("type", "access");
        claims.setId(UUID.randomUUID().toString()); // JTI 추가

        Date now = new Date();
        Date expireDate = Date.from(now.toInstant().plusSeconds(jwtProperties.getAccessTokenValidity()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    private String createRefreshToken(Long userId, String authority) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("authority", authority);
        claims.put("type", "refresh");

        Date now = new Date();
        Date expireDate = Date.from(now.toInstant().plusSeconds(jwtProperties.getRefreshTokenValidity()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 일회용 토큰 생성 (패스워드 재설정용)
     */
    private String createOneTimeToken(Long userId, String authority) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("authority", authority);
        claims.put("type", "onetime");
        claims.setId(UUID.randomUUID().toString()); // JTI

        Date now = new Date();
        Date expireDate = Date.from(now.toInstant().plusSeconds(jwtProperties.getOneTimeTokenValidity()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰 파싱
     */
    public Claims parseClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}