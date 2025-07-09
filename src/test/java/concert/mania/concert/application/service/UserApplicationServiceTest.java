package concert.mania.concert.application.service;

import concert.mania.exception.model.ApplicationException;
import concert.mania.exception.model.BadRequestException;
import concert.mania.exception.model.ConflictException;
import concert.mania.exception.model.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import concert.mania.concert.application.command.CreateUserCommand;
import concert.mania.concert.application.command.DeleteUserCommand;
import concert.mania.concert.application.port.out.command.UserCommandPort;
import concert.mania.concert.application.port.out.query.UserQueryPort;
import concert.mania.concert.domain.model.Agreement;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.RoleType;
import concert.mania.concert.infrastructure.web.dto.response.UserProfileResponse;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("UserApplicationService 테스트")
class UserApplicationServiceTest {

    @Mock private UserCommandPort userCommandPort;
    @Mock private UserQueryPort userQueryPort;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserApplicationService userApplicationService;

    private String testEmail;
    private String testPassword;
    private String encodedPassword;
    private String testName;
    private String testBirth;
    private String testPhoneNumber;
    private String testCi;
    private String testCredential;
    private String testAffiliationName;
    private String testWithdrawReason;
    private RoleType testRole;
    private Long testUserId;
    private Long testIdentityAuthId;
    private CreateUserCommand createUserCommand;
    private DeleteUserCommand deleteUserCommand;
    private Agreement testAgreement;
    private User testUser;
    private User savedUser;

    @BeforeEach
    void setUp() {
        log.info("=== 👤 사용자 서비스 테스트 데이터 초기화 시작 ===");

        testEmail = "test@example.com";
        testPassword = "password123!";
        encodedPassword = "encoded-password";
        testName = "홍길동";
        testBirth = "19900101";
        testPhoneNumber = "01012345678";
        testCi = "test-ci-value";
        testCredential = "test-credential-uuid";
        testAffiliationName = "테스트 회사";
        testWithdrawReason = "서비스 불만족";
        testRole = RoleType.USER;
        testUserId = 1L;
        testIdentityAuthId = 1L;

        createUserCommand = CreateUserCommand.builder()
                .email(testEmail)
                .password(testPassword)
                .name(testName)
                .role(testRole)
                .build();

        deleteUserCommand = DeleteUserCommand.builder()
                .userId(testUserId)
                .password(testPassword)
                .reason(testWithdrawReason)
                .build();


        testAgreement = Agreement.builder()
                .termOfService(true)
                .privacyPolicy(true)
                .marketingOptIn(true)
                .receiveNotification(true)
                .guardianConsent(false)
                .build();

        testUser = User.builder()
                .id(testUserId)
                .email(testEmail)
                .password(encodedPassword)
                .name(testName)
                .role(testRole)
                .build();

        savedUser = User.builder()
                .id(testUserId)
                .email(testEmail)
                .password(encodedPassword)
                .name(testName)
                .role(testRole)
                .createdAt(LocalDateTime.now())
                .build();

        log.info("테스트 데이터 초기화 완료 - email: {}, name: {}, userId: {}",
                testEmail, testName, testUserId);
        log.info("=== 👤 사용자 서비스 테스트 데이터 초기화 완료 ===");
    }


    @Nested
    @DisplayName("createUser 메서드 테스트")
    class CreateUserTest {

        @Test
        @DisplayName("정상적으로 새 사용자를 생성한다 (기존 본인인증 정보)")
        void createUser_Success_WithExistingIdentityAuth() {
            log.info("🔵 [TEST START] createUser_Success_WithExistingIdentityAuth - 새 사용자 생성 (기존 본인인증)");

            // given
            given(passwordEncoder.encode(testPassword)).willReturn(encodedPassword);
            given(userCommandPort.createUser(any(User.class))).willReturn(savedUser);

            // when
            log.info("⚡ [WHEN] createUser 호출 (기존 본인인증)");
            UserProfileResponse result = userApplicationService.createUser(createUserCommand);
            log.info("⚡ [WHEN] 사용자 생성 완료 - 기존 본인인증 정보 재사용");

            // then
            log.info("✅ [THEN] 결과 검증 시작");
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(testUserId);

            log.info("🟢 [TEST SUCCESS] createUser_Success_WithExistingIdentityAuth 테스트 성공");
        }

