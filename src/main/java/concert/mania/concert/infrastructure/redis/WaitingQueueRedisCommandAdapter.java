package concert.mania.concert.infrastructure.redis;

import concert.mania.concert.application.port.out.command.WaitingQueueCommandPort;
import concert.mania.concert.domain.model.WaitingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 대기열 명령 Redis 어댑터
 * Redis를 사용하여 대기열 명령을 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueRedisCommandAdapter implements WaitingQueueCommandPort {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis 키 접두사
    private static final String WAITING_QUEUE_KEY = "waiting:queue:";
    private static final String WAITING_QUEUE_COUNT_KEY = "waiting:count:";
    private static final String WAITING_QUEUE_USER_KEY = "waiting:user:";
    
    /**
     * 대기열에 사용자 등록
     */
    @Override
    public WaitingQueue register(Long concertId, Long userId) {
        String countKey = WAITING_QUEUE_COUNT_KEY + concertId;
        String queueKey = WAITING_QUEUE_KEY + concertId;
        String userKey = WAITING_QUEUE_USER_KEY + concertId + ":" + userId;
        
        // 대기열 위치 계산 (현재 카운트 + 1)
        Long position = redisTemplate.opsForValue().increment(countKey);
        
        // 대기열 정보 생성
        WaitingQueue waitingQueue = WaitingQueue.register(concertId, userId, position.intValue());
        
        // Redis에 저장
        redisTemplate.opsForHash().put(queueKey, position.toString(), waitingQueue);
        redisTemplate.opsForValue().set(userKey, position);
        
        // 24시간 후 만료 설정
        redisTemplate.expire(userKey, 24, TimeUnit.HOURS);
        
        return waitingQueue;
    }

    /**
     * 대기열 상태를 처리 중으로 변경
     */
    @Override
    public WaitingQueue process(Long waitingQueueId) {
        String queueKey = WAITING_QUEUE_KEY + waitingQueueId.toString().split(":")[0]; // concertId 추출
        
        // 대기열 정보 조회
        WaitingQueue waitingQueue = (WaitingQueue) redisTemplate.opsForHash().get(queueKey, waitingQueueId.toString());
        
        if (waitingQueue == null) {
            throw new IllegalArgumentException("존재하지 않는 대기열 항목입니다.");
        }
        
        // 상태 변경
        WaitingQueue updated = waitingQueue.process();
        
        // Redis에 업데이트
        redisTemplate.opsForHash().put(queueKey, waitingQueueId.toString(), updated);
        
        return updated;
    }

    /**
     * 대기열 상태를 입장 완료로 변경
     */
    @Override
    public WaitingQueue enter(Long waitingQueueId) {
        String queueKey = WAITING_QUEUE_KEY + waitingQueueId.toString().split(":")[0]; // concertId 추출
        
        // 대기열 정보 조회
        WaitingQueue waitingQueue = (WaitingQueue) redisTemplate.opsForHash().get(queueKey, waitingQueueId.toString());
        
        if (waitingQueue == null) {
            throw new IllegalArgumentException("존재하지 않는 대기열 항목입니다.");
        }
        
        // 상태 변경
        WaitingQueue updated = waitingQueue.enter();
        
        // Redis에 업데이트
        redisTemplate.opsForHash().put(queueKey, waitingQueueId.toString(), updated);
        
        return updated;
    }

    /**
     * 대기열 상태를 만료로 변경
     */
    @Override
    public WaitingQueue expire(Long waitingQueueId) {
        String queueKey = WAITING_QUEUE_KEY + waitingQueueId.toString().split(":")[0]; // concertId 추출
        
        // 대기열 정보 조회
        WaitingQueue waitingQueue = (WaitingQueue) redisTemplate.opsForHash().get(queueKey, waitingQueueId.toString());
        
        if (waitingQueue == null) {
            throw new IllegalArgumentException("존재하지 않는 대기열 항목입니다.");
        }
        
        // 상태 변경
        WaitingQueue updated = waitingQueue.expire();
        
        // Redis에 업데이트
        redisTemplate.opsForHash().put(queueKey, waitingQueueId.toString(), updated);
        
        return updated;
    }

    /**
     * 대기열에서 사용자 제거
     */
    @Override
    public void remove(Long waitingQueueId) {
        String queueKey = WAITING_QUEUE_KEY + waitingQueueId.toString().split(":")[0]; // concertId 추출
        
        // 대기열 정보 조회
        WaitingQueue waitingQueue = (WaitingQueue) redisTemplate.opsForHash().get(queueKey, waitingQueueId.toString());
        
        if (waitingQueue == null) {
            throw new IllegalArgumentException("존재하지 않는 대기열 항목입니다.");
        }
        
        // 사용자 키 삭제
        String userKey = WAITING_QUEUE_USER_KEY + waitingQueue.getConcertId() + ":" + waitingQueue.getUserId();
        redisTemplate.delete(userKey);
        
        // 대기열에서 삭제
        redisTemplate.opsForHash().delete(queueKey, waitingQueueId.toString());
    }

    /**
     * 콘서트의 모든 대기열 초기화
     */
    @Override
    public int resetByConcertId(Long concertId) {
        String queueKey = WAITING_QUEUE_KEY + concertId;
        String countKey = WAITING_QUEUE_COUNT_KEY + concertId;
        
        // 대기열 정보 조회
        Long size = redisTemplate.opsForHash().size(queueKey);
        
        // 대기열 삭제
        redisTemplate.delete(queueKey);
        redisTemplate.delete(countKey);
        
        // 사용자 키 패턴으로 삭제
        String userKeyPattern = WAITING_QUEUE_USER_KEY + concertId + ":*";
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(userKeyPattern)));
        
        return size.intValue();
    }
}