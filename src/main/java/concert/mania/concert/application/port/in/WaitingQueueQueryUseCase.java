package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.WaitingQueue;

import java.util.List;

/**
 * 대기열 조회 유스케이스
 * 대기열 정보 조회 기능을 정의
 */
public interface WaitingQueueQueryUseCase {
    
    /**
     * 대기열 ID로 대기열 정보 조회
     * @param id 대기열 ID
     * @return 대기열 정보
     */
    WaitingQueue getWaitingQueueById(Long id);
    
    /**
     * 콘서트 ID와 사용자 ID로 대기열 정보 조회
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     * @return 대기열 정보
     */
    WaitingQueue getWaitingQueueByConcertIdAndUserId(Long concertId, Long userId);
    
    /**
     * 콘서트 ID로 모든 대기열 정보 조회
     * @param concertId 콘서트 ID
     * @return 대기열 정보 목록
     */
    List<WaitingQueue> getAllWaitingQueuesByConcertId(Long concertId);
    
    /**
     * 콘서트 ID와 상태로 대기열 정보 조회
     * @param concertId 콘서트 ID
     * @param status 대기 상태
     * @return 대기열 정보 목록
     */
    List<WaitingQueue> getWaitingQueuesByConcertIdAndStatus(Long concertId, WaitingQueue.WaitingStatus status);
    
    /**
     * 콘서트의 대기열 총 인원 조회
     * @param concertId 콘서트 ID
     * @return 대기열 총 인원
     */
    int getWaitingQueueCount(Long concertId);
    
    /**
     * 콘서트의 대기 상태별 인원 조회
     * @param concertId 콘서트 ID
     * @param status 대기 상태
     * @return 해당 상태의 대기열 인원
     */
    int getWaitingQueueCountByStatus(Long concertId, WaitingQueue.WaitingStatus status);
    
    /**
     * 사용자의 대기열 위치 및 예상 대기 시간 조회
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     * @return 대기열 정보 (위치 및 예상 대기 시간 포함)
     */
    WaitingQueue getWaitingQueueStatus(Long concertId, Long userId);
}