package concert.mania.concert.domain.model;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import concert.mania.concert.domain.model.type.Authority;
import concert.mania.concert.domain.model.type.RoleType;

import java.time.LocalDateTime;

@Getter
@Builder// private Builder
@ToString
public class User {
    private final Long id;
    private String name; // 변경 가능한 필드는 final 제거
    private String password;
    private final String email;
    private final RoleType role;
    private final LocalDateTime createdAt;

    public void updatePassword(String password) {
        this.password = password;
    }

    // 새 User 생성용 정적 팩토리 메서드
    public static User create(String email, String password,
                              String name, RoleType role) {
        // 도메인 불변식 검증
        validateCreateUser(email, password, role);

        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(role)
                .build();
    }

    // 기존 User 로드용 정적 팩토리 메서드
    public static User of(Long id, String email, String password,
                          String name, RoleType role) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null for existing user.");

        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .name(name)
                .role(role)
                .build();
    }

    private static void validateCreateUser(String email, String password,
                                           RoleType role) {
        if (email == null) throw new IllegalArgumentException("Invalid email.");
        if (password == null) throw new IllegalArgumentException("Weak password.");
        if (role == null) throw new IllegalArgumentException("Role required.");
    }

    /**
     * 현재 사용자의 Authority 반환
     */
    public Authority getAuthority() {
        return Authority.from(this.role);
    }
}

