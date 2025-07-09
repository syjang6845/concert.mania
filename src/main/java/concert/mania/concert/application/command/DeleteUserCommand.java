package concert.mania.concert.application.command;

import lombok.Builder;
import lombok.Getter;

/**
 * Command for deleting a user
 */
@Getter
@Builder
public class DeleteUserCommand {
    private final Long userId;
    private final String password;
    private final String reason;
}