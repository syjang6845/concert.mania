package concert.mania.concert.infrastructure.persistence.jpa.adapter.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import concert.mania.concert.application.port.out.command.UserCommandPort;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.infrastructure.persistence.jpa.entity.UserJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaUserRepository;
import concert.mania.concert.infrastructure.persistence.mapper.UserMapper;

@Component
@Transactional
@RequiredArgsConstructor
public class UserCommandAdapter implements UserCommandPort {

    private final DataJpaUserRepository dataJpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public User createUser(User user) {
        UserJpaEntity userJpaEntity = userMapper.toJpaEntityForCreate(user);
        UserJpaEntity savedEntity = dataJpaUserRepository.save(userJpaEntity);
        // !! 핵심: 저장 후 ID가 할당된 JPA Entity를 다시 User 애그리거트로 매핑하여 반환 !!
        return userMapper.toDomainModel(savedEntity);
    }


    @Override
    public void delete(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Cannot delete user without ID");
        }

        // 사용자 삭제
        dataJpaUserRepository.deleteById(user.getId());
    }

    @Override
    public void updatePasswordById(Long userId, String password) {
        dataJpaUserRepository.updatePasswordById(userId, password);
    }



}
