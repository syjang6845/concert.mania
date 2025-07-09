package concert.mania.concert.application.port.out.payment;

import concert.mania.concert.domain.model.Payment;

/**
 * 외부 결제 게이트웨이 포트
 * PG사 연동을 위한 인터페이스
 */
public interface PaymentGatewayPort {

    /**
     * 결제 요청
     * @param payment 결제 정보
     * @return 외부 결제 시스템 ID
     */
    String requestPayment(Payment payment);

    /**
     * 결제 상태 조회
     * @param externalPaymentId 외부 결제 시스템 ID
     * @return 결제 상태 정보 (JSON 형태)
     */
    String getPaymentStatus(String externalPaymentId);

    /**
     * 결제 취소 요청
     * @param payment 취소할 결제 정보
     * @return 취소 성공 여부
     */
    boolean cancelPayment(Payment payment);

    /**
     * 결제 검증
     * @param payment 검증할 결제 정보
     * @return 검증 성공 여부
     */
    boolean verifyPayment(Payment payment);
}