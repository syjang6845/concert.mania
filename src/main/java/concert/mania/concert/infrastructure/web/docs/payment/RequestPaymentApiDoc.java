package concert.mania.concert.infrastructure.web.docs.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.response.PaymentResponse;
import concert.mania.concert.infrastructure.web.dto.request.PaymentRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "결제 요청",
        description = """
        **예약에 대한 결제를 요청**
        
        **처리 과정:**
        1. 예약 정보 및 결제 정보 검증
        2. 결제 정보 생성 및 PG사로 결제 요청 전달
        3. 결제 요청 정보 반환
        
        **파라미터:**
        - reservationId: 예약 ID (필수)
        - concertId: 콘서트 ID (필수)
        - seatId: 좌석 ID (필수)
        - seatLockId: 좌석 잠금 ID (필수)
        - amount: 결제 금액 (필수)
        - method: 결제 방식 (필수)
        - userId: 사용자 ID (필수)
        
        **예외 조건:**
        - 존재하지 않는 예약 ID인 경우
        - 존재하지 않는 좌석 ID인 경우
        - 좌석 잠금이 만료된 경우
        - 결제 금액이 좌석 가격과 일치하지 않는 경우
        - 이미 결제가 완료된 예약인 경우
        
        **반환 정보:**
        - 결제 ID, 예약 ID, 결제 상태, 결제 금액, 결제 방식, 외부 결제 ID 등
        """,
        security = @SecurityRequirement(name = "Bearer")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "결제 요청 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PaymentResponse.class),
                        examples = @ExampleObject(
                                name = "요청 성공",
                                value = """
                                {
                                    "message": "결제 요청이 접수되었습니다.",
                                    "statusCode": 200,
                                    "data": {
                                        "id": 1,
                                        "reservationId": 100,
                                        "status": "PENDING",
                                        "amount": 150000,
                                        "method": "CREDIT_CARD",
                                        "externalPaymentId": "pg_123456789",
                                        "createdAt": "2025-06-17T10:00:00",
                                        "updatedAt": "2025-06-17T10:00:00"
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
                                            "message": "해당 ID의 예약을 찾을 수 없습니다.",
                                            "path": "/api/v1/payments"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "좌석을 찾을 수 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "SEAT_NOT_FOUND",
                                            "message": "해당 ID의 좌석을 찾을 수 없습니다.",
                                            "path": "/api/v1/payments"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "좌석 잠금 만료",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "SEAT_LOCK_EXPIRED",
                                            "message": "좌석 잠금이 만료되었습니다.",
                                            "path": "/api/v1/payments"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "금액 불일치",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "AMOUNT_MISMATCH",
                                            "message": "결제 금액이 좌석 가격과 일치하지 않습니다.",
                                            "path": "/api/v1/payments"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "이미 결제 완료",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "ALREADY_PAID",
                                            "message": "이미 결제가 완료된 예약입니다.",
                                            "path": "/api/v1/payments"
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
                                            "path": "/api/v1/payments"
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
                                    "path": "/api/v1/payments"
                                }
                                """
                        )
                )
        )
})
public @interface RequestPaymentApiDoc {
}