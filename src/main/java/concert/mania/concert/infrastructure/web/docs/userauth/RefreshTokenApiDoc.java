
package concert.mania.concert.infrastructure.web.docs.userauth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import concert.mania.concert.infrastructure.web.dto.response.AccessTokenResponse;
import concert.mania.exception.model.ErrorResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "액세스 토큰 재발급",
        description = """
        Refresh Token을 사용한 Access Token 재발급 API

        **토큰 처리 방식:**
        - Refresh Token: HttpOnly 쿠키에서 자동 추출
        - Access Token: Response Body로 새로 발급 (유효기간: 30분)
        - Refresh Token: 새로운 HttpOnly 쿠키로 자동 갱신 (유효기간: 3일)

        **보안 특징:**
        - Refresh Token은 XSS 공격 방지를 위해 HttpOnly 쿠키로 처리
        - 토큰 로테이션으로 보안 강화 (기존 Refresh Token 무효화)

        **사용 시나리오:**
        1. Access Token 만료 시 자동으로 호출
        2. 브라우저가 자동으로 Refresh Token 쿠키 전송
        3. 새로운 Access Token을 Response Body에서 획득
        4. 새로운 Refresh Token이 쿠키로 자동 설정

        **토큰 만료 처리:**
        - Refresh Token 만료 시 401 반환 후 재로그인 필요
        - Access Token은 Authorization 헤더에 Bearer Token으로 설정

        **주의사항:**
        - 쿠키가 없거나 만료된 경우 로그인 페이지로 이동 필요
        """
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "토큰 재발급 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AccessTokenResponse.class),
                        examples = @ExampleObject(
                                value = """
                                {
                                    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTY5MDg3MDgwMH0.new_signature",
                                    "grantType": "Bearer",
                                    "user": {
                                        "id": 1,
                                        "email": "user@example.com",
                                        "name": "홍길동",
                                        "role": "ROLE_USER"
                                    }
                                }
                                """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패 - Refresh Token 없음 또는 만료",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "Refresh Token 없음",
                                        summary = "Refresh Token 쿠키가 없는 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "REFRESH_TOKEN_NOT_FOUND",
                                            "message": "Refresh Token을 찾을 수 없습니다.",
                                            "path": "/api/v1/users/authentication/tokens/refresh"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "Refresh Token 만료",
                                        summary = "Refresh Token이 만료된 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "REFRESH_TOKEN_EXPIRED",
                                            "message": "만료된 Refresh Token입니다. 다시 로그인해주세요.",
                                            "path": "/api/v1/users/authentication/tokens/refresh"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "유효하지 않은 Refresh Token",
                                        summary = "유효하지 않은 형식의 Refresh Token",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "INVALID_REFRESH_TOKEN",
                                            "message": "유효하지 않은 Refresh Token입니다.",
                                            "path": "/api/v1/users/authentication/tokens/refresh"
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
                                summary = "토큰 재발급 중 서버 오류 발생",
                                value = """
                                {
                                    "timestamp": "2025-06-17T10:00:00",
                                    "statusCode": 500,
                                    "errorCode": "INTERNAL_SERVER_ERROR",
                                    "message": "서버 에러가 발생했습니다.",
                                    "path": "/api/v1/users/authentication/tokens/refresh"
                                }
                                """
                        )
                )
        )
})
public @interface RefreshTokenApiDoc {
}
