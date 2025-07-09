package concert.mania.concert.infrastructure.scheduler;

import concert.mania.concert.application.port.in.SeatUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 좌석 잠금 스케줄러
 * 만료된 좌석 잠금을 주기적으로 해제
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SeatLockScheduler {

    private final SeatUseCase seatUseCase;

    /**
     * 만료된 좌석 잠금 해제 작업
     * 30초마다 실행되어 만료된 좌석 잠금을 해제
     */
    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void unlockExpiredSeats() {
        log.info("만료된 좌석 잠금 해제 작업 시작");
        int count = seatUseCase.cancelExpiredSeatSelections();
        log.info("만료된 좌석 잠금 해제 작업 완료 - 해제된 좌석 수: {}", count);
    }
}
