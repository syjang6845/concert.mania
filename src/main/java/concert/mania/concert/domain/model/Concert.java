package concert.mania.concert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 콘서트 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Concert {
    
    private Long id; // 콘서트 고유 식별자
    private String title; // 콘서트 제목
    private String description; // 콘서트 상세 설명
    private LocalDateTime startDateTime; // 콘서트 시작 일시
    private LocalDateTime endDateTime; // 콘서트 종료 일시
    private String venue; // 콘서트 개최 장소명
    private String venueAddress; // 콘서트 개최 장소 주소
    private LocalDateTime reservationOpenDateTime; // 예매 오픈 일시
    private LocalDateTime reservationCloseDateTime; // 예매 마감 일시
    private boolean active; // 콘서트 활성화 상태 (true: 활성화, false: 비활성화)
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    // 관계
    private List<SeatGrade> seatGrades = new ArrayList<>(); // 좌석 등급 목록
    private List<Seat> seats = new ArrayList<>(); // 좌석 목록
    
    /**
     * 콘서트가 현재 예매 가능한지 확인
     * @return 예매 가능 여부
     */
    public boolean isReservable() {
        LocalDateTime now = LocalDateTime.now();
        return active &&
               now.isAfter(reservationOpenDateTime) && 
               now.isBefore(reservationCloseDateTime);
    }
    
    /**
     * 콘서트가 이미 시작되었는지 확인
     * @return 시작 여부
     */
    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startDateTime);
    }
    
    /**
     * 콘서트가 이미 종료되었는지 확인
     * @return 종료 여부
     */
    public boolean hasEnded() {
        return LocalDateTime.now().isAfter(endDateTime);
    }
}