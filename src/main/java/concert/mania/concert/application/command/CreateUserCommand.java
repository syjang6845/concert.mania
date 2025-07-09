package concert.mania.concert.application.command;

import lombok.Builder;
import lombok.Getter;
import concert.mania.concert.domain.model.type.RoleType;

/**
 * Command for creating a new user
 */
@Getter
@Builder
public class CreateUserCommand {
    private final String email;
    private final String password;
    private final String name;
    private final RoleType role;
}
