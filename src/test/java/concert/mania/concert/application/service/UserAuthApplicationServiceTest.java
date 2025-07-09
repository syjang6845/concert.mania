package concert.mania.concert.application.service;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import concert.mania.common.util.RefreshTokenHolder;
import concert.mania.exception.model.ErrorCode;
import concert.mania.exception.model.UnAuthorizedException;
import concert.mania.jwt.dto.JwtToken;
import concert.mania.jwt.service.JwtTokenService;
import concert.mania.security.service.SecurityService;
import concert.mania.concert.application.command.LoginCommand;
import concert.mania.concert.application.dto.TokenDto;
import concert.mania.concert.application.port.out.query.UserQueryPort;
import concert.mania.concert.application.port.out.redis.RedisAccessTokenPort;
import concert.mania.concert.application.port.out.redis.RedisRefreshTokenPort;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.Authority;
import concert.mania.concert.domain.model.type.RoleType;
import concert.mania.concert.domain.service.UserAuthDomainService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthApplicationService 테스트")
class UserAuthApplicationServiceTest {

    @Mock private RefreshTokenHolder refreshTokenHolder;
    @Mock private UserQueryPort userQueryPort;
    @Mock private SecurityService securityService;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private UserAuthDomainService userAuthDomainService;
    @Mock private RedisAccessTokenPort redisAccessTokenPort;
    @Mock private RedisRefreshTokenPort redisRefreshTokenPort;
    @Mock private HttpServletResponse response;
    @Mock private Authentication authentication;

    @InjectMocks
    private UserAuthApplicationService userAuthApplicationService;

    private String testEmail;
    private String testPassword;
    private RoleType testEducatorRole;
    private String testAccessToken;
    private String testRefreshToken;
    private String testNewAccessToken;
    private String testNewRefreshToken;
    private Long testUserId;
    private Authority testAuthority;
    private LoginCommand loginCommand;
    private LoginCommand autoLoginCommand;
    private User testUser;
    private JwtToken jwtToken;
    private JwtToken jwtTokenWithoutRefresh;
    private JwtToken newJwtToken;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testPassword = "password123";
        testEducatorRole = RoleType.USER;
        testAccessToken = "test-access-token";
        testRefreshToken = "test-refresh-token";
        testNewAccessToken = "new-access-token";
        testNewRefreshToken = "new-refresh-token";
        testUserId = 1L;
        testAuthority = Authority.ROLE_USER;

        loginCommand = LoginCommand.builder()
                .email(testEmail)
                .password(testPassword)
                .role(testEducatorRole)
                .autoLogin(false)
                .build();

        autoLoginCommand = LoginCommand.builder()
                .email(testEmail)
                .password(testPassword)
                .role(testEducatorRole)
                .autoLogin(true)
                .build();

        testUser = User.builder()
                .id(testUserId)
                .email(testEmail)
                .password(testPassword)
                .role(testEducatorRole)
                .role(testAuthority.toRoleType())
                .build();

        jwtToken = JwtToken.builder()
                .accessToken(testAccessToken)
                .refreshToken(testRefreshToken)
                .grantType("Bearer")
                .build();

        jwtTokenWithoutRefresh = JwtToken.builder()
                .accessToken(testAccessToken)
                .refreshToken(null)
                .grantType("Bearer")
                .build();

