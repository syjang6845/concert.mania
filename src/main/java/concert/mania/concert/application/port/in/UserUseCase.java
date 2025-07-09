package concert.mania.concert.application.port.in;

import concert.mania.concert.application.command.CreateUserCommand;
import concert.mania.concert.application.command.DeleteUserCommand;
import concert.mania.concert.application.query.VerifyPasswordQuery;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.infrastructure.web.dto.response.UserProfileResponse;

public interface UserUseCase {
    /**
     * 사용자 생성
     * @param command 사용자 생성 명령
     * @return 생성된 사용자 프로필 응답
     */
    UserProfileResponse createUser(CreateUserCommand command);

    /**
     * 사용자 삭제
     * @param command 사용자 삭제 명령
     */
    void deleteUser(DeleteUserCommand command);


    User getUserInfoByEmail(String email);

    Boolean verifyPassword(VerifyPasswordQuery query);

}
