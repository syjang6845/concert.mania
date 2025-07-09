package concert.mania.concert.application.port.in;

import concert.mania.concert.domain.model.Payment;
import concert.mania.concert.domain.model.type.PaymentMethod;

import java.math.BigDecimal;

/**
 * 결제 유스케이스 인터페이스
 * 결제 처리 기능을 정의
 */
public interface PaymentUseCase {

    /**
     * 결제 요청
     * @param reservationId 예약 ID
     * @param concertId 콘서트 ID
     * @param seatId 좌석 ID
     * @param seatLockId 좌석 잠금 ID
     * @param amount 결제 금액
     * @param method 결제 방식
     * @param userId 사용자 ID
     * @return 결제 정보
     */
    Payment requestPayment(Long reservationId, Long concertId, Long seatId, Long seatLockId, BigDecimal amount, PaymentMethod method, Long userId);

    /**
     * 결제 상태 확인
     * @param paymentId 결제 ID
     * @return 결제 정보
     */
    Payment checkPaymentStatus(Long paymentId);

    /**
     * 결제 취소
     * @param paymentId 결제 ID
     * @param userId 사용자 ID
     * @return 취소된 결제 정보
     */
    Payment cancelPayment(Long paymentId, Long userId);

    /**
     * 결제 실패 처리
     * @param paymentId 결제 ID
     * @return 실패 처리된 결제 정보
     */
    Payment handlePaymentFailure(Long paymentId);

    /**
     * 결제 완료 처리
     * @param paymentId 결제 ID
     * @return 완료 처리된 결제 정보
     */
    Payment completePayment(Long paymentId);

    /**
     * 외부 결제 ID로 결제 정보 조회
     * @param externalPaymentId 외부 결제 시스템 ID
     * @return 결제 정보
     */
    Payment getPaymentByExternalId(String externalPaymentId);
}
