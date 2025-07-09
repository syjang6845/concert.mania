package concert.mania.concert.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import concert.mania.concert.application.command.CreateUserCommand;
import concert.mania.concert.domain.model.type.RoleType;

/**
 * Request DTO for creating a new user
 */
public record CreateUserRequest(
    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 입력하세요.")
    @Schema(description = "로그인 이메일")
    String email,

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 30, message = "비밀번호는 8자 이상으로 30자 이하로 해야합니다.")
    @Pattern(regexp = "^(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+{}\"';<>]+$", message = "비밀번호는 영문+숫자+특수기호 조합으로 가능합니다.")
    @Schema(description = "비밀번호")
    String password,

    @NotNull
    @Schema(description = "사용자 이름")
    String name,
    
    @NotNull(message = "Role is required")
    RoleType role

) {
    public CreateUserCommand toCommand() {
        return CreateUserCommand.builder()
                .email(this.email())
                .password(this.password())
                .name(this.name())
                .role(this.role())
                .build();
    }

}