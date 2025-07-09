package concert.mania.concert.infrastructure.persistence.jpa.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.ConcertCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static concert.mania.concert.infrastructure.persistence.jpa.entity.QConcertJpaEntity.concertJpaEntity;

/**
 * ConcertCustomRepository 인터페이스의 구현체
 * QueryDSL을 사용하여 동적 쿼리를 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class DataJpaConcertRepositoryImpl implements ConcertCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<ConcertJpaEntity> findConcertsAll() {
        return queryFactory.selectFrom(concertJpaEntity)
                .join(concertJpaEntity.seatGrades).fetchJoin()
                .fetch();
    }


}