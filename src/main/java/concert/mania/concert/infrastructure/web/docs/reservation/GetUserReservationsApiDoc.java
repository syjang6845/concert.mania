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
        summary = "사용자의 예약 목록 조회",
        description = """
        **사용자의 예약 목록을 조회**
        
        **처리 과정:**
        1. 사용자 ID로 예약 목록 조회
        2. 선택적으로 예약 상태로 필터링 가능
        3. 예약 목록 반환
        
        **파라미터:**
        - userId: 사용자 ID (필수)
        - status: 예약 상태 (선택, 필터링용)
        
        **예외 조건:**
        - 존재하지 않는 사용자 ID인 경우
        - 본인의 예약 목록만 조회 가능 (인증 필요)
        
        **반환 정보:**
        - 예약 목록 (각 예약의 ID, 예약 번호, 사용자 ID, 콘서트 정보, 좌석 정보, 예약 상태, 예약 일시 등)
        """,
        security = @SecurityRequirement(name = "Bearer")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "예약 목록 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReservationResponse.class),
                        examples = @ExampleObject(
                                name = "조회 성공",
                                value = """
                                {
                                    "message": "예약 목록 조회 성공",
                                    "statusCode": 200,
                                    "data": [
                                        {
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
                                        },
                                        {
                                            "id": 2,
                                            "reservationNumber": "RES-20250618-002",
                                            "userId": 100,
                                            "concertId": 2,
                                            "concertName": "가을 재즈 페스티벌",
                                            "seats": [
                                                {
                                                    "id": 50,
                                                    "seatNumber": "B10",
                                                    "seatGrade": {
                                                        "id": 2,
                                                        "name": "R",
                                                        "price": 120000
                                                    }
                                                }
                                            ],
                                            "status": "PENDING",
                                            "totalAmount": 120000,
                                            "createdAt": "2025-06-18T14:30:00",
                                            "updatedAt": "2025-06-18T14:30:00"
                                        }
                                    ]
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
                                        name = "사용자를 찾을 수 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "USER_NOT_FOUND",
                                            "message": "해당 ID의 사용자를 찾을 수 없습니다.",
                                            "path": "/api/v1/reservations/user/999"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "권한 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 403,
                                            "errorCode": "FORBIDDEN",
                                            "message": "본인의 예약 목록만 조회할 수 있습니다.",
                                            "path": "/api/v1/reservations/user/100"
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
                                            "path": "/api/v1/reservations/user/100"
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
                                    "path": "/api/v1/reservations/user/100"
                                }
                                """
                        )
                )
        )
})
public @interface GetUserReservationsApiDoc {
}