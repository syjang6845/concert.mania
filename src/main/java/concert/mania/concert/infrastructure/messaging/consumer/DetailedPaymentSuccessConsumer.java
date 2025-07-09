package concert.mania.concert.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import concert.mania.concert.application.port.in.PaymentUseCase;
import concert.mania.concert.application.port.in.SeatUseCase;
import concert.mania.concert.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static concert.mania.config.RabbitMQConfig.PAYMENT_DETAILED_SUCCESS_QUEUE;

/**
 * 상세 결제 성공 메시지 소비자
 * RabbitMQ로부터 상세 결제 성공 메시지를 수신하고 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DetailedPaymentSuccessConsumer {

    private final PaymentUseCase paymentUseCase;
    private final SeatUseCase seatUseCase;
    private final ObjectMapper objectMapper;

    /**
     * 상세 결제 성공 메시지 처리
     * @param message 상세 결제 성공 메시지 (JSON 문자열)
     */
    @RabbitListener(queues = PAYMENT_DETAILED_SUCCESS_QUEUE)
    @Transactional
    public void handleDetailedSuccessMessage(String message) {
        log.info("상세 결제 성공 메시지 수신: {}", message);

        try {
            // 메시지 파싱
            Map<String, Object> successDetails = objectMapper.readValue(message, Map.class);

            Long paymentId = Long.valueOf(successDetails.get("paymentId").toString());
            Long concertId = Long.valueOf(successDetails.get("concertId").toString());
            Long seatId = Long.valueOf(successDetails.get("seatId").toString());
            Long seatLockId = Long.valueOf(successDetails.get("seatLockId").toString());

            // 결제 정보 조회
            Payment payment = paymentUseCase.checkPaymentStatus(paymentId);

            // 이미 성공 처리된 결제인지 확인
            if (!payment.isCompleted()) {
                // 결제 성공 처리
                payment = paymentUseCase.completePayment(paymentId);
            }

            // 좌석 확정 처리
            Long userId = payment.getReservation().getUserId();
            seatUseCase.confirmSeat(seatId, userId);

            log.info("결제 성공 처리 및 좌석 확정 완료 - 결제 ID: {}, 좌석 ID: {}", paymentId, seatId);

        } catch (Exception e) {
            log.error("상세 결제 성공 메시지 처리 중 오류 발생: {}", e.getMessage());
        }
    }
}
