package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.RoleType;

import java.util.List;
import java.util.Optional;

public interface UserQueryPort {
    // ID로 사용자 조회
    Optional<User> findById(Long userId);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 역할별 사용자 조회
    List<User> findByRole(RoleType role);

    // 이메일로 사용자 존재 여부 확인 (예시)
    boolean existsByEmail(String email);
}
