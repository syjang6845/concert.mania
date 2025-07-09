package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.Concert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 콘서트 조회(Query) 작업을 위한 포트 인터페이스
 */
public interface ConcertQueryPort {
    
    /**
     * ID로 콘서트 조회
     * 
     * @param id 콘서트 ID
     * @return 콘서트 (Optional)
     */
    Optional<Concert> findById(Long id);
    
    /**
     * 모든 콘서트 조회
     * 
     * @return 콘서트 목록
     */
    List<Concert> findAll();

}