package concert.mania.concert.infrastructure.messaging.consumer;

import concert.mania.concert.application.port.in.WaitingQueueCommandUseCase;
import concert.mania.concert.domain.model.WaitingQueue;
import concert.mania.concert.infrastructure.messaging.producer.WaitingQueueProducer.WaitingQueueMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static concert.mania.config.RabbitMQConfig.*;

/**
 * 대기열 메시지 소비자
 * RabbitMQ로부터 대기열 관련 메시지를 수신하고 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueConsumer {

    private final WaitingQueueCommandUseCase waitingQueueCommandUseCase;

    /**
     * 대기열 등록 메시지 처리
     * @param message 대기열 메시지
     */
    @RabbitListener(queues = WAITING_QUEUE_REGISTER_QUEUE)
    public void handleRegisterMessage(WaitingQueueMessage message) {
        log.info("대기열 등록 메시지 수신 - 콘서트 ID: {}, 사용자 ID: {}", message.getConcertId(), message.getUserId());

        try {
            WaitingQueue waitingQueue = waitingQueueCommandUseCase.registerToWaitingQueue(
                    message.getConcertId(), message.getUserId());

            log.info("대기열 등록 완료 - 콘서트 ID: {}, 사용자 ID: {}, 대기 위치: {}", 
                    message.getConcertId(), message.getUserId(), waitingQueue.getPosition());
        } catch (Exception e) {
            log.error("대기열 등록 실패 - 콘서트 ID: {}, 사용자 ID: {}, 오류: {}", 
                    message.getConcertId(), message.getUserId(), e.getMessage());
        }
    }

    /**
     * 대기열 처리 메시지 처리
     * @param message 대기열 메시지
     */
    @RabbitListener(queues = WAITING_QUEUE_PROCESS_QUEUE)
    public void handleProcessMessage(WaitingQueueMessage message) {
        log.info("대기열 처리 메시지 수신 - 콘서트 ID: {}", message.getConcertId());

        try {
            WaitingQueue waitingQueue = waitingQueueCommandUseCase.processNextWaiting(message.getConcertId());

            log.info("대기열 처리 완료 - 콘서트 ID: {}, 사용자 ID: {}, 대기 위치: {}", 
                    message.getConcertId(), waitingQueue.getUserId(), waitingQueue.getPosition());
        } catch (Exception e) {
            log.error("대기열 처리 실패 - 콘서트 ID: {}, 오류: {}", 
                    message.getConcertId(), e.getMessage());
        }
    }

    /**
     * 대기열 입장 메시지 처리
     * @param message 대기열 메시지
     */
    @RabbitListener(queues = WAITING_QUEUE_ENTER_QUEUE)
    public void handleEnterMessage(WaitingQueueMessage message) {
        log.info("대기열 입장 메시지 수신 - 대기열 ID: {}", message.getWaitingQueueId());

        try {
            WaitingQueue waitingQueue = waitingQueueCommandUseCase.enterWaitingQueue(message.getWaitingQueueId());

            log.info("대기열 입장 완료 - 대기열 ID: {}, 콘서트 ID: {}, 사용자 ID: {}", 
                    message.getWaitingQueueId(), waitingQueue.getConcertId(), waitingQueue.getUserId());
        } catch (Exception e) {
            log.error("대기열 입장 실패 - 대기열 ID: {}, 오류: {}", 
                    message.getWaitingQueueId(), e.getMessage());
        }
    }
}
