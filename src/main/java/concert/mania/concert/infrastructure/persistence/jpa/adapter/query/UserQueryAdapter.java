package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import concert.mania.concert.application.port.out.query.UserQueryPort;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.RoleType;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaUserRepository;
import concert.mania.concert.infrastructure.persistence.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {

    private final DataJpaUserRepository dataJpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public boolean existsByEmail(String email) {
        return dataJpaUserRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return dataJpaUserRepository.findByEmail(email)
                .map(userMapper::toDomainModel);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return dataJpaUserRepository.findById(userId)
                .map(userMapper::toDomainModel);
    }



    @Override
    public List<User> findByRole(RoleType role) {

        return dataJpaUserRepository.findByRole(role)
                .stream()
                .map(userMapper::toDomainModel)
                .collect(Collectors.toList());
    }
}
