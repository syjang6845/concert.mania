package concert.mania.concert.infrastructure.persistence.jpa.entity;

import concert.mania.concert.domain.model.type.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "users")
public class UserJpaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 64)
    private String name;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(nullable = false, length = 256, unique = true)
    private String email;

    @Column(nullable = false, length = 256)
    private String password;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserJpaEntity userJpaEntity = (UserJpaEntity) o;
        return Objects.equals(id, userJpaEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
