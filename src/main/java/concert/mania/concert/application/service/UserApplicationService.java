package concert.mania.concert.application.service;

import concert.mania.exception.model.BadRequestException;
import concert.mania.exception.model.ConflictException;
import concert.mania.security.service.SecurityService;
import concert.mania.concert.application.command.CreateUserCommand;
import concert.mania.concert.application.command.DeleteUserCommand;
import concert.mania.concert.application.dto.UserProfileDto;
import concert.mania.concert.application.port.in.UserUseCase;
import concert.mania.concert.application.port.out.command.UserCommandPort;
import concert.mania.concert.application.port.out.query.UserQueryPort;
import concert.mania.concert.application.query.VerifyPasswordQuery;
import concert.mania.concert.domain.model.Agreement;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.infrastructure.web.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static concert.mania.exception.model.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService implements UserUseCase {

    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;


    // Current user context - in a real application, this would be injected or retrieved from a security context
    private Long getCurrentUserId() {
        // TODO: Implement this method to get the current user ID from the security context
        return 1L; // Placeholder
    }

    @Override
    @Transactional
    public UserProfileResponse createUser(CreateUserCommand command) {
        log.trace("사용자 생성 시작 - email: {}, name: {}", command.getEmail(), command.getName());
        if(userQueryPort.existsByEmail(command.getEmail())) {
            throw new ConflictException(DUPLICATE_EMAIL);
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(command.getPassword());
        log.debug("비밀번호 암호화 완료");

        // 4. 사용자 도메인 객체 생성
        User user = createUser(command, encodedPassword);
        log.debug("사용자 도메인 객체 생성 완료");

        // 5. 사용자 저장
        User savedUser = userCommandPort.createUser(user);
        log.debug("사용자 생성 완료 - userId: {}, email: {}", savedUser.getId(), savedUser.getEmail());

        // 6. DTO 변환 및 반환
        return UserProfileResponse.of(savedUser);

    }

    @Override
    @Transactional
    public void deleteUser(DeleteUserCommand command) {
        User user = userQueryPort.findById(command.getUserId())
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new BadRequestException(INVALID_PASSWORD);
        }

        userCommandPort.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserInfoByEmail(String email) {
        return userQueryPort.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean verifyPassword(VerifyPasswordQuery query) {
        return isPasswordMatch(query.getUserId(), query.getPassword());
    }

    private boolean isPasswordMatch(long userId, String password) {
        User findUser = userQueryPort.findById(userId)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
        return passwordEncoder.matches(password, findUser.getPassword());
    }


    // Helper method to map a User domain object to a UserProfileDto
    private UserProfileDto mapToUserProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())// Not stored in the domain model
                .build();
    }




    /**
     * 사용자 도메인 객체 생성
     */
    private User createUser(CreateUserCommand command, String encodedPassword) {
        return User.create(
                command.getEmail(),
                encodedPassword, // 🔥 암호화된 비밀번호 사용
                command.getName(),
                command.getRole()
        );
    }


}
