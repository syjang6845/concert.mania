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
        summary = "외부 결제 ID로 결제 정보 조회",
        description = """
        **외부 결제 시스템 ID로 결제 정보를 조회**
        
        **처리 과정:**
        1. 외부 결제 ID로 결제 정보 조회
        2. 결제 정보 반환
        
        **파라미터:**
        - externalPaymentId: 외부 결제 시스템 ID (필수)
        
        **예외 조건:**
        - 존재하지 않는 외부 결제 ID인 경우
        
        **반환 정보:**
        - 결제 ID, 예약 ID, 결제 상태, 결제 금액, 결제 방식, 외부 결제 ID 등
        """,
        security = @SecurityRequirement(name = "Bearer")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "결제 정보 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PaymentResponse.class),
                        examples = @ExampleObject(
                                name = "조회 성공",
                                value = """
                                {
                                    "message": "결제 정보 조회 성공",
                                    "statusCode": 200,
                                    "data": {
                                        "id": 1,
                                        "reservationId": 100,
                                        "status": "COMPLETED",
                                        "amount": 150000,
                                        "method": "CREDIT_CARD",
                                        "externalPaymentId": "pg_123456789",
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
                                        name = "결제를 찾을 수 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "PAYMENT_NOT_FOUND",
                                            "message": "해당 외부 결제 ID의 결제를 찾을 수 없습니다.",
                                            "path": "/api/v1/payments/external/invalid_id"
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
                                            "path": "/api/v1/payments/external/pg_123456789"
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
                                    "path": "/api/v1/payments/external/pg_123456789"
                                }
                                """
                        )
                )
        )
})
public @interface GetPaymentByExternalIdApiDoc {
}