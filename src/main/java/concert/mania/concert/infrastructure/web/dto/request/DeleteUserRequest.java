package concert.mania.concert.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import concert.mania.concert.application.command.DeleteUserCommand;

public record DeleteUserRequest(
        @NotBlank(message = "Password is required")
        String password,

        String reason
) {
        public DeleteUserCommand toCommand(Long userId) {
                return DeleteUserCommand.builder()
                        .userId(userId)
                        .password(this.password())
                        .reason(this.reason())
                        .build();
        }
}
