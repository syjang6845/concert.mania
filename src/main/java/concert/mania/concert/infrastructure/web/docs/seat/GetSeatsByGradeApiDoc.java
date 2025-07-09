package concert.mania.concert.infrastructure.web.docs.seat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.response.SeatResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "좌석 등급별 좌석 목록 조회",
        description = """
        **특정 콘서트의 특정 좌석 등급 좌석들을 조회**
        
        **처리 과정:**
        1. 콘서트 ID와 좌석 등급 ID로 해당 콘서트의 특정 등급 좌석 정보 조회
        2. 좌석 목록 반환
        
        **파라미터:**
        - concertId: 콘서트 ID (필수)
        - seatGradeId: 좌석 등급 ID (필수)
        
        **반환 정보:**
        - 좌석 ID, 좌석 번호, 좌석 등급, 가격, 상태 등 정보
        """,
        security = @SecurityRequirement(name = "Bearer")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "좌석 등급별 좌석 목록 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = SeatResponse.class),
                        examples = @ExampleObject(
                                name = "조회 성공",
                                value = """
                                {
                                    "message": "좌석 등급별 좌석 목록 조회 성공",
                                    "statusCode": 200,
                                    "data": [
                                        {
                                            "id": 1,
                                            "seatNumber": "A1",
                                            "seatGrade": {
                                                "id": 1,
                                                "name": "VIP",
                                                "price": 150000
                                            },
                                            "status": "AVAILABLE"
                                        },
                                        {
                                            "id": 2,
                                            "seatNumber": "A2",
                                            "seatGrade": {
                                                "id": 1,
                                                "name": "VIP",
                                                "price": 150000
                                            },
                                            "status": "LOCKED"
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
                                        name = "콘서트를 찾을 수 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "CONCERT_NOT_FOUND",
                                            "message": "해당 ID의 콘서트를 찾을 수 없습니다.",
                                            "path": "/api/v1/concerts/999/seat-grades/1/seats"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "좌석 등급을 찾을 수 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "SEAT_GRADE_NOT_FOUND",
                                            "message": "해당 ID의 좌석 등급을 찾을 수 없습니다.",
                                            "path": "/api/v1/concerts/1/seat-grades/999/seats"
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
                                            "path": "/api/v1/concerts/1/seat-grades/1/seats"
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
                                    "path": "/api/v1/concerts/1/seat-grades/1/seats"
                                }
                                """
                        )
                )
        )
})
public @interface GetSeatsByGradeApiDoc {
}