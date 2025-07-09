package concert.mania.concert.application.service;

import concert.mania.concert.application.port.in.ConcertQueryUseCase;
import concert.mania.concert.application.port.out.query.ConcertQueryPort;
import concert.mania.concert.domain.model.Concert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 콘서트 애플리케이션 서비스
 * 콘서트 관련 유스케이스를 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConcertApplicationService implements ConcertQueryUseCase {

    private final ConcertQueryPort concertQueryPort;

    @Override
    public List<Concert> getAllConcerts() {
        log.info("모든 콘서트 목록 조회");
        return concertQueryPort.findAll();
    }



    @Override
    public Concert getConcertById(Long concertId) {
        log.info("콘서트 상세 정보 조회: {}", concertId);
        return concertQueryPort.findById(concertId)
                .orElseThrow(() -> new NoSuchElementException("콘서트를 찾을 수 없습니다. ID: " + concertId));
    }
}
