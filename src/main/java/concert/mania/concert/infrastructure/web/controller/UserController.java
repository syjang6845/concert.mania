package concert.mania.concert.infrastructure.web.controller;

import concert.mania.common.annotations.token.ClearRefreshToken;
import concert.mania.concert.application.command.CreateUserCommand;
import concert.mania.concert.application.command.DeleteUserCommand;
import concert.mania.concert.application.port.in.UserAuthUseCase;
import concert.mania.concert.application.port.in.UserUseCase;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.infrastructure.web.docs.user.DeleteUserApiDoc;
import concert.mania.concert.infrastructure.web.docs.user.GetUserInfoApiDoc;
import concert.mania.concert.infrastructure.web.docs.user.SignupApiDoc;
import concert.mania.concert.infrastructure.web.dto.request.CreateUserRequest;
import concert.mania.concert.infrastructure.web.dto.request.DeleteUserRequest;
import concert.mania.concert.infrastructure.web.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;
    private final UserAuthUseCase userAuthUseCase;


    @GetMapping("/{email}")
    @GetUserInfoApiDoc
    public ResponseEntity<UserProfileResponse> getUserInfo(@PathVariable("email") String email) {
        User user = userUseCase.getUserInfoByEmail(email);
        UserProfileResponse response = UserProfileResponse.of(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("")
    @SignupApiDoc
    public ResponseEntity<UserProfileResponse> signup(@Valid @RequestBody CreateUserRequest request) {
        // Convert request to command
        CreateUserCommand command = request.toCommand();
        // Execute use case
        UserProfileResponse response = userUseCase.createUser(command);

        // Convert DTO to response

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{userId}")
    @ClearRefreshToken
    @DeleteUserApiDoc
    public void deleteUser(@Valid @RequestBody DeleteUserRequest request,
                                                            @PathVariable Long userId,
                                                           HttpServletResponse response) {
        DeleteUserCommand command = request.toCommand(userId);
        userUseCase.deleteUser(command);
        userAuthUseCase.logout(response);
    }
}
