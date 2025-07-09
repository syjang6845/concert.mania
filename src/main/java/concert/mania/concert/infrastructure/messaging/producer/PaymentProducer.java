package concert.mania.concert.infrastructure.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static concert.mania.config.RabbitMQConfig.*;

/**
 * 결제 메시지 생산자
 * RabbitMQ를 통해 결제 관련 메시지를 전송
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 결제 처리 메시지 전송
     * @param paymentId 결제 ID
     */
    public void sendProcessMessage(Long paymentId) {
        PaymentMessage message = new PaymentMessage(paymentId);
        log.info("결제 처리 메시지 전송 - 결제 ID: {}", paymentId);
        rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, "process", message);
    }

    /**
     * 결제 성공 메시지 전송
     * @param paymentId 결제 ID
     */
    public void sendSuccessMessage(Long paymentId) {
        PaymentMessage message = new PaymentMessage(paymentId);
        log.info("결제 성공 메시지 전송 - 결제 ID: {}", paymentId);
        rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, "success", message);
    }

    /**
     * 결제 실패 메시지 전송
     * @param paymentId 결제 ID
     */
    public void sendFailureMessage(Long paymentId) {
        PaymentMessage message = new PaymentMessage(paymentId);
        log.info("결제 실패 메시지 전송 - 결제 ID: {}", paymentId);
        rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, "failure", message);
    }

    /**
     * 상세 결제 실패 메시지 전송
     * @param message 상세 실패 메시지 (JSON 문자열)
     */
    public void sendDetailedFailureMessage(String message) {
        log.info("상세 결제 실패 메시지 전송: {}", message);
        rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, "detailed.failure", message);
    }

    /**
     * 상세 결제 성공 메시지 전송
     * @param message 상세 성공 메시지 (JSON 문자열)
     */
    public void sendDetailedSuccessMessage(String message) {
        log.info("상세 결제 성공 메시지 전송: {}", message);
        rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, "detailed.success", message);
    }

    /**
     * 결제 메시지 클래스
     */
    public static class PaymentMessage {
        private Long paymentId;

        public PaymentMessage() {
        }

        public PaymentMessage(Long paymentId) {
            this.paymentId = paymentId;
        }

        public Long getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
        }
    }
}
