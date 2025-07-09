package concert.mania.concert.domain.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Authority {
    ROLE_SUPER(0, "최고 관리자"),
    ROLE_ADMIN(1, "관리자"),
    ROLE_USER(2, "사용자");

    private final Integer number;
    private final String description;

    /**
     * RoleType을 Authority로 변환
     */
    public static Authority from(RoleType roleType) {
        return switch (roleType) {
            case USER -> ROLE_USER;
            case ADMIN -> ROLE_ADMIN;
        };
    }

    /**
     * Authority에서 RoleType 추출
     */
    public RoleType toRoleType() {
        return switch (this) {
            case ROLE_USER -> RoleType.USER;
            case ROLE_ADMIN, ROLE_SUPER -> RoleType.ADMIN;
        };
    }

    /**
     * 교육자 권한인지 확인
     */
    public boolean isUser() {
        return this == ROLE_USER;
    }

    /**
     * 관리자 권한인지 확인
     */
    public boolean isAdmin() {
        return this == ROLE_ADMIN || this == ROLE_SUPER;
    }
}
