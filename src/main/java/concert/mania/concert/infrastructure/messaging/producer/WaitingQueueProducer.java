package concert.mania.concert.infrastructure.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static concert.mania.config.RabbitMQConfig.*;

/**
 * 대기열 메시지 생산자
 * RabbitMQ를 통해 대기열 관련 메시지를 전송
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 대기열 등록 메시지 전송
     * @param concertId 콘서트 ID
     * @param userId 사용자 ID
     */
    public void sendRegisterMessage(Long concertId, Long userId) {
        WaitingQueueMessage message = new WaitingQueueMessage(concertId, userId);
        log.info("대기열 등록 메시지 전송 - 콘서트 ID: {}, 사용자 ID: {}", concertId, userId);
        rabbitTemplate.convertAndSend(WAITING_QUEUE_EXCHANGE, "register", message);
    }

    /**
     * 대기열 처리 메시지 전송
     * @param concertId 콘서트 ID
     */
    public void sendProcessMessage(Long concertId) {
        WaitingQueueMessage message = new WaitingQueueMessage(concertId, null);
        log.info("대기열 처리 메시지 전송 - 콘서트 ID: {}", concertId);
        rabbitTemplate.convertAndSend(WAITING_QUEUE_EXCHANGE, "process", message);
    }

    /**
     * 대기열 입장 메시지 전송
     * @param waitingQueueId 대기열 ID
     */
    public void sendEnterMessage(Long waitingQueueId) {
        WaitingQueueMessage message = new WaitingQueueMessage(null, null, waitingQueueId);
        log.info("대기열 입장 메시지 전송 - 대기열 ID: {}", waitingQueueId);
        rabbitTemplate.convertAndSend(WAITING_QUEUE_EXCHANGE, "enter", message);
    }

    /**
     * 대기열 메시지 클래스
     */
    public static class WaitingQueueMessage {
        private Long concertId;
        private Long userId;
        private Long waitingQueueId;

        public WaitingQueueMessage() {
        }

        public WaitingQueueMessage(Long concertId, Long userId) {
            this.concertId = concertId;
            this.userId = userId;
        }

        public WaitingQueueMessage(Long concertId, Long userId, Long waitingQueueId) {
            this.concertId = concertId;
            this.userId = userId;
            this.waitingQueueId = waitingQueueId;
        }

        public Long getConcertId() {
            return concertId;
        }

        public void setConcertId(Long concertId) {
            this.concertId = concertId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getWaitingQueueId() {
            return waitingQueueId;
        }

        public void setWaitingQueueId(Long waitingQueueId) {
            this.waitingQueueId = waitingQueueId;
        }
    }
}
