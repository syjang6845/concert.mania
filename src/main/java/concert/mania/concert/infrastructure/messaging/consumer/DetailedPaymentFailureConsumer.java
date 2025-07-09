package concert.mania.concert.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import concert.mania.concert.application.port.in.PaymentUseCase;
import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.PaymentCancellationHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 상세 결제 실패 메시지 소비자
 * RabbitMQ로부터 상세 결제 실패 메시지를 수신하고 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DetailedPaymentFailureConsumer {

    private final PaymentUseCase paymentUseCase;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 상세 결제 실패 메시지 처리
     * @param message 상세 결제 실패 메시지 (JSON 문자열)
     */
    @RabbitListener(queues = "payment.detailed.failure")
    @Transactional
    public void handleDetailedFailureMessage(String message) {
        log.info("상세 결제 실패 메시지 수신: {}", message);

        try {
            // 메시지 파싱
            Map<String, Object> failureDetails = objectMapper.readValue(message, Map.class);

            Long paymentId = Long.valueOf(failureDetails.get("paymentId").toString());
            Long concertId = Long.valueOf(failureDetails.get("concertId").toString());
            Long seatId = Long.valueOf(failureDetails.get("seatId").toString());
            Long seatLockId = Long.valueOf(failureDetails.get("seatLockId").toString());

            // 결제 정보 조회
            Payment payment = paymentUseCase.checkPaymentStatus(paymentId);

            // 이미 실패 처리된 결제인지 확인
            if (!payment.isFailed()) {
                // 결제 실패 처리
                payment = paymentUseCase.handlePaymentFailure(paymentId);
            }

            // 취소 이력 저장 (직접 JDBC를 사용하여 저장)
            LocalDateTime now = LocalDateTime.now();
            jdbcTemplate.update(
                "INSERT INTO payment_cancellation_history (payment_id, concert_id, seat_id, seat_lock_id, reason, cancelled_at, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                paymentId, concertId, seatId, seatLockId, "결제 처리 실패 (5회 재시도 후)", now, now
            );

            log.info("결제 실패 처리 및 취소 이력 저장 완료 - 결제 ID: {}", paymentId);

        } catch (Exception e) {
            log.error("상세 결제 실패 메시지 처리 중 오류 발생: {}", e.getMessage());
        }
    }
}
