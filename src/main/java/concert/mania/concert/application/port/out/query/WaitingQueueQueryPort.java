package concert.mania.concert.application.port.out.query;

import concert.mania.concert.domain.model.WaitingQueue;

import java.util.List;
import java.util.Optional;

/**
 * 대기열 조회 포트
 * 대기열 정보 조회 기능을 정의
 */
public interface WaitingQueueQueryPort {
    
    /**
     * 대기열 ID로 대기열 정보 조회
     * @param id 대기열 ID
     * @return 대기열 정보
     */
    Optional<WaitingQueue> findById(Long id);
    
    /**
     * 콘서트 ID와 사용자 ID로 대기열 정보 조회
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     * @return 대기열 정보
     */
    Optional<WaitingQueue> findByConcertIdAndUserId(Long concertId, Long userId);
    
    /**
     * 콘서트 ID로 모든 대기열 정보 조회
     * @param concertId 콘서트 ID
     * @return 대기열 정보 목록
     */
    List<WaitingQueue> findAllByConcertId(Long concertId);
    
    /**
     * 콘서트 ID와 상태로 대기열 정보 조회
     * @param concertId 콘서트 ID
     * @param status 대기 상태
     * @return 대기열 정보 목록
     */
    List<WaitingQueue> findAllByConcertIdAndStatus(Long concertId, WaitingQueue.WaitingStatus status);
    
    /**
     * 콘서트의 대기열 총 인원 조회
     * @param concertId 콘서트 ID
     * @return 대기열 총 인원
     */
    int countByConcertId(Long concertId);
    
    /**
     * 콘서트의 대기 상태별 인원 조회
     * @param concertId 콘서트 ID
     * @param status 대기 상태
     * @return 해당 상태의 대기열 인원
     */
    int countByConcertIdAndStatus(Long concertId, WaitingQueue.WaitingStatus status);
    
    /**
     * 다음 처리할 대기열 항목 조회 (대기 중인 항목 중 가장 앞선 위치)
     * @param concertId 콘서트 ID
     * @return 다음 처리할 대기열 정보
     */
    Optional<WaitingQueue> findNextWaiting(Long concertId);
}