package concert.mania.concert.infrastructure.persistence.mapper;

import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatJpaEntity;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * 좌석 도메인 모델과 JPA 엔티티 간의 매핑을 처리하는 MapStruct 인터페이스
 */
@Mapper(componentModel = SPRING) // Spring Bean으로 등록
@Component
public interface SeatMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환
     * 
     * @param entity 변환할 JPA 엔티티
     * @return 변환된 도메인 모델
     */
    @Mapping(target = "concert.seatGrades", ignore = true)
    @Mapping(target = "concert.seats", ignore = true)
    @Mapping(target = "seatGrade.seats", ignore = true)
    @Mapping(target = "seatGrade.concert", ignore = true)
    Seat toDomain(SeatJpaEntity entity);

    /**
     * 도메인 모델을 JPA 엔티티로 변환
     * 
     * @param domain 변환할 도메인 모델
     * @return 변환된 JPA 엔티티
     */
    SeatJpaEntity toEntity(Seat domain);

    /**
     * JPA 엔티티를 도메인 모델로 변환 (ID만 사용)
     * 
     * @param id 엔티티 ID
     * @return ID만 설정된 도메인 모델
     */
    default Seat toDomainById(Long id) {
        if (id == null) {
            return null;
        }
        return Seat.builder().id(id).build();
    }

    /**
     * 기존 엔티티에 도메인 모델 데이터 업데이트
     * 
     * @param domain 업데이트할 도메인 모델 데이터
     * @param entity 업데이트될 기존 엔티티
     */
    @Mapping(target = "id", ignore = true) // ID는 변경하지 않음
    void updateEntityFromDomain(Seat domain, @MappingTarget SeatJpaEntity entity);
}
