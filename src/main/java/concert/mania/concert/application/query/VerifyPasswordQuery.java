package concert.mania.concert.application.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifyPasswordQuery {
    private String password;
    private Long userId;
}