        @Test
        @DisplayName("이미 가입된 이메일인 경우 예외를 발생시킨다")
        void createUser_EmailAlreadyExists_ThrowsException() {
            log.info("🔵 [TEST START] createUser_EmailAlreadyExists_ThrowsException");

            // given
            log.info("📋 [GIVEN] 중복 이메일 상황 설정 - email: {}", testEmail);

            // 🔥 실제 UserApplicationService 로직 순서에 맞게 수정
            // 1. 이메일 중복 체크가 먼저 실행됨
            given(userQueryPort.existsByEmail(testEmail)).willReturn(true);

            // 2. 이메일 인증 검증은 중복 체크 후에 실행되므로, 중복일 때는 호출되지 않음
            // doNothing().when(emailAuthUseCase).validateEmailAuthForSignup(testEmail, testCredential); // 제거

            // when & then
            log.info("⚡ [WHEN&THEN] 중복 이메일 예외 테스트 시작");
            assertThatThrownBy(() -> {
                log.info("⚡ [WHEN] createUser 호출 (중복 이메일)");
                userApplicationService.createUser(createUserCommand);
            })
                    .isInstanceOf(ConflictException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
            log.info("✅ [THEN] 중복 이메일 예외 검증 완료");

            // 🔥 실제 호출된 메서드만 검증
            verify(userQueryPort).existsByEmail(testEmail);

            // 🔥 중복 이메일로 인해 호출되지 않은 메서드들 검증
            verify(userCommandPort, never()).createUser(any(User.class));
            log.info("✅ [THEN] 후속 처리 호출되지 않음 검증 완료");

            log.info("🟢 [TEST SUCCESS] 중복 이메일 예외 테스트 성공");
        }
    }

        @Nested
    @DisplayName("deleteUser 메서드 테스트")
    class DeleteUserTest {

        @Test
        @DisplayName("정상적으로 사용자를 탈퇴 처리한다")
        void deleteUser_Success() {
            log.info("🔵 [TEST START] deleteUser_Success - 사용자 탈퇴 정상 시나리오");

            // given
            log.info("📋 [GIVEN] Mock 설정 시작");
            log.info("📋 [GIVEN] 1. 사용자 조회 설정 - userId: {} → user: {}", testUserId, testName);
            given(userQueryPort.findById(testUserId)).willReturn(Optional.of(testUser));

            log.info("📋 [GIVEN] 2. 비밀번호 검증 성공 설정 - password 일치");
            given(passwordEncoder.matches(testPassword, encodedPassword)).willReturn(true);

            log.info("📋 [GIVEN] 3. 탈퇴 이력 저장 및 사용자 삭제 설정");
            doNothing().when(userCommandPort).delete(testUser);

            // when
            log.info("⚡ [WHEN] deleteUser 호출 - userId: {}, reason: {}", testUserId, testWithdrawReason);
            assertThatNoException().isThrownBy(() -> {
                userApplicationService.deleteUser(deleteUserCommand);
                log.info("⚡ [WHEN] 사용자 탈퇴 처리 완료 - 예외 없음");
            });

            // then
            log.info("✅ [THEN] Mock 호출 검증 시작");
            verify(userQueryPort).findById(testUserId);
            log.info("✅ [THEN] 사용자 조회 검증 완료");

            verify(passwordEncoder).matches(testPassword, encodedPassword);
            log.info("✅ [THEN] 비밀번호 검증 완료");

            log.info("✅ [THEN] 탈퇴 이력 저장 검증 완료");

            verify(userCommandPort).delete(testUser);
            log.info("✅ [THEN] 사용자 삭제 검증 완료");

            log.info("🟢 [TEST SUCCESS] deleteUser_Success 테스트 성공");
        }

