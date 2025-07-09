package concert.mania.concert.infrastructure.web.dto.response;

import concert.mania.concert.domain.model.Seat;
import concert.mania.concert.domain.model.type.SeatStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 좌석 응답 DTO
 */
public record SeatResponse(
    Long seatId, // 좌석 ID
    String seatNumber, // 좌석 번호
    Integer seatRow, // 좌석 행 번호
    Integer seatCol, // 좌석 열 번호
    SeatStatus status, // 좌석 상태
    Long concertId, // 콘서트 ID
    String title, // 콘서트 제목
    Long seatGradeId, // 좌석 등급 ID
    String seatGradeName, // 좌석 등급명
    double price // 좌석 가격
) {

    /**
     * 도메인 모델을 응답 DTO로 변환
     * @param seat 좌석 도메인 모델
     * @return 좌석 응답 DTO
     */
    public static SeatResponse from(Seat seat) {
        if (seat == null) {
            return null;
        }

        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getSeatRow(),
                seat.getSeatCol(),
                seat.getStatus(),
                seat.getConcert().getId(),
                seat.getConcert().getTitle(),
                seat.getSeatGrade().getId(),
                seat.getSeatGrade().getName(),
                seat.getSeatGrade().getPrice().doubleValue()
        );
    }

    /**
     * 도메인 모델 목록을 응답 DTO 목록으로 변환
     * @param seats 좌석 도메인 모델 목록
     * @return 좌석 응답 DTO 목록
     */
    public static List<SeatResponse> fromList(List<Seat> seats) {
        if (seats == null) {
            return List.of();
        }

        return seats.stream()
                .map(SeatResponse::from)
                .collect(Collectors.toList());
    }
}