        newJwtToken = JwtToken.builder()
                .accessToken(testNewAccessToken)
                .refreshToken(testNewRefreshToken)
                .userId(testUserId)
                .authority(testAuthority)
                .grantType("Bearer")
                .build();
    }

    @Nested
    @DisplayName("login 메서드 테스트")
    class LoginTest {

        @Test
        @DisplayName("autoLogin이 true일 때 refresh 토큰을 포함한 로그인을 성공한다")
        void login_WithAutoLogin_Success() {
            // Given
            when(userQueryPort.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
            doNothing().when(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            when(jwtTokenService.generateToken(testUser, true)).thenReturn(jwtToken);
            doNothing().when(redisAccessTokenPort).saveToken(testUserId, testAuthority, testAccessToken);
            doNothing().when(redisRefreshTokenPort).saveToken(testUserId, testAuthority, testRefreshToken);
            doNothing().when(refreshTokenHolder).setToken(testRefreshToken);

            // When
            TokenDto result = userAuthApplicationService.login(autoLoginCommand, response);

            // Then
            assertNotNull(result);
            assertEquals(testAccessToken, result.getAccessToken());
            assertEquals(testRefreshToken, result.getRefreshToken());
            assertEquals("Bearer", result.getGrantType());

            verify(userQueryPort).findByEmail(testEmail);
            verify(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            verify(jwtTokenService).generateToken(testUser, true);
            verify(redisAccessTokenPort).saveToken(testUserId, testAuthority, testAccessToken);
            verify(redisRefreshTokenPort).saveToken(testUserId, testAuthority, testRefreshToken);
            verify(refreshTokenHolder).setToken(testRefreshToken);
        }

        @Test
        @DisplayName("autoLogin이 false일 때 refresh 토큰 없이 로그인을 성공한다")
        void login_WithoutAutoLogin_Success() {
            // Given
            when(userQueryPort.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
            doNothing().when(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            when(jwtTokenService.generateToken(testUser, false)).thenReturn(jwtTokenWithoutRefresh);
            doNothing().when(redisAccessTokenPort).saveToken(testUserId, testAuthority, testAccessToken);
            doNothing().when(refreshTokenHolder).setToken(null);

            // When
            TokenDto result = userAuthApplicationService.login(loginCommand, response);

            // Then
            assertNotNull(result);
            assertEquals(testAccessToken, result.getAccessToken());
            assertNull(result.getRefreshToken());
            assertEquals("Bearer", result.getGrantType());

            verify(userQueryPort).findByEmail(testEmail);
            verify(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            verify(jwtTokenService).generateToken(testUser, false);
            verify(redisAccessTokenPort).saveToken(testUserId, testAuthority, testAccessToken);
            verify(redisRefreshTokenPort, never()).saveToken(any(), any(), any());
            verify(refreshTokenHolder).setToken(null);
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 로그인 시 예외를 발생시킨다")
        void login_UserNotFound_ThrowsException() {
            // Given
            when(userQueryPort.findByEmail(testEmail)).thenReturn(Optional.empty());

            // When & Then
            UnAuthorizedException exception = assertThrows(
                    UnAuthorizedException.class,
                    () -> userAuthApplicationService.login(loginCommand, response)
            );

            assertEquals(ErrorCode.LOGIN_USER_NOT_FOUND, exception.getErrorCode());
            verify(userQueryPort).findByEmail(testEmail);
            verify(securityService, never()).createAuthenticationWithLogin(any(), any());
            verify(jwtTokenService, never()).generateToken(any(), anyBoolean());
        }

        @Test
        @DisplayName("인증 객체 생성 실패 시 예외를 발생시킨다")
        void login_AuthenticationFailed_ThrowsException() {
            // Given
            when(userQueryPort.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
            doThrow(new UnAuthorizedException(ErrorCode.AUTHENTICATION_FAILED))
                    .when(securityService).createAuthenticationWithLogin(testEmail, testPassword);

            // When & Then
            UnAuthorizedException exception = assertThrows(
                    UnAuthorizedException.class,
                    () -> userAuthApplicationService.login(loginCommand, response)
            );

            assertEquals(ErrorCode.AUTHENTICATION_FAILED, exception.getErrorCode());
            verify(userQueryPort).findByEmail(testEmail);
            verify(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            verify(jwtTokenService, never()).generateToken(any(), anyBoolean());
        }
    }

    @Nested
    @DisplayName("regenerateAccessTokenWithRefreshToken 메서드 테스트")
    class RegenerateTokenTest {

        @Test
        @DisplayName("RefreshToken으로 정상적으로 새 토큰을 생성한다")
        void regenerateAccessTokenWithRefreshToken_Success() {
            // Given
            when(jwtTokenService.regenerateTokenWithRefreshToken(testRefreshToken)).thenReturn(newJwtToken);
            doNothing().when(redisAccessTokenPort).saveToken(testUserId, testAuthority, testNewAccessToken);
            doNothing().when(redisRefreshTokenPort).saveToken(testUserId, testAuthority, testNewRefreshToken);
            doNothing().when(refreshTokenHolder).setToken(testNewRefreshToken);

            // When
            when(userQueryPort.findById(testUserId)).thenReturn(Optional.of(testUser));
            TokenDto result = userAuthApplicationService.regenerateAccessTokenWithRefreshToken(testRefreshToken, response);

            // Then
            assertNotNull(result);
            assertEquals(testNewAccessToken, result.getAccessToken());
            assertEquals(testNewRefreshToken, result.getRefreshToken());
            assertEquals("Bearer", result.getGrantType());

            verify(jwtTokenService).regenerateTokenWithRefreshToken(testRefreshToken);
            verify(redisAccessTokenPort).saveToken(testUserId, testAuthority, testNewAccessToken);
            verify(redisRefreshTokenPort).saveToken(testUserId, testAuthority, testNewRefreshToken);
            verify(refreshTokenHolder).setToken(testNewRefreshToken);
        }

        @Test
        @DisplayName("유효하지 않은 RefreshToken으로 토큰 재생성 시 검증 예외를 발생시킨다")
        void regenerateAccessTokenWithRefreshToken_InvalidRefreshToken_ThrowsException() {
            // Given
            when(jwtTokenService.regenerateTokenWithRefreshToken(testRefreshToken))
                    .thenThrow(new UnAuthorizedException(ErrorCode.JWT_INVALID));

            // When & Then
            UnAuthorizedException exception = assertThrows(
                    UnAuthorizedException.class,
                    () -> userAuthApplicationService.regenerateAccessTokenWithRefreshToken(testRefreshToken, response)
            );

            assertEquals(ErrorCode.JWT_INVALID, exception.getErrorCode());
            verify(jwtTokenService).regenerateTokenWithRefreshToken(testRefreshToken);
            verify(redisAccessTokenPort, never()).saveToken(any(), any(), any());
            verify(redisRefreshTokenPort, never()).saveToken(any(), any(), any());
            verify(refreshTokenHolder, never()).setToken(any());
        }

        @Test
        @DisplayName("만료된 RefreshToken으로 토큰 재생성 시 만료 예외를 발생시킨다")
        void regenerateAccessTokenWithRefreshToken_ExpiredRefreshToken_ThrowsException() {
            // Given
            when(jwtTokenService.regenerateTokenWithRefreshToken(testRefreshToken))
                    .thenThrow(new UnAuthorizedException(ErrorCode.JWT_EXPIRED));

            // When & Then
            UnAuthorizedException exception = assertThrows(
                    UnAuthorizedException.class,
                    () -> userAuthApplicationService.regenerateAccessTokenWithRefreshToken(testRefreshToken, response)
            );

            assertEquals(ErrorCode.JWT_EXPIRED, exception.getErrorCode());
            verify(jwtTokenService).regenerateTokenWithRefreshToken(testRefreshToken);
            verify(redisAccessTokenPort, never()).saveToken(any(), any(), any());
            verify(redisRefreshTokenPort, never()).saveToken(any(), any(), any());
            verify(refreshTokenHolder, never()).setToken(any());
        }
    }

    @Nested
    @DisplayName("logout 메서드 테스트")
    class LogoutTest {

        @Test
        @DisplayName("정상적으로 로그아웃을 처리한다")
        void logout_Success() {
            // Given
            when(securityService.getCurrentUserId()).thenReturn(testUserId);
            when(securityService.getCurrentAuthority()).thenReturn(testAuthority);
            doNothing().when(userAuthDomainService).performLogout(testUserId, testAuthority, response);

            // When
            assertDoesNotThrow(() -> userAuthApplicationService.logout(response));

            // Then
            verify(securityService).getCurrentUserId();
            verify(securityService).getCurrentAuthority();
            verify(userAuthDomainService).performLogout(testUserId, testAuthority, response);
        }

        @Test
        @DisplayName("현재 사용자 ID 조회 실패 시 예외를 발생시킨다")
        void logout_GetCurrentUserIdFailed_ThrowsException() {
            // Given
            when(securityService.getCurrentUserId()).thenThrow(new UnAuthorizedException(ErrorCode.UNAUTHORIZED));

            // When & Then
            UnAuthorizedException exception = assertThrows(
                    UnAuthorizedException.class,
                    () -> userAuthApplicationService.logout(response)
            );

            assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
            verify(securityService).getCurrentUserId();
            verify(securityService, never()).getCurrentAuthority();
            verify(userAuthDomainService, never()).performLogout(any(), any(), any());
        }

        @Test
        @DisplayName("도메인 서비스 로그아웃 처리 실패 시 예외를 발생시킨다")
        void logout_DomainServiceFailed_ThrowsException() {
            // Given
            when(securityService.getCurrentUserId()).thenReturn(testUserId);
            when(securityService.getCurrentAuthority()).thenReturn(testAuthority);
            doThrow(new RuntimeException("로그아웃 처리 실패"))
                    .when(userAuthDomainService).performLogout(testUserId, testAuthority, response);

            // When & Then
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> userAuthApplicationService.logout(response)
            );

            assertEquals("로그아웃 처리 실패", exception.getMessage());
            verify(securityService).getCurrentUserId();
            verify(securityService).getCurrentAuthority();
            verify(userAuthDomainService).performLogout(testUserId, testAuthority, response);
        }
    }

    @Nested
    @DisplayName("통합 테스트 - 전체 플로우")
    class IntegrationTest {

        @Test
        @DisplayName("autoLogin=true 로그인 → 토큰 재생성 → 로그아웃 전체 플로우가 정상적으로 동작한다")
        void userAuthenticationFullFlow_WithAutoLogin_Success() {
            // Given - 로그인
            when(userQueryPort.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
            doNothing().when(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            when(jwtTokenService.generateToken(testUser, true)).thenReturn(jwtToken);
            doNothing().when(redisAccessTokenPort).saveToken(testUserId, testAuthority, testAccessToken);
            doNothing().when(redisRefreshTokenPort).saveToken(testUserId, testAuthority, testRefreshToken);
            doNothing().when(refreshTokenHolder).setToken(testRefreshToken);

            // Given - 토큰 재생성
            when(jwtTokenService.regenerateTokenWithRefreshToken(testRefreshToken)).thenReturn(newJwtToken);
            doNothing().when(redisAccessTokenPort).saveToken(testUserId, testAuthority, testNewAccessToken);
            doNothing().when(redisRefreshTokenPort).saveToken(testUserId, testAuthority, testNewRefreshToken);
            doNothing().when(refreshTokenHolder).setToken(testNewRefreshToken);

            // Given - 로그아웃
            when(securityService.getCurrentUserId()).thenReturn(testUserId);
            when(securityService.getCurrentAuthority()).thenReturn(testAuthority);
            doNothing().when(userAuthDomainService).performLogout(testUserId, testAuthority, response);

            // When - 전체 플로우 실행
            TokenDto loginResult = userAuthApplicationService.login(autoLoginCommand, response);
            when(securityService.getCurrentUserId()).thenReturn(testUserId);
            when(userQueryPort.findById(testUserId)).thenReturn(Optional.of(testUser));

            TokenDto refreshResult = userAuthApplicationService.regenerateAccessTokenWithRefreshToken(testRefreshToken, response);
            assertDoesNotThrow(() -> userAuthApplicationService.logout(response));

            // Then - 로그인 검증
            assertNotNull(loginResult);
            assertEquals(testAccessToken, loginResult.getAccessToken());
            assertEquals(testRefreshToken, loginResult.getRefreshToken());

            // Then - 토큰 재생성 검증
            assertNotNull(refreshResult);
            assertEquals(testNewAccessToken, refreshResult.getAccessToken());
            assertEquals(testNewRefreshToken, refreshResult.getRefreshToken());

            // Then - 모든 메서드 호출 검증
            verify(userQueryPort).findByEmail(testEmail);
            verify(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            verify(jwtTokenService).generateToken(testUser, true);
            verify(jwtTokenService).regenerateTokenWithRefreshToken(testRefreshToken);
            verify(securityService).getCurrentUserId();
            verify(securityService).getCurrentAuthority();
            verify(userAuthDomainService).performLogout(testUserId, testAuthority, response);
        }

        @Test
        @DisplayName("autoLogin=false 로그인 후 토큰 재생성은 불가능하다")
        void userAuthenticationFlow_WithoutAutoLogin_CannotRegenerateToken() {
            // Given - 로그인 (autoLogin=false)
            when(userQueryPort.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
            doNothing().when(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            when(jwtTokenService.generateToken(testUser, false)).thenReturn(jwtTokenWithoutRefresh);
            doNothing().when(redisAccessTokenPort).saveToken(testUserId, testAuthority, testAccessToken);
            doNothing().when(refreshTokenHolder).setToken(null);

            // When - 로그인
            TokenDto loginResult = userAuthApplicationService.login(loginCommand, response);

            // Then - 로그인 결과 검증
            assertNotNull(loginResult);
            assertEquals(testAccessToken, loginResult.getAccessToken());
            assertNull(loginResult.getRefreshToken());

            // Then - RefreshToken 저장이 호출되지 않았는지 검증
            verify(redisRefreshTokenPort, never()).saveToken(any(), any(), any());
            verify(refreshTokenHolder).setToken(null);
        }

        @Test
        @DisplayName("로그인 실패 후 토큰 재생성과 로그아웃이 호출되지 않는다")
        void loginFailure_DoesNotProceedToNextSteps() {
            // Given
            when(userQueryPort.findByEmail(testEmail)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(
                    UnAuthorizedException.class,
                    () -> userAuthApplicationService.login(loginCommand, response)
            );

            // Then - 후속 작업이 호출되지 않았는지 검증
            verify(userQueryPort).findByEmail(testEmail);
            verify(securityService, never()).createAuthenticationWithLogin(any(), any());
            verify(jwtTokenService, never()).generateToken(any(), anyBoolean());
            verify(jwtTokenService, never()).regenerateTokenWithRefreshToken(any());
            verify(userAuthDomainService, never()).performLogout(any(), any(), any());
        }

        @Test
        @DisplayName("로그인 성공 후 토큰 재생성 실패 시 로그아웃은 독립적으로 동작한다")
        void loginSuccess_TokenRegenerationFailure_LogoutWorksIndependently() {
            // Given - 로그인 성공
            when(userQueryPort.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
            doNothing().when(securityService).createAuthenticationWithLogin(testEmail, testPassword);
            when(jwtTokenService.generateToken(testUser, true)).thenReturn(jwtToken);
            doNothing().when(redisAccessTokenPort).saveToken(testUserId, testAuthority, testAccessToken);
            doNothing().when(redisRefreshTokenPort).saveToken(testUserId, testAuthority, testRefreshToken);
            doNothing().when(refreshTokenHolder).setToken(testRefreshToken);

            // Given - 토큰 재생성 실패
            when(jwtTokenService.regenerateTokenWithRefreshToken(testRefreshToken))
                    .thenThrow(new UnAuthorizedException(ErrorCode.JWT_EXPIRED));

            // Given - 로그아웃 성공
            when(securityService.getCurrentUserId()).thenReturn(testUserId);
            when(securityService.getCurrentAuthority()).thenReturn(testAuthority);
            doNothing().when(userAuthDomainService).performLogout(testUserId, testAuthority, response);

            // When - 로그인 성공
            TokenDto loginResult = userAuthApplicationService.login(autoLoginCommand, response);
            assertNotNull(loginResult);

            // When - 토큰 재생성 실패
            assertThrows(
                    UnAuthorizedException.class,
                    () -> userAuthApplicationService.regenerateAccessTokenWithRefreshToken(testRefreshToken, response)
            );

            // When - 로그아웃 성공
            assertDoesNotThrow(() -> userAuthApplicationService.logout(response));

            // Then - 각각 독립적으로 동작했는지 검증
            verify(jwtTokenService).generateToken(testUser, true);
            verify(jwtTokenService).regenerateTokenWithRefreshToken(testRefreshToken);
            verify(userAuthDomainService).performLogout(testUserId, testAuthority, response);
        }
    }
}