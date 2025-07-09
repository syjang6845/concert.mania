package concert.mania.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import concert.mania.common.util.HashUtil;
import concert.mania.exception.model.ErrorCode;
import concert.mania.exception.model.UnAuthorizedException;
import concert.mania.exception.model.BadRequestException;
import concert.mania.security.model.CustomUserDetails;
import concert.mania.concert.domain.model.type.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.springframework.http.HttpMethod.*;

/**
 * JWT 토큰 사전 인증 필터
 * Spring Security와 통합하여 인증 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${extends.jwt.secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final HashUtil hashUtil;
    private final Environment environment;

    private Key key;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 키 초기화 (lazy initialization)
        if (key == null) {
            String jwtSecret = Base64.getEncoder().encodeToString(secretKey.getBytes());
            key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.info("=== JWT 사전 인증 필터 시작 ===");
        log.info("요청: {} {}", method, requestURI);

        try {
            // 인증이 필요하지 않은 경로는 통과
            if (isExcludedPath(request)) {
                log.info("인증 제외 경로 통과 - {} {}", method, requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 추출
            String token = extractToken(request, requestURI);
            log.info("토큰 추출 성공 - 길이: {}", token.length());

            // 기본 JWT 검증
            validateToken(token);
            log.info("기본 JWT 검증 통과");

            // 사용자 정보 추출
            Long userId = getUserId(token);
            Authority authority = getAuthority(token);
            log.info("🔍 토큰에서 추출된 사용자 정보 - userId: {}, authority: {} ({})",
                    userId, authority.name(), authority.getDescription());

            // Redis 기반 토큰 검증
            validateSpecificToken(token, userId, authority);
            log.info("Redis 토큰 검증 통과");

            // SecurityContext에 인증 정보 설정
            setAuthenticationInSecurityContext(userId, authority, request);

            // API 호출 기록
            recordApiCall(userId, authority, requestURI, request);

            log.info("✅ JWT 사전 인증 완료 - userId: {}, authority: {}", userId, authority.name());

        } catch (UnAuthorizedException e) {
            log.error("❌ 인증 실패 - {} {}, 오류: {}", method, requestURI, e.getMessage());
            handleException(e, response);
            return;
        } catch (BadRequestException e) {
            log.error("❌ 요청 오류 - {} {}, 오류: {}", method, requestURI, e.getMessage());
            handleException(e, response);
            return;
        } catch (Exception e) {
            log.error("❌ 사전 인증 필터 처리 중 예상치 못한 오류 - {} {}", method, requestURI, e);
            handleException(e, response);
            return;
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }


    /**
     * 인증이 필요하지 않은 경로인지 확인 (SecurityConfig와 동일한 로직)
     */
    private boolean isExcludedPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // GET 요청 허용 경로
        if (GET.name().equals(method)) {
            // Actuator 경로
            if (requestURI.equals("/actuator/health") || requestURI.equals("/actuator/info") || requestURI.equals("/actuator/prometheus")) {
                return true;
            }

            // Swagger UI 및 API 문서 관련 경로 허용 (모든 하위 경로 포함)
            if (requestURI.startsWith("/v3/api-docs") ||
                    requestURI.startsWith("/swagger-ui") ||
                    requestURI.equals("/swagger-ui.html") ||
                    requestURI.startsWith("/swagger-resources") ||
                    requestURI.equals("/configuration/ui") ||
                    requestURI.equals("/configuration/security") ||
                    requestURI.startsWith("/webjars/")) {
                return true;
            }

            // 개발 환경에서만 허용되는 경로들
            boolean isDevelopment = environment.acceptsProfiles(Profiles.of("dev", "local"));
            if (isDevelopment) {
                if (requestURI.startsWith("/h2-console")) {
                    return true;
                }
            }
        }

        // POST 요청 허용 경로
        if (POST.name().equals(method)) {
            if (requestURI.equals("/api/v1/users") || // 회원가입
                    requestURI.equals("/api/v1/users/authentication/login") ||
                    requestURI.equals("/api/v1/users/authentication/tokens/refresh")) {
                return true;
            }
        }

        // 기타 허용 경로
        if (requestURI.equals("/error") || requestURI.equals("/favicon.ico")) {
            return true;
        }

        log.debug("경로 제외 확인 - {} {}, 제외됨: false", method, requestURI);
        return false;
    }
    /**
     * HTTP 요청에서 토큰 추출
     */
    protected String extractToken(HttpServletRequest request, String path) {
        String token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(header -> !header.isEmpty())
                .map(header -> header.replace("Bearer ", ""))
                .orElseGet(() -> path.startsWith("/ws/")
                        ? request.getParameter("token")
                        : null);

        // 토큰이 없으면 예외 발생
        if (!StringUtils.hasText(token)) {
            throw new UnAuthorizedException(ErrorCode.MISSING_TOKEN);
        }

        return token;
    }

    /**
     * 기본 JWT 토큰 검증
     */
    protected void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (Exception e) {
            log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
            throw new UnAuthorizedException(ErrorCode.JWT_INVALID);
        }
    }

    /**
     * Redis 기반 토큰 검증
     */
    protected void validateSpecificToken(String token, Long userId, Authority authority) {
        log.debug("Redis 토큰 검증 - userId: {}, authority: {}", userId, authority);

        // Redis 키 생성
        String redisKey = "access_token:" + userId + ":" + authority.name();

        // Redis에서 저장된 값 조회
        String storedValue = stringRedisTemplate.opsForValue().get(redisKey);

        // 저장된 값이 없거나 해시 검증 실패 시 예외 발생
        if (storedValue == null || !hashUtil.validateTokenHash(token, storedValue)) {
            log.warn("Redis 토큰 검증 실패 - userId: {}, redisKey: {}", userId, redisKey);
            throw new BadRequestException(ErrorCode.AUTHENTICATION_FAILED);
        }

        log.debug("Redis 토큰 검증 통과 - userId: {}", userId);
    }

    /**
     * SecurityContext에 인증 정보 설정
     */
    protected void setAuthenticationInSecurityContext(Long userId, Authority authority,
                                                      HttpServletRequest request) {
        log.info("🔐 SecurityContext 설정 시작 - userId: {}, authority: {}", userId, authority.name());

        // CustomUserDetails 생성
        CustomUserDetails userDetails = new CustomUserDetails(userId, "", authority.name());
        log.info("👤 CustomUserDetails 생성 완료 - authorities: {}",
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("없음"));

        // UsernamePasswordAuthenticationToken 사용 (JWT 토큰 기반 인증)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);


        log.info("✅ SecurityContext 인증 정보 설정 완료 - userId: {}, authority: {}",
                userId, authority.name());
    }

    /**
     * 공통 메소드들
     */
    protected Claims parseClaims(String jwtToken) {
        return Jwts.parserBuilder().setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    protected Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    protected Authority getAuthority(String token) {
        Claims claims = parseClaims(token);
        return Authority.valueOf(claims.get("authority", String.class));
    }

    protected void recordApiCall(Long userId, Authority authority, String path, HttpServletRequest request) {
        log.debug("API Call recorded: userId={}, authority={}, path={}", userId, authority, path);
        handleFileRecords(request, userId, authority);
    }

    protected void handleFileRecords(HttpServletRequest request, Long userId, Authority authority) {
        String contentType = request.getContentType();

        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            String fileSizeHeader = request.getHeader("X-File-Size");
            if (fileSizeHeader != null) {
                try {
                    long fileSize = Long.parseLong(fileSizeHeader);
                    log.debug("File record: userId={}, authority={}, fileSize={}", userId, authority, fileSize);
                } catch (NumberFormatException e) {
                    log.error("Invalid file size header: ", e);
                }
            }
        }
    }

    /**
     * 예외 처리를 위한 공통 메소드
     */
    protected void handleException(Exception e, HttpServletResponse response) throws IOException {
        log.error("Authentication filter error: ", e);

        if (e instanceof UnAuthorizedException) {
            UnAuthorizedException ue = (UnAuthorizedException) e;
            onError(response, ue.getErrorCode(), HttpStatus.UNAUTHORIZED);
        } else if (e instanceof BadRequestException) {
            BadRequestException be = (BadRequestException) e;
            onError(response, be.getErrorCode(), HttpStatus.BAD_REQUEST);
        } else {
            onError(response, ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected void onError(HttpServletResponse response, ErrorCode err, HttpStatus httpStatus) throws IOException {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String errorResponse = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"code\":\"%s\",\"timestamp\":\"%s\"}",
                err.name(),
                err.getMessage(),
                err.name(),
                LocalDateTime.now()
        );

        response.getWriter().write(errorResponse);
        response.getWriter().flush();
    }
}
