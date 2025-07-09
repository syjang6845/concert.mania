package concert.mania.concert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 결제 취소 이력 도메인 모델
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancellationHistory {

    private Long id; // 취소 이력 고유 식별자
    private Long paymentId; // 결제 ID
    private Long concertId; // 콘서트 ID
    private Long seatId; // 좌석 ID
    private Long seatLockId; // 좌석 잠금 ID
    private String reason; // 취소 사유
    private LocalDateTime cancelledAt; // 취소 시간
    private LocalDateTime createdAt; // 생성 시간
}
