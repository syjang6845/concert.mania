package concert.mania.concert.infrastructure.persistence.jpa.adapter.query;

import concert.mania.concert.application.port.out.query.ConcertQueryPort;
import concert.mania.concert.domain.model.Concert;
import concert.mania.concert.infrastructure.persistence.jpa.entity.ConcertJpaEntity;
import concert.mania.concert.infrastructure.persistence.jpa.querydsl.ConcertCustomRepository;
import concert.mania.concert.infrastructure.persistence.jpa.repository.DataJpaConcertRepository;
import concert.mania.concert.infrastructure.persistence.mapper.ConcertMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 콘서트 조회 영속성 어댑터
 * 콘서트 관련 조회 포트 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertQueryAdapter implements ConcertQueryPort {

    private final DataJpaConcertRepository concertRepository;
    private final ConcertMapper concertMapper;

    @Override
    public Optional<Concert> findById(Long id) {
        return concertRepository.findById(id)
                .map(concertMapper::toDomainWithSeatGrades);
    }

    @Override
    public List<Concert> findAll() {
        return concertRepository.findConcertsAll().stream()
                .map(concertMapper::toDomainWithSeatGrades)
                .collect(Collectors.toList());
    }


}