package concert.mania.concert.application.port.out.command;

import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.type.PaymentStatus;

import java.time.LocalDateTime;

/**
 * 결제 명령(Command) 작업을 위한 포트 인터페이스
 */
public interface PaymentCommandPort {
    
    /**
     * 결제 정보 저장
     * 
     * @param payment 저장할 결제 정보
     * @return 저장된 결제 정보
     */
    Payment save(Payment payment);
    
    /**
     * 결제 정보 삭제
     * 
     * @param paymentId 삭제할 결제 ID
     */
    void delete(Long paymentId);
    
    /**
     * 결제 완료 처리
     * 
     * @param paymentId 결제 ID
     * @param completedAt 완료 시간
     * @return 업데이트된 결제 정보
     */
    Payment complete(Long paymentId, LocalDateTime completedAt);
    
    /**
     * 결제 실패 처리
     * 
     * @param paymentId 결제 ID
     * @return 업데이트된 결제 정보
     */
    Payment fail(Long paymentId);
    
    /**
     * 결제 취소 처리
     * 
     * @param paymentId 결제 ID
     * @param cancelledAt 취소 시간
     * @return 업데이트된 결제 정보
     */
    Payment cancel(Long paymentId, LocalDateTime cancelledAt);
    
    /**
     * 결제 상태 변경
     * 
     * @param paymentId 결제 ID
     * @param status 변경할 상태
     * @return 업데이트된 결제 정보
     */
    Payment updateStatus(Long paymentId, PaymentStatus status);
    
    /**
     * 외부 결제 ID 업데이트
     * 
     * @param paymentId 결제 ID
     * @param externalPaymentId 외부 결제 시스템 ID
     * @return 업데이트된 결제 정보
     */
    Payment updateExternalPaymentId(Long paymentId, String externalPaymentId);
    
    /**
     * 결제 상세 정보 업데이트
     * 
     * @param paymentId 결제 ID
     * @param paymentDetails 결제 상세 정보 (JSON 형태)
     * @return 업데이트된 결제 정보
     */
    Payment updatePaymentDetails(Long paymentId, String paymentDetails);
}