package concert.mania.concert.infrastructure.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import concert.mania.concert.application.port.out.payment.PaymentGatewayPort;
import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 모의 결제 게이트웨이 어댑터
 * PG사 연동을 시뮬레이션하는 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MockPaymentGatewayAdapter implements PaymentGatewayPort {

    private final ObjectMapper objectMapper;
    
    // 결제 정보 저장소 (실제로는 외부 PG사 시스템에 저장됨)
    private final ConcurrentHashMap<String, Map<String, Object>> paymentStore = new ConcurrentHashMap<>();
    
    // 결제 성공 확률 (90%)
    private static final double PAYMENT_SUCCESS_RATE = 0.9;
    
    /**
     * 결제 요청
     * @param payment 결제 정보
     * @return 외부 결제 시스템 ID
     */
    @Override
    public String requestPayment(Payment payment) {
        log.info("결제 요청 - 금액: {}, 결제 방식: {}", payment.getAmount(), payment.getMethod());
        
        // 외부 결제 시스템 ID 생성
        String externalPaymentId = generateExternalPaymentId();
        
        // 결제 성공 여부 결정 (90% 확률로 성공)
        boolean isSuccess = new Random().nextDouble() < PAYMENT_SUCCESS_RATE;
        
        // 결제 정보 저장
        Map<String, Object> paymentInfo = new HashMap<>();
        paymentInfo.put("paymentId", payment.getId());
        paymentInfo.put("amount", payment.getAmount());
        paymentInfo.put("method", payment.getMethod().name());
        paymentInfo.put("status", isSuccess ? PaymentStatus.COMPLETED.name() : PaymentStatus.FAILED.name());
        paymentInfo.put("timestamp", LocalDateTime.now().toString());
        paymentInfo.put("transactionId", UUID.randomUUID().toString());
        
        // 실패 시 오류 정보 추가
        if (!isSuccess) {
            Map<String, String> errorInfo = new HashMap<>();
            errorInfo.put("code", "PG_ERROR_" + (1000 + new Random().nextInt(9000)));
            errorInfo.put("message", "결제 처리 중 오류가 발생했습니다.");
            paymentInfo.put("error", errorInfo);
        }
        
        // 결제 정보 저장소에 저장
        paymentStore.put(externalPaymentId, paymentInfo);
        
        log.info("결제 요청 완료 - 외부 결제 ID: {}, 성공 여부: {}", externalPaymentId, isSuccess);
        
        return externalPaymentId;
    }

    /**
     * 결제 상태 조회
     * @param externalPaymentId 외부 결제 시스템 ID
     * @return 결제 상태 정보 (JSON 형태)
     */
    @Override
    public String getPaymentStatus(String externalPaymentId) {
        log.info("결제 상태 조회 - 외부 결제 ID: {}", externalPaymentId);
        
        Map<String, Object> paymentInfo = paymentStore.get(externalPaymentId);
        if (paymentInfo == null) {
            log.warn("결제 정보 없음 - 외부 결제 ID: {}", externalPaymentId);
            return createErrorJson("PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다.");
        }
        
        try {
            return objectMapper.writeValueAsString(paymentInfo);
        } catch (JsonProcessingException e) {
            log.error("결제 정보 JSON 변환 실패", e);
            return createErrorJson("JSON_PROCESSING_ERROR", "결제 정보 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 결제 취소 요청
     * @param payment 취소할 결제 정보
     * @return 취소 성공 여부
     */
    @Override
    public boolean cancelPayment(Payment payment) {
        log.info("결제 취소 요청 - 외부 결제 ID: {}", payment.getExternalPaymentId());
        
        String externalPaymentId = payment.getExternalPaymentId();
        if (externalPaymentId == null) {
            log.warn("외부 결제 ID 없음 - 결제 ID: {}", payment.getId());
            return false;
        }
        
        Map<String, Object> paymentInfo = paymentStore.get(externalPaymentId);
        if (paymentInfo == null) {
            log.warn("결제 정보 없음 - 외부 결제 ID: {}", externalPaymentId);
            return false;
        }
        
        // 이미 취소된 결제인지 확인
        if (PaymentStatus.CANCELLED.name().equals(paymentInfo.get("status"))) {
            log.warn("이미 취소된 결제 - 외부 결제 ID: {}", externalPaymentId);
            return false;
        }
        
        // 실패한 결제는 취소할 수 없음
        if (PaymentStatus.FAILED.name().equals(paymentInfo.get("status"))) {
            log.warn("실패한 결제는 취소할 수 없음 - 외부 결제 ID: {}", externalPaymentId);
            return false;
        }
        
        // 결제 상태 변경
        paymentInfo.put("status", PaymentStatus.CANCELLED.name());
        paymentInfo.put("cancelledAt", LocalDateTime.now().toString());
        
        log.info("결제 취소 완료 - 외부 결제 ID: {}", externalPaymentId);
        return true;
    }

    /**
     * 결제 검증
     * @param payment 검증할 결제 정보
     * @return 검증 성공 여부
     */
    @Override
    public boolean verifyPayment(Payment payment) {
        log.info("결제 검증 - 외부 결제 ID: {}", payment.getExternalPaymentId());
        
        String externalPaymentId = payment.getExternalPaymentId();
        if (externalPaymentId == null) {
            log.warn("외부 결제 ID 없음 - 결제 ID: {}", payment.getId());
            return false;
        }
        
        Map<String, Object> paymentInfo = paymentStore.get(externalPaymentId);
        if (paymentInfo == null) {
            log.warn("결제 정보 없음 - 외부 결제 ID: {}", externalPaymentId);
            return false;
        }
        
        // 결제 금액 검증
        if (!payment.getAmount().toString().equals(paymentInfo.get("amount").toString())) {
            log.warn("결제 금액 불일치 - 요청: {}, 실제: {}", payment.getAmount(), paymentInfo.get("amount"));
            return false;
        }
        
        // 결제 상태 검증
        boolean isCompleted = PaymentStatus.COMPLETED.name().equals(paymentInfo.get("status"));
        
        log.info("결제 검증 완료 - 외부 결제 ID: {}, 성공 여부: {}", externalPaymentId, isCompleted);
        return isCompleted;
    }
    
    /**
     * 외부 결제 시스템 ID 생성
     * @return 외부 결제 시스템 ID
     */
    private String generateExternalPaymentId() {
        return "PG_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
    
    /**
     * 오류 JSON 생성
     * @param code 오류 코드
     * @param message 오류 메시지
     * @return 오류 JSON
     */
    private String createErrorJson(String code, String message) {
        try {
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of(
                    "code", code,
                    "message", message
            ));
            return objectMapper.writeValueAsString(error);
        } catch (JsonProcessingException e) {
            log.error("오류 JSON 생성 실패", e);
            return "{\"error\":{\"code\":\"UNKNOWN_ERROR\",\"message\":\"알 수 없는 오류가 발생했습니다.\"}}";
        }
    }
}