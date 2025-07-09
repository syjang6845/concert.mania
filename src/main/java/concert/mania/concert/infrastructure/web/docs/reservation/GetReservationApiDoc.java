package concert.mania.concert.infrastructure.web.docs.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.response.ReservationResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "예약 조회",
        description = """
        **예약 ID로 예약 정보를 조회**
        
        **처리 과정:**
        1. 예약 ID로 예약 정보 조회
        2. 사용자 ID 검증 (본인의 예약만 조회 가능)
        3. 예약 정보 반환
        
        **파라미터:**
        - reservationId: 예약 ID (필수)
        - userId: 사용자 ID (필수)
        
        **예외 조건:**
        - 존재하지 않는 예약 ID인 경우
        - 본인의 예약이 아닌 경우 (사용자 ID 불일치)
        
        **반환 정보:**
        - 예약 ID, 예약 번호, 사용자 ID, 콘서트 정보, 좌석 정보, 예약 상태, 예약 일시 등
        """,
        security = @SecurityRequirement(name = "Bearer")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "예약 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReservationResponse.class),
                        examples = @ExampleObject(
                                name = "조회 성공",
                                value = """
                                {
                                    "message": "예약 조회 성공",
                                    "statusCode": 200,
                                    "data": {
                                        "id": 1,
                                        "reservationNumber": "RES-20250617-001",
                                        "userId": 100,
                                        "concertId": 1,
                                        "concertName": "2025 여름 콘서트",
                                        "seats": [
                                            {
                                                "id": 1,
                                                "seatNumber": "A1",
                                                "seatGrade": {
                                                    "id": 1,
                                                    "name": "VIP",
                                                    "price": 150000
                                                }
                                            }
                                        ],
                                        "status": "COMPLETED",
                                        "totalAmount": 150000,
                                        "createdAt": "2025-06-17T10:00:00",
                                        "updatedAt": "2025-06-17T10:05:00"
                                    }
                                }
                                """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "예약을 찾을 수 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "RESERVATION_NOT_FOUND",
                                            "message": "존재하지 않는 예약입니다.",
                                            "path": "/api/v1/reservations/1"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "사용자 불일치",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "USER_MISMATCH",
                                            "message": "예약한 사용자만 조회할 수 있습니다.",
                                            "path": "/api/v1/reservations/1"
                                        }
                                        """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "토큰 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "UNAUTHORIZED",
                                            "message": "인증이 필요합니다.",
                                            "path": "/api/v1/reservations/1"
                                        }
                                        """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "서버 내부 오류",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "서버 오류",
                                value = """
                                {
                                    "timestamp": "2025-06-17T10:00:00",
                                    "statusCode": 500,
                                    "errorCode": "INTERNAL_SERVER_ERROR",
                                    "message": "서버에서 오류가 발생했습니다.",
                                    "path": "/api/v1/reservations/1"
                                }
                                """
                        )
                )
        )
})
public @interface GetReservationApiDoc {
}