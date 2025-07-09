package concert.mania.concert.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import concert.mania.concert.domain.model.Concert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘서트 응답 DTO
 */
public record ConcertResponse(
        Long concertId, // 콘서트 ID
        String title, // 콘서트 제목
        String description, // 콘서트 설명
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDateTime, // 콘서트 시작 일시
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDateTime, // 콘서트 종료 일시
        String venue, // 콘서트 장소
        String venueAddress, // 콘서트 장소 주소
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime reservationOpenDateTime, // 예매 오픈 일시
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime reservationCloseDateTime, // 예매 마감 일시
        boolean isActive, // 활성화 여부
        boolean isReservable, // 예매 가능 여부
        List<SeatGradeResponse> seatGrades // 좌석 등급 목록
) {

    /**
     * 도메인 모델을 응답 DTO로 변환
     * @param concert 콘서트 도메인 모델
     * @return 콘서트 응답 DTO
     */
    public static ConcertResponse from(Concert concert) {
        if (concert == null) {
            return null;
        }
        return new ConcertResponse(
                concert.getId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getStartDateTime(),
                concert.getEndDateTime(),
                concert.getVenue(),
                concert.getVenueAddress(),
                concert.getReservationOpenDateTime(),
                concert.getReservationCloseDateTime(),
                concert.isActive(),
                concert.isReservable(),
                concert.getSeatGrades().stream()
                        .map(SeatGradeResponse::from)
                        .collect(Collectors.toList())
        );
    }

    /**
     * 좌석 등급 응답 DTO
     */
    public record SeatGradeResponse(
            Long seatGradeId, // 좌석 등급 ID
            String name, // 좌석 등급명
            String description, // 좌석 등급 설명
            double price, // 좌석 등급 가격
            int capacity, // 좌석 등급 수용 인원
            int availableSeats // 예매 가능한 좌석 수
    ) {

        /**
         * 도메인 모델을 응답 DTO로 변환
         * @param seatGrade 좌석 등급 도메인 모델
         * @return 좌석 등급 응답 DTO
         */
        public static SeatGradeResponse from(concert.mania.concert.domain.model.SeatGrade seatGrade) {
            if (seatGrade == null) {
                return null;
            }

            return new SeatGradeResponse(
                    seatGrade.getId(),
                    seatGrade.getName(),
                    seatGrade.getDescription(),
                    seatGrade.getPrice().doubleValue(),
                    seatGrade.getCapacity(),
                    seatGrade.getRemainingSeats()
            );
        }
    }
}