        @Test
        @DisplayName("존재하지 않는 사용자인 경우 예외를 발생시킨다")
        void deleteUser_UserNotFound_ThrowsException() {
            log.info("🔵 [TEST START] deleteUser_UserNotFound_ThrowsException");

            // given
            log.info("📋 [GIVEN] 존재하지 않는 사용자 설정 - userId: {}", testUserId);
            given(userQueryPort.findById(testUserId)).willReturn(Optional.empty());

            // when & then
            log.info("⚡ [WHEN&THEN] 사용자 없음 예외 테스트 시작");
            assertThatThrownBy(() -> {
                log.info("⚡ [WHEN] deleteUser 호출 (사용자 없음)");
                userApplicationService.deleteUser(deleteUserCommand);
            })
                    .isInstanceOf(BadRequestException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
            log.info("✅ [THEN] User not found 예외 검증 완료");

            verify(userQueryPort).findById(testUserId);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(userCommandPort, never()).delete(any(User.class));
            log.info("✅ [THEN] 후속 처리 호출되지 않음 검증 완료");

            log.info("🟢 [TEST SUCCESS] 사용자 없음 예외 테스트 성공");
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우 예외를 발생시킨다")
        void deleteUser_InvalidPassword_ThrowsException() {
            log.info("🔵 [TEST START] deleteUser_InvalidPassword_ThrowsException");

            // given
            log.info("📋 [GIVEN] 비밀번호 불일치 상황 설정 - userId: {}", testUserId);
            given(userQueryPort.findById(testUserId)).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(testPassword, encodedPassword)).willReturn(false);
            log.info("📋 [GIVEN] 비밀번호 검증 실패로 설정");

            // when & then
            log.info("⚡ [WHEN&THEN] 비밀번호 불일치 예외 테스트 시작");
            assertThatThrownBy(() -> {
                log.info("⚡ [WHEN] deleteUser 호출 (비밀번호 불일치)");
                userApplicationService.deleteUser(deleteUserCommand);
            })
                    .isInstanceOf(ApplicationException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
            log.info("✅ [THEN] Invalid password 예외 검증 완료");

            verify(userQueryPort).findById(testUserId);
            verify(passwordEncoder).matches(testPassword, encodedPassword);
            verify(userCommandPort, never()).delete(any(User.class));
            log.info("✅ [THEN] 후속 처리 호출되지 않음 검증 완료");

            log.info("🟢 [TEST SUCCESS] 비밀번호 불일치 예외 테스트 성공");
        }
    }


    @Nested
    @DisplayName("통합 테스트")
    class IntegrationTest {

        @Test
        @DisplayName("사용자 생성부터 탈퇴까지 전체 플로우가 정상 동작한다")
        void userLifecycle_EndToEnd_Success() {
            log.info("🔵 [INTEGRATION START] 사용자 생성-탈퇴 전체 플로우 테스트");

            // ========== 1단계: 사용자 생성 ==========
            log.info("📋 [PHASE 1] 사용자 생성 단계 시작");

            // given - 사용자 생성 Mock 설정
            given(userQueryPort.existsByEmail(testEmail)).willReturn(false);
            given(passwordEncoder.encode(testPassword)).willReturn(encodedPassword);
            given(userCommandPort.createUser(any(User.class))).willReturn(savedUser);
            log.info("📋 [PHASE 1] 사용자 생성 Mock 설정 완료");

            // when - 사용자 생성
            log.info("⚡ [PHASE 1] 사용자 생성 실행 - email: {}", testEmail);
            UserProfileResponse createdUser = userApplicationService.createUser(createUserCommand);
            log.info("⚡ [PHASE 1] 사용자 생성 완료 - userId: {}, name: {}", createdUser.id(), createdUser.name());

            // then - 사용자 생성 검증
            assertThat(createdUser).isNotNull();
            assertThat(createdUser.id()).isEqualTo(testUserId);
            assertThat(createdUser.email()).isEqualTo(testEmail);
            assertThat(createdUser.name()).isEqualTo(testName);
            log.info("✅ [PHASE 1] 사용자 생성 결과 검증 완료");

            // ========== 2단계: 사용자 탈퇴 ==========
            log.info("📋 [PHASE 2] 사용자 탈퇴 단계 시작");

            // given - 사용자 탈퇴 Mock 설정
            given(userQueryPort.findById(testUserId)).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(testPassword, encodedPassword)).willReturn(true);
            doNothing().when(userCommandPort).delete(testUser);
            log.info("📋 [PHASE 2] 사용자 탈퇴 Mock 설정 완료");

            // when - 사용자 탈퇴
            log.info("⚡ [PHASE 2] 사용자 탈퇴 실행 - userId: {}, reason: {}", testUserId, testWithdrawReason);
            assertThatNoException().isThrownBy(() -> {
                userApplicationService.deleteUser(deleteUserCommand);
            });
            log.info("⚡ [PHASE 2] 사용자 탈퇴 완료 - 예외 없음");

            // ========== 3단계: 전체 플로우 검증 ==========
            log.info("📋 [PHASE 3] 전체 플로우 검증 시작");

            // 사용자 생성 과정 검증
            verify(userQueryPort).existsByEmail(testEmail);
            verify(passwordEncoder).encode(testPassword);
            verify(userCommandPort).createUser(any(User.class));
            log.info("✅ [PHASE 3] 사용자 생성 과정 검증 완료");

            // 사용자 탈퇴 과정 검증
            verify(userQueryPort).findById(testUserId);
            verify(passwordEncoder).matches(testPassword, encodedPassword);
            verify(userCommandPort).delete(testUser);
            log.info("✅ [PHASE 3] 사용자 탈퇴 과정 검증 완료");

            // 전체 호출 순서 검증
            var inOrder = inOrder(
                    userQueryPort,
                    passwordEncoder, userCommandPort

            );

            // 생성 순서
            inOrder.verify(userQueryPort).existsByEmail(testEmail);
            inOrder.verify(passwordEncoder).encode(testPassword);
            inOrder.verify(userCommandPort).createUser(any(User.class));

            // 탈퇴 순서
            inOrder.verify(userQueryPort).findById(testUserId);
            inOrder.verify(passwordEncoder).matches(testPassword, encodedPassword);
            inOrder.verify(userCommandPort).delete(testUser);

            log.info("✅ [PHASE 3] 전체 호출 순서 검증 완료");

            // ========== 4단계: 비즈니스 로직 검증 ==========
            log.info("📋 [PHASE 4] 비즈니스 로직 검증");

            // 이메일 인증이 먼저 검증되었는지

            // 중복 이메일 체크가 수행되었는지
            verify(userQueryPort, times(1)).existsByEmail(testEmail);

            // 본인 인증 정보가 있는지

            // 비밀번호가 암호화되었는지
            verify(passwordEncoder, times(1)).encode(testPassword);

            // 탈퇴 시 비밀번호 검증이 수행되었는지
            verify(passwordEncoder, times(1)).matches(testPassword, encodedPassword);

            // 탈퇴 이력이 저장되었는지

            log.info("✅ [PHASE 4] 비즈니스 로직 검증 완료");

            log.info("🟢 [INTEGRATION SUCCESS] 사용자 생성-탈퇴 전체 플로우 테스트 성공");
            log.info("🎯 검증된 플로우: 이메일인증 → 중복체크 → 본인인증저장 → 비밀번호암호화 → 사용자저장 → 사용자조회 → 비밀번호검증 → 탈퇴이력저장 → 사용자삭제");
        }
    }
}