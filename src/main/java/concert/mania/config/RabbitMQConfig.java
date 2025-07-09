package concert.mania.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 설정
 */
@Configuration
public class RabbitMQConfig {

    // 대기열 관련 큐 및 익스체인지 이름
    public static final String WAITING_QUEUE_EXCHANGE = "waiting.queue.exchange";
    public static final String WAITING_QUEUE_REGISTER_QUEUE = "waiting.queue.register";
    public static final String WAITING_QUEUE_PROCESS_QUEUE = "waiting.queue.process";
    public static final String WAITING_QUEUE_ENTER_QUEUE = "waiting.queue.enter";

    // 결제 관련 큐 및 익스체인지 이름
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_PROCESS_QUEUE = "payment.process";
    public static final String PAYMENT_SUCCESS_QUEUE = "payment.success";
    public static final String PAYMENT_FAILURE_QUEUE = "payment.failure";
    public static final String PAYMENT_DETAILED_FAILURE_QUEUE = "payment.detailed.failure";
    public static final String PAYMENT_DETAILED_SUCCESS_QUEUE = "payment.detailed.success";

    /**
     * 메시지 컨버터 설정
     * @return JSON 메시지 컨버터
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 설정
     * @param connectionFactory RabbitMQ 연결 팩토리
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * 대기열 등록 큐 설정
     * @return 대기열 등록 큐
     */
    @Bean
    public Queue waitingQueueRegisterQueue() {
        return QueueBuilder.durable(WAITING_QUEUE_REGISTER_QUEUE)
                .build();
    }

    /**
     * 대기열 처리 큐 설정
     * @return 대기열 처리 큐
     */
    @Bean
    public Queue waitingQueueProcessQueue() {
        return QueueBuilder.durable(WAITING_QUEUE_PROCESS_QUEUE)
                .build();
    }

    /**
     * 대기열 입장 큐 설정
     * @return 대기열 입장 큐
     */
    @Bean
    public Queue waitingQueueEnterQueue() {
        return QueueBuilder.durable(WAITING_QUEUE_ENTER_QUEUE)
                .build();
    }

    /**
     * 대기열 익스체인지 설정
     * @return 대기열 익스체인지
     */
    @Bean
    public DirectExchange waitingQueueExchange() {
        return new DirectExchange(WAITING_QUEUE_EXCHANGE);
    }

    /**
     * 대기열 등록 큐와 익스체인지 바인딩
     * @param waitingQueueRegisterQueue 대기열 등록 큐
     * @param waitingQueueExchange 대기열 익스체인지
     * @return 바인딩
     */
    @Bean
    public Binding bindingWaitingQueueRegister(Queue waitingQueueRegisterQueue, DirectExchange waitingQueueExchange) {
        return BindingBuilder.bind(waitingQueueRegisterQueue)
                .to(waitingQueueExchange)
                .with("register");
    }

    /**
     * 대기열 처리 큐와 익스체인지 바인딩
     * @param waitingQueueProcessQueue 대기열 처리 큐
     * @param waitingQueueExchange 대기열 익스체인지
     * @return 바인딩
     */
    @Bean
    public Binding bindingWaitingQueueProcess(Queue waitingQueueProcessQueue, DirectExchange waitingQueueExchange) {
        return BindingBuilder.bind(waitingQueueProcessQueue)
                .to(waitingQueueExchange)
                .with("process");
    }

    /**
     * 대기열 입장 큐와 익스체인지 바인딩
     * @param waitingQueueEnterQueue 대기열 입장 큐
     * @param waitingQueueExchange 대기열 익스체인지
     * @return 바인딩
     */
    @Bean
    public Binding bindingWaitingQueueEnter(Queue waitingQueueEnterQueue, DirectExchange waitingQueueExchange) {
        return BindingBuilder.bind(waitingQueueEnterQueue)
                .to(waitingQueueExchange)
                .with("enter");
    }

    /**
     * 결제 처리 큐 설정
     * @return 결제 처리 큐
     */
    @Bean
    public Queue paymentProcessQueue() {
        return QueueBuilder.durable(PAYMENT_PROCESS_QUEUE)
                .build();
    }

    /**
     * 결제 성공 큐 설정
     * @return 결제 성공 큐
     */
    @Bean
    public Queue paymentSuccessQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_QUEUE)
                .build();
    }

    /**
     * 결제 실패 큐 설정
     * @return 결제 실패 큐
     */
    @Bean
    public Queue paymentFailureQueue() {
        return QueueBuilder.durable(PAYMENT_FAILURE_QUEUE)
                .build();
    }

    /**
     * 결제 익스체인지 설정
     * @return 결제 익스체인지
     */
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    /**
     * 결제 처리 큐와 익스체인지 바인딩
     * @param paymentProcessQueue 결제 처리 큐
     * @param paymentExchange 결제 익스체인지
     * @return 바인딩
     */
    @Bean
    public Binding bindingPaymentProcess(Queue paymentProcessQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentProcessQueue)
                .to(paymentExchange)
                .with("process");
    }

    /**
     * 결제 성공 큐와 익스체인지 바인딩
     * @param paymentSuccessQueue 결제 성공 큐
     * @param paymentExchange 결제 익스체인지
     * @return 바인딩
     */
    @Bean
    public Binding bindingPaymentSuccess(Queue paymentSuccessQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentSuccessQueue)
                .to(paymentExchange)
                .with("success");
    }

    /**
     * 결제 실패 큐와 익스체인지 바인딩
     * @param paymentFailureQueue 결제 실패 큐
     * @param paymentExchange 결제 익스체인지
     * @return 바인딩
     */
    @Bean
    public Binding bindingPaymentFailure(Queue paymentFailureQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentFailureQueue)
                .to(paymentExchange)
                .with("failure");
    }

    /**
     * 상세 결제 실패 큐 설정
     * @return 상세 결제 실패 큐
     */
    @Bean
    public Queue paymentDetailedFailureQueue() {
        return QueueBuilder.durable(PAYMENT_DETAILED_FAILURE_QUEUE)
                .build();
    }

    /**
     * 상세 결제 실패 큐와 익스체인지 바인딩
     * @param paymentDetailedFailureQueue 상세 결제 실패 큐
     * @param paymentExchange 결제 익스체인지
     * @return 바인딩
     */
    @Bean
    public Binding bindingPaymentDetailedFailure(Queue paymentDetailedFailureQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentDetailedFailureQueue)
                .to(paymentExchange)
                .with("detailed.failure");
    }

    /**
     * 상세 결제 성공 큐 설정
     * @return 상세 결제 성공 큐
     */
    @Bean
    public Queue paymentDetailedSuccessQueue() {
        return QueueBuilder.durable(PAYMENT_DETAILED_SUCCESS_QUEUE)
                .build();
    }

    /**
     * 상세 결제 성공 큐와 익스체인지 바인딩
     * @param paymentDetailedSuccessQueue 상세 결제 성공 큐
     * @param paymentExchange 결제 익스체인지
     * @return 바인딩
     */
    @Bean
    public Binding bindingPaymentDetailedSuccess(Queue paymentDetailedSuccessQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentDetailedSuccessQueue)
                .to(paymentExchange)
                .with("detailed.success");
    }
}
