package concert.mania.concert.application.command;

import lombok.Builder;
import lombok.Getter;
import concert.mania.concert.domain.model.type.RoleType;

/**
 * Command for user login
 */
@Getter
@Builder
public class LoginCommand {
    private final String email;
    private final String password;
    private final RoleType role;
    private final boolean autoLogin;
}