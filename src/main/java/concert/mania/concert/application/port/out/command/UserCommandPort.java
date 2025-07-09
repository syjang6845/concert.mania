package concert.mania.concert.application.port.out.command;

import org.springframework.data.repository.query.Param;
import concert.mania.concert.domain.model.User;

public interface UserCommandPort {
    // 사용자 저장 (생성 및 업데이트)
    User createUser(User user);

    // 사용자 삭제
    void delete(User user);

    void updatePasswordById(@Param("userId") Long userId, @Param("password") String password);

}
