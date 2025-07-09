package concert.mania.concert.infrastructure.web.dto.response;

import concert.mania.concert.domain.model.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 예약 응답 DTO
 */
public record ReservationResponse(
    @Schema(description = "예약 ID", example = "1")
    Long id,

    @Schema(description = "예약 번호", example = "RES-20230101-001")
    String reservationNumber,

    @Schema(description = "사용자 ID", example = "1")
    Long userId,

    @Schema(description = "콘서트 ID", example = "1")
    Long concertId,

    @Schema(description = "콘서트 이름", example = "2023 여름 콘서트")
    String concertName,

    @Schema(description = "총 금액", example = "150000")
    BigDecimal totalAmount,

    @Schema(description = "예약 상태", example = "COMPLETED")
    String status,

    @Schema(description = "예약 완료 시간", example = "2023-01-01T12:00:00")
    LocalDateTime completedAt,

    @Schema(description = "예약 취소 시간", example = "2023-01-01T13:00:00")
    LocalDateTime cancelledAt,

    @Schema(description = "생성 시간", example = "2023-01-01T11:00:00")
    LocalDateTime createdAt,

    @Schema(description = "수정 시간", example = "2023-01-01T12:00:00")
    LocalDateTime updatedAt,

    @Schema(description = "총 좌석 수", example = "3")
    int totalSeats,

    @Schema(description = "좌석 ID 목록", example = "[1, 2, 3]")
    List<Long> seatIds
) {

    /**
     * 도메인 모델을 응답 DTO로 변환
     * @param reservation 예약 도메인 모델
     * @return 예약 응답 DTO
     */
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getUserId(),
                reservation.getConcert().getId(),
                reservation.getConcert().getTitle(),
                reservation.getTotalAmount(),
                reservation.getStatus() != null ? reservation.getStatus().name() : null,
                reservation.getCompletedAt(),
                reservation.getCancelledAt(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt(),
                reservation.getTotalSeats(),
                reservation.getReservationDetails().stream()
                        .map(detail -> detail.getSeat().getId())
                        .collect(Collectors.toList())
        );
    }

    /**
     * 도메인 모델 목록을 응답 DTO 목록으로 변환
     * @param reservations 예약 도메인 모델 목록
     * @return 예약 응답 DTO 목록
     */
    public static List<ReservationResponse> fromList(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }
}
