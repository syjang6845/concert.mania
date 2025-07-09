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
import concert.mania.concert.infrastructure.web.dto.request.SeatSelectionRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "좌석 선택",
        description = """
        **좌석을 선택하여 10분 동안 임시 점유**
        
        **처리 과정:**
        1. 좌석 ID로 좌석 정보 조회
        2. 대기열 상태 확인 (입장 완료 상태인지 검증)
        3. 좌석 선택 처리 (10분 동안 임시 점유)
        4. 선택된 좌석 정보 반환
        
        **파라미터:**
        - seatId: 좌석 ID (필수)
        - userId: 사용자 ID (필수)
        
        **예외 조건:**
        - 존재하지 않는 좌석인 경우
        - 대기열에 등록되지 않은 사용자인 경우
        - 대기열 입장이 완료되지 않은 사용자인 경우
        - 이미 선택된 좌석인 경우
        
        **반환 정보:**
        - 선택된 좌석 정보 (ID, 좌석 번호, 좌석 등급, 상태 등)
        """,
        security = @SecurityRequirement(name = "Bearer")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "좌석 선택 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = SeatResponse.class),
                        examples = @ExampleObject(
                                name = "선택 성공",
                                value = """
                                {
                                    "message": "좌석 선택 성공",
                                    "statusCode": 200,
                                    "data": {
                                        "id": 1,
                                        "seatNumber": "A1",
                                        "seatGrade": {
                                            "id": 1,
                                            "name": "VIP",
                                            "price": 150000
                                        },
                                        "status": "LOCKED"
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
                                        name = "존재하지 않는 좌석",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "SEAT_NOT_FOUND",
                                            "message": "존재하지 않는 좌석입니다.",
                                            "path": "/api/v1/seats/999/select"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "대기열 미등록 사용자",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "USER_NOT_IN_WAITING_QUEUE",
                                            "message": "대기열에 등록되지 않은 사용자입니다. 대기열에 먼저 등록해주세요.",
                                            "path": "/api/v1/seats/1/select"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "대기열 입장 미완료",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "USER_NOT_ENTERED",
                                            "message": "대기열 입장이 완료되지 않은 사용자입니다.",
                                            "path": "/api/v1/seats/1/select"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "이미 선택된 좌석",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "SEAT_ALREADY_SELECTED",
                                            "message": "이미 선택된 좌석입니다.",
                                            "path": "/api/v1/seats/1/select"
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
                                            "path": "/api/v1/seats/1/select"
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
                                    "path": "/api/v1/seats/1/select"
                                }
                                """
                        )
                )
        )
})
public @interface SelectSeatApiDoc {
}