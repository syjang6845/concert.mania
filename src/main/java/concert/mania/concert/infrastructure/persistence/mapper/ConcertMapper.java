package concert.mania.concert.infrastructure.persistence.mapper;

import concert.mania.concert.domain.model.Concert;
import concert.mania.concert.domain.model.SeatGrade;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.entity.SeatGradeJpaEntity;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * 콘서트 도메인 모델과 JPA 엔티티 간의 매핑을 처리하는 MapStruct 인터페이스
 */
@Mapper(componentModel = SPRING) // Spring Bean으로 등록
@Component
public interface ConcertMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환 (목록 조회용 - 간단한 매핑)
     *
     * @param entity 변환할 JPA 엔티티
     * @return 변환된 도메인 모델
     */
    @Mapping(target = "seatGrades", ignore = true) // 순환 참조 방지
    @Mapping(target = "seats", ignore = true) // 순환 참조 방지
    Concert toDomain(ConcertJpaEntity entity);

    /**
     * JPA 엔티티를 도메인 모델로 변환 (상세 조회용 - 좌석 등급 포함)
     *
     * @param entity 변환할 JPA 엔티티
     * @return 변환된 도메인 모델
     */
    @Mapping(target = "seats", ignore = true) // 순환 참조 방지
    @Mapping(target = "seatGrades", source = "seatGrades", qualifiedByName = "mapSeatGradesWithoutConcert")
    Concert toDomainWithSeatGrades(ConcertJpaEntity entity);

    /**
     * 도메인 모델을 JPA 엔티티로 변환
     *
     * @param domain 변환할 도메인 모델
     * @return 변환된 JPA 엔티티
     */
    @Mapping(target = "seatGrades", ignore = true) // 양방향 관계는 별도로 처리
    @Mapping(target = "seats", ignore = true) // 양방향 관계는 별도로 처리
    ConcertJpaEntity toEntity(Concert domain);

    /**
     * 좌석 등급 목록을 매핑 (순환 참조 방지)
     */
    @Named("mapSeatGradesWithoutConcert")
    default List<SeatGrade> mapSeatGradesWithoutConcert(List<SeatGradeJpaEntity> seatGrades) {
        if (seatGrades == null) {
            return null;
        }

        return seatGrades.stream()
                .map(this::mapSeatGradeWithoutConcert)
                .collect(Collectors.toList());
    }

    /**
     * 좌석 등급을 매핑 (Concert 참조 제외)
     */
    @Mapping(target = "concert", ignore = true) // 순환 참조 방지
    @Mapping(target = "seats", ignore = true) // 성능을 위해 제외
    SeatGrade mapSeatGradeWithoutConcert(SeatGradeJpaEntity entity);

}