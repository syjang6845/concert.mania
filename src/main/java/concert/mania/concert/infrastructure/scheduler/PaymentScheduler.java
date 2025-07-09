package concert.mania.concert.infrastructure.scheduler;

import concert.mania.concert.application.port.in.PaymentUseCase;
import concert.mania.concert.application.port.out.query.PaymentQueryPort;
import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 결제 스케줄러
 * 결제 관련 주기적인 작업을 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {

    private final PaymentQueryPort paymentQueryPort;
    private final PaymentUseCase paymentUseCase;
    
    // 결제 만료 시간 (분)
    private static final int PAYMENT_EXPIRATION_MINUTES = 15;
    
    /**
     * 만료된 결제 처리
     * 1분마다 실행
     */
    @Scheduled(fixedRate = 60000)
    public void handleExpiredPayments() {
        log.info("만료된 결제 처리 스케줄러 실행");
        
        try {
            // 만료 시간 계산 (현재 시간 - 15분)
            LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(PAYMENT_EXPIRATION_MINUTES);
            
            // 만료된 PENDING 상태의 결제 조회
            List<Payment> expiredPayments = paymentQueryPort.findExpiredPayments(
                    PaymentStatus.PENDING, expirationTime);
            
            if (expiredPayments.isEmpty()) {
                log.info("만료된 결제 없음");
                return;
            }
            
            log.info("만료된 결제 발견 - 개수: {}", expiredPayments.size());
            
            // 각 만료된 결제 처리
            for (Payment payment : expiredPayments) {
                try {
                    // 결제 실패 처리 (좌석 자동 해제 포함)
                    paymentUseCase.handlePaymentFailure(payment.getId());
                    log.info("만료된 결제 실패 처리 완료 - 결제 ID: {}", payment.getId());
                } catch (Exception e) {
                    log.error("만료된 결제 처리 중 오류 발생 - 결제 ID: {}, 오류: {}", 
                            payment.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("만료된 결제 처리 스케줄러 실행 중 오류 발생: {}", e.getMessage());
        }
    }
    
    /**
     * 결제 상태 확인
     * 5분마다 실행
     */
    @Scheduled(fixedRate = 300000)
    public void checkPaymentStatus() {
        log.info("결제 상태 확인 스케줄러 실행");
        
        try {
            // 30분 이내에 생성된 PENDING 상태의 결제 조회
            LocalDateTime checkTime = LocalDateTime.now().minusMinutes(30);
            List<Payment> pendingPayments = paymentQueryPort.findPendingPayments(checkTime);
            
            if (pendingPayments.isEmpty()) {
                log.info("진행 중인 결제 없음");
                return;
            }
            
            log.info("진행 중인 결제 발견 - 개수: {}", pendingPayments.size());
            
            // 각 진행 중인 결제 상태 확인
            for (Payment payment : pendingPayments) {
                try {
                    paymentUseCase.checkPaymentStatus(payment.getId());
                    log.info("결제 상태 확인 완료 - 결제 ID: {}", payment.getId());
                } catch (Exception e) {
                    log.error("결제 상태 확인 중 오류 발생 - 결제 ID: {}, 오류: {}", 
                            payment.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("결제 상태 확인 스케줄러 실행 중 오류 발생: {}", e.getMessage());
        }
    }
}