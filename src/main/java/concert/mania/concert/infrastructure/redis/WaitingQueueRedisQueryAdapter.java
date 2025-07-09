package concert.mania.concert.infrastructure.redis;

import concert.mania.concert.application.port.out.query.WaitingQueueQueryPort;
import concert.mania.concert.domain.model.WaitingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 대기열 조회 Redis 어댑터
 * Redis를 사용하여 대기열 정보를 조회
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueRedisQueryAdapter implements WaitingQueueQueryPort {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis 키 접두사
    private static final String WAITING_QUEUE_KEY = "waiting:queue:";
    private static final String WAITING_QUEUE_USER_KEY = "waiting:user:";
    
    /**
     * 대기열 ID로 대기열 정보 조회
     */
    @Override
    public Optional<WaitingQueue> findById(Long id) {
        // ID 형식: concertId:position
        String[] parts = id.toString().split(":");
        if (parts.length != 2) {
            return Optional.empty();
        }
        
        String concertId = parts[0];
        String position = parts[1];
        String queueKey = WAITING_QUEUE_KEY + concertId;
        
        WaitingQueue waitingQueue = (WaitingQueue) redisTemplate.opsForHash().get(queueKey, position);
        return Optional.ofNullable(waitingQueue);
    }

    /**
     * 콘서트 ID와 사용자 ID로 대기열 정보 조회
     */
    @Override
    public Optional<WaitingQueue> findByConcertIdAndUserId(Long concertId, Long userId) {
        String userKey = WAITING_QUEUE_USER_KEY + concertId + ":" + userId;
        String queueKey = WAITING_QUEUE_KEY + concertId;
        
        // 사용자의 대기열 위치 조회
        Integer position = (Integer) redisTemplate.opsForValue().get(userKey);
        if (position == null) {
            return Optional.empty();
        }
        
        // 대기열 정보 조회
        WaitingQueue waitingQueue = (WaitingQueue) redisTemplate.opsForHash().get(queueKey, position.toString());
        return Optional.ofNullable(waitingQueue);
    }

    /**
     * 콘서트 ID로 모든 대기열 정보 조회
     */
    @Override
    public List<WaitingQueue> findAllByConcertId(Long concertId) {
        String queueKey = WAITING_QUEUE_KEY + concertId;
        
        // 모든 대기열 정보 조회
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(queueKey);
        
        return entries.values().stream()
                .map(value -> (WaitingQueue) value)
                .sorted(Comparator.comparing(WaitingQueue::getPosition))
                .collect(Collectors.toList());
    }

    /**
     * 콘서트 ID와 상태로 대기열 정보 조회
     */
    @Override
    public List<WaitingQueue> findAllByConcertIdAndStatus(Long concertId, WaitingQueue.WaitingStatus status) {
        String queueKey = WAITING_QUEUE_KEY + concertId;
        
        // 모든 대기열 정보 조회
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(queueKey);
        
        return entries.values().stream()
                .map(value -> (WaitingQueue) value)
                .filter(queue -> queue.getStatus() == status)
                .sorted(Comparator.comparing(WaitingQueue::getPosition))
                .collect(Collectors.toList());
    }

    /**
     * 콘서트의 대기열 총 인원 조회
     */
    @Override
    public int countByConcertId(Long concertId) {
        String queueKey = WAITING_QUEUE_KEY + concertId;
        
        // 대기열 크기 조회
        Long size = redisTemplate.opsForHash().size(queueKey);
        return size.intValue();
    }

    /**
     * 콘서트의 대기 상태별 인원 조회
     */
    @Override
    public int countByConcertIdAndStatus(Long concertId, WaitingQueue.WaitingStatus status) {
        String queueKey = WAITING_QUEUE_KEY + concertId;
        
        // 모든 대기열 정보 조회
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(queueKey);
        
        return (int) entries.values().stream()
                .map(value -> (WaitingQueue) value)
                .filter(queue -> queue.getStatus() == status)
                .count();
    }

    /**
     * 다음 처리할 대기열 항목 조회 (대기 중인 항목 중 가장 앞선 위치)
     */
    @Override
    public Optional<WaitingQueue> findNextWaiting(Long concertId) {
        String queueKey = WAITING_QUEUE_KEY + concertId;
        
        // 모든 대기열 정보 조회
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(queueKey);
        
        return entries.values().stream()
                .map(value -> (WaitingQueue) value)
                .filter(queue -> queue.getStatus() == WaitingQueue.WaitingStatus.WAITING)
                .min(Comparator.comparing(WaitingQueue::getPosition));
    }
}