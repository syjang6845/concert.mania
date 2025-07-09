package concert.mania.concert.infrastructure.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import concert.mania.concert.application.command.LoginCommand;
import concert.mania.concert.domain.model.type.RoleType;

/**
 * Request DTO for user login
 */
public record LoginRequest (
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Password is required")
    String password,

    @NotNull
    RoleType role,

    Boolean autoLogin
) {
    public LoginCommand toCommand() {
        return LoginCommand.builder()
                .email(this.email())
                .password(this.password())
                .role(role)
                .autoLogin(autoLogin)
                .build();
    }
    
}