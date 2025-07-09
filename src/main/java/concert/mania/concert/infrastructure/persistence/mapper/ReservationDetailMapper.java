package concert.mania.concert.infrastructure.persistence.mapper;

import concert.mania.concert.domain.model.ReservationDetail;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ReservationDetailJpaEntity;
import org.mapstruct.*;

/**
 * 예매 상세 도메인 모델과 JPA 엔티티 간의 매핑을 처리하는 MapStruct 인터페이스
 */
@Mapper(componentModel = "spring", 
        uses = {SeatMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationDetailMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환
     * 
     * @param entity 변환할 JPA 엔티티
     * @return 변환된 도메인 모델
     */
    @Mapping(target = "reservation", ignore = true) // 순환 참조 방지를 위해 별도 처리
    ReservationDetail toDomain(ReservationDetailJpaEntity entity);

    /**
     * 도메인 모델을 JPA 엔티티로 변환
     * 
     * @param domain 변환할 도메인 모델
     * @return 변환된 JPA 엔티티
     */
    @Mapping(target = "reservation", ignore = true) // 순환 참조 방지를 위해 별도 처리
    ReservationDetailJpaEntity toEntity(ReservationDetail domain);

    /**
     * JPA 엔티티를 도메인 모델로 변환 (ID만 사용)
     * 
     * @param id 엔티티 ID
     * @return ID만 설정된 도메인 모델
     */
    default ReservationDetail toDomainById(Long id) {
        if (id == null) {
            return null;
        }
        return ReservationDetail.builder().id(id).build();
    }

    /**
     * 기존 엔티티에 도메인 모델 데이터 업데이트
     * 
     * @param domain 업데이트할 도메인 모델 데이터
     * @param entity 업데이트될 기존 엔티티
     */
    @Mapping(target = "id", ignore = true) // ID는 변경하지 않음
    @Mapping(target = "reservation", ignore = true) // 순환 참조 방지를 위해 별도 처리
    void updateEntityFromDomain(ReservationDetail domain, @MappingTarget ReservationDetailJpaEntity entity);
}