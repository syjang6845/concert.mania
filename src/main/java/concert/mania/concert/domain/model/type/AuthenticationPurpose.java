package concert.mania.concert.domain.model.type;

import lombok.Getter;

@Getter
public enum AuthenticationPurpose {
    REGISTER("register"),
    PASSWORD_RESET("password-reset");
    private final String value;

    AuthenticationPurpose(String value) {
        this.value = value;
    }
}
