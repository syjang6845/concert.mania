package concert.mania.concert.infrastructure.persistence.mapper;

import concert.mania.concert.domain.model.User;
import concert.mania.concert.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING) // Spring Bean으로 등록
@Component
public interface UserMapper {

    @Mapping(target = "email", source = "email") // 값 객체의 내부 값 매핑
    @Mapping(target = "password", source = "password") // 값 객체의 내부 값 매핑
    @Mapping(target = "name", source = "name") // UserProfile 값 객체 내부 필드 매핑
    @Mapping(target = "role", source = "role") // Enum 매핑
    UserJpaEntity toJpaEntityForCreate(User user);


    void updateJpaEntity(@MappingTarget UserJpaEntity target, User user);


    // --- JPA Entity (UserJpaEntity) -> Domain (User) 매핑 ---

    // 3. UserJpaEntity를 Domain User로 변환 (재구성)
    // User 도메인 모델의 protected 생성자를 사용하도록 MapStruct가 코드를 생성합니다.
    @Mapping(target = "createdAt", source = "createdAt")
    User toDomainModel(UserJpaEntity entity);


}
