package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.Concert;
import java.util.List;

/**
 * 콘서트 조회 유스케이스 인터페이스
 * 콘서트 관련 조회 기능을 정의
 */
public interface ConcertQueryUseCase {

    /**
     * 모든 콘서트 목록 조회
     * @return 콘서트 목록
     */
    List<Concert> getAllConcerts();

    /**
     * 콘서트 상세 정보 조회
     * @param concertId 콘서트 ID
     * @return 콘서트 상세 정보
     */
    Concert getConcertById(Long concertId);
}
