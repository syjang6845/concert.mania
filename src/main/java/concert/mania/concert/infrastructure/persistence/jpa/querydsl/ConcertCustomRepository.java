package concert.mania.concert.infrastructure.persistence.jpa.querydsl;

import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 콘서트 엔티티에 대한 사용자 정의 쿼리 인터페이스
 * QueryDSL을 사용한 동적 쿼리 메서드를 정의합니다.
 */
public interface ConcertCustomRepository {

    List<ConcertJpaEntity> findConcertsAll();

}