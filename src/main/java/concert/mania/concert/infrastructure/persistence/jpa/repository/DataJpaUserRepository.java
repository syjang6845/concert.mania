package concert.mania.concert.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import concert.mania.concert.domain.model.type.RoleType;
import concert.mania.concert.infrastructure.persistence.jpa.entity.UserJpaEntity;

import java.util.List;
import java.util.Optional;

public interface DataJpaUserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);
    List<UserJpaEntity> findByRole(RoleType role);

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.password = :password WHERE u.id = :userId")
    void updatePasswordById(@Param("userId") Long userId, @Param("password") String password);

}
