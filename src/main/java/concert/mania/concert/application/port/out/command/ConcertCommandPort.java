package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.Concert;

import java.util.List;

/**
 * 콘서트 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface ConcertCommandPort {
    
    /**
     * 콘서트 저장
     * 
     * @param concert 저장할 콘서트
     * @return 저장된 콘서트
     */
    Concert save(Concert concert);
    
    /**
     * 콘서트 일괄 저장
     * 
     * @param concerts 저장할 콘서트 목록
     * @return 저장된 콘서트 목록
     */
    List<Concert> saveAll(List<Concert> concerts);
    
    /**
     * 콘서트 삭제
     * 
     * @param concertId 삭제할 콘서트 ID
     */
    void delete(Long concertId);
    
    /**
     * 콘서트 활성화 상태 변경
     * 
     * @param concertId 콘서트 ID
     * @param active 활성화 여부
     * @return 업데이트된 콘서트
     */
    Concert updateActiveStatus(Long concertId, boolean active);
    
    /**
     * 콘서트 정보 업데이트
     * 
     * @param concert 업데이트할 콘서트 정보
     * @return 업데이트된 콘서트
     */
    Concert update(Concert concert);
}