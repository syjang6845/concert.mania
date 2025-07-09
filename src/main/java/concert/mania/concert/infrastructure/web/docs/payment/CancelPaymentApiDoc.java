package concert.mania.concert.infrastructure.web.docs.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.response.PaymentResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "결제 취소",
        description = """
        **결제를 취소**
        
        **처리 과정:**
        1. 결제 ID로 결제 정보 조회
        2. 사용자 ID 검증 (본인 결제만 취소 가능)
        3. 결제 취소 처리
        4. 취소된 결제 정보 반환
        
        **파라미터:**
        - paymentId: 결제 ID (필수)
        - userId: 사용자 ID (필수)
        
        **예외 조건:**
        - 존재하지 않는 결제 ID인 경우
        - 본인이 요청한 결제가 아닌 경우
        - 이미 취소된 결제인 경우
        - 결제 완료 후 일정 시간이 지난 결제인 경우
        
        **반환 정보:**
        - 결제 ID, 예약 ID, 결제 상태, 결제 금액, 결제 방식, 외부 결제 ID 등
        """,
        security = @SecurityRequirement(name = "Bearer")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "결제 취소 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PaymentResponse.class),
                        examples = @ExampleObject(
                                name = "취소 성공",
                                value = """
                                {
                                    "message": "결제 취소 성공",
                                    "statusCode": 200,
                                    "data": {
                                        "id": 1,
                                        "reservationId": 100,
                                        "status": "CANCELLED",
                                        "amount": 150000,
                                        "method": "CREDIT_CARD",
                                        "externalPaymentId": "pg_123456789",
                                        "createdAt": "2025-06-17T10:00:00",
                                        "updatedAt": "2025-06-17T11:00:00"
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
                                        name = "결제를 찾을 수 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "PAYMENT_NOT_FOUND",
                                            "message": "해당 ID의 결제를 찾을 수 없습니다.",
                                            "path": "/api/v1/payments/999/cancel"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "본인 결제 아님",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "PAYMENT_NOT_OWNED",
                                            "message": "본인이 요청한 결제만 취소할 수 있습니다.",
                                            "path": "/api/v1/payments/1/cancel"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "이미 취소됨",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "PAYMENT_ALREADY_CANCELLED",
                                            "message": "이미 취소된 결제입니다.",
                                            "path": "/api/v1/payments/1/cancel"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "취소 기간 초과",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "PAYMENT_CANCEL_PERIOD_EXPIRED",
                                            "message": "결제 취소 가능 기간이 지났습니다.",
                                            "path": "/api/v1/payments/1/cancel"
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
                                            "path": "/api/v1/payments/1/cancel"
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
                                    "path": "/api/v1/payments/1/cancel"
                                }
                                """
                        )
                )
        )
})
public @interface CancelPaymentApiDoc {
}