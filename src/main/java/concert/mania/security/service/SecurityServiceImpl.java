package concert.mania.security.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import concert.mania.exception.model.ErrorCode;
import concert.mania.exception.model.InternalServerErrorException;
import concert.mania.exception.model.UnAuthorizedException;
import concert.mania.jwt.service.JwtTokenService;
import concert.mania.security.model.CustomUserDetails;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.Authority;

import java.time.Duration;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = getValidAuthentication();
        if (authentication == null) {
            log.debug("No authentication found in security context - likely an actuator endpoint");
            return null;
        }
        return Long.valueOf(authentication.getName());
    }

    @Override
    public String getCurrentUsername() {
        Authentication authentication = getValidAuthentication();
        if (authentication == null) {
            log.debug("No authentication found in security context - likely an actuator endpoint");
            return null;
        }
        return authentication.getName();
    }

    @Override
    public Authority getCurrentAuthority() {
        Authentication authentication = getValidAuthentication();
        if (authentication == null) {
            log.debug("No authentication found in security context - likely an actuator endpoint");
            return null;
        }
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Authority.valueOf(authorities);
    }

    @Override
    public Authentication getCurrentAuthentication() {
        Authentication authentication = getValidAuthentication();
        if (authentication == null) {
            log.debug("No authentication found in security context - likely an actuator endpoint");
        }
        return authentication;
    }

    @Override
    public Authentication createAuthentication(User user) {
        UserDetails userDetails = new CustomUserDetails(
                user.getId(),
                "",
                user.getAuthority().name()
        );
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public Authentication createAuthentication(Long userId, Authority authority) {
        UserDetails userDetails = new CustomUserDetails(
                userId,
                "",
                authority.name()
        );
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public void createAuthenticationWithLogin(String email, String password) {
        log.debug("로그인 인증 시작 - email: {}", email);

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            DaoAuthenticationProvider authProvider = createAuthenticationProvider();
            authProvider.authenticate(authToken);
        } catch (BadCredentialsException e) {
            // 비밀번호 불일치 (사용자는 존재하지만 비밀번호가 틀림)
            log.warn("비밀번호 불일치 - email: {}", email);
            throw new UnAuthorizedException(ErrorCode.PASSWORD_MISMATCH);
        } catch (AuthenticationException e) {
            log.warn("로그인 인증 실패 - email: {}, reason: {}", email, e.getMessage());
            throw new UnAuthorizedException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    @Override
    public boolean authorize(Long id, Authority authority) {
        try {
            Long currentUserId = getCurrentUserId();
            Authority currentAuthority = getCurrentAuthority();

            // If either is null (likely an actuator endpoint), authorization fails
            if (currentUserId == null || currentAuthority == null) {
                log.debug("권한 검증 실패 - 인증 정보 없음 (액추에이터 엔드포인트 접근 가능성)");
                return false;
            }

            boolean authorized = id.equals(currentUserId) && authority == currentAuthority;
            log.debug("권한 검증 - 요청 ID: {}, 현재 ID: {}, 요청 권한: {}, 현재 권한: {}, 결과: {}",
                    id, currentUserId, authority, currentAuthority, authorized);

            return authorized;
        } catch (Exception e) {
            log.warn("권한 검증 중 오류 발생", e);
            return false;
        }
    }

    @Override
    public boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean authenticated = authentication != null
                    && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal());

            log.debug("인증 상태 확인: {}", authenticated);
            return authenticated;
        } catch (Exception e) {
            log.warn("인증 상태 확인 중 오류 발생", e);
            return false;
        }
    }

    @Override
    public String getCurrentAccessTokenJti() {
        try {
            String accessToken = getCurrentAccessToken();
            if (accessToken == null || accessToken.isEmpty()) {
                log.warn("현재 요청에서 Access Token을 찾을 수 없습니다.");
                return null;
            }

            return jwtTokenService.extractJtiFromAccessToken(accessToken);
        } catch (Exception e) {
            log.warn("Access Token에서 JTI 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Duration getCurrentAccessTokenRemainingTtl() {
        try {
            String accessToken = getCurrentAccessToken();
            if (accessToken == null || accessToken.isEmpty()) {
                log.warn("현재 요청에서 Access Token을 찾을 수 없습니다.");
                return Duration.ZERO;
            }

            return jwtTokenService.getAccessTokenRemainingTtl(accessToken);
        } catch (Exception e) {
            log.warn("Access Token 만료시간 계산 실패: {}", e.getMessage());
            return Duration.ZERO;
        }
    }

    @Override
    public String getCurrentAccessToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                log.warn("현재 요청 컨텍스트를 찾을 수 없습니다.");
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.debug("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다.");
                return null;
            }

            String token = authorizationHeader.substring(7);
            log.debug("현재 요청의 Access Token 추출 완료");

            return token;

        } catch (Exception e) {
            log.warn("현재 요청에서 Access Token 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    private DaoAuthenticationProvider createAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setHideUserNotFoundExceptions(true);
        return authProvider;
    }

    private Authentication getValidAuthentication() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            // Check if the current request is for an actuator endpoint
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String requestURI = request.getRequestURI();
                if (requestURI != null && requestURI.startsWith("/actuator/")) {
                    log.debug("Actuator endpoint accessed without authentication: {}", requestURI);
                    return null;
                }
            }

            log.error("Security Context 에 인증 정보가 없습니다");
            throw new InternalServerErrorException();
        }
        return authentication;
    }
}
