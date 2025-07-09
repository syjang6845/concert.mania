package concert.mania.concert.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import concert.mania.common.converter.BooleanToYNConverter;
import concert.mania.concert.domain.model.type.RoleType;

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

    @Column(nullable = false, name = "withdraw", length = 1)
    @Convert(converter = BooleanToYNConverter.class)
    private boolean withdraw;


    // 모든 필드를 받는 생성자 (선택 사항, 매퍼에서 활용 가능)
    public UserJpaEntity(String name, RoleType role,
                         String email, String password, boolean withdraw
                         ) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.withdraw = withdraw;
    }

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
