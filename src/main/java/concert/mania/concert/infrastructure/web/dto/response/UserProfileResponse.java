package concert.mania.concert.infrastructure.web.dto.response;

import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.RoleType;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String name, // 변경 가능한 필드는 제거
        String email,
        RoleType role,
        LocalDateTime createdAt
) {
        public static UserProfileResponse of(User user) {
            return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
        }
}
