package concert.mania.concert.infrastructure.web.docs.userauth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.response.AccessTokenResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "사용자 로그인",
        description = """
        이메일과 비밀번호를 사용한 사용자 로그인 API

        **인증 방식:**
        - Access Token: Response Body에 반환 (유효기간: 30분)
        - Refresh Token: HttpOnly 쿠키로 자동 설정 (유효기간: 3일)

        **보안 특징:**
        - Refresh Token은 XSS 공격 방지를 위해 HttpOnly 쿠키로 처리
        - CSRF 방지를 위한 SameSite 속성 적용
        - 로그인 실패 시 계정 잠금 정책 적용 (추후 구현)

        **사용 방법:**
        1. 이메일과 비밀번호로 로그인 요청
        2. 성공 시 Access Token을 Response Body에서 획득
        3. Refresh Token은 브라우저가 자동으로 쿠키 저장
        4. API 호출 시 Authorization 헤더에 Bearer Token 설정

        **토큰 갱신:**
        - Access Token 만료 시 `/tokens/refresh` API 사용
        - Refresh Token은 자동으로 쿠키에서 읽어 처리
        """
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AccessTokenResponse.class),
                        examples = @ExampleObject(
                                value = """
                                {
                                    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTY5MDg2OTAwMH0.signature",
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
                responseCode = "400",
                description = "잘못된 요청 - 유효성 검증 실패",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "유효성 검증 실패",
                                        value = """
                                                {
                                                    "timestamp": "2025-06-17T10:00:00",
                                                    "statusCode": 400,
                                                    "errorCode": "BAD_REQUEST",
                                                    "message": "유효성 검사에 실패했습니다.",
                                                    "path": "/api/v1/users/authentication/login",
                                                    "fieldErrors": [
                                                        {
                                                            "field": "email",
                                                            "rejectedValue": "invalid-email",
                                                            "message": "이메일 형식이 올바르지 않습니다."
                                                        },
                                                        {
                                                            "field": "password",
                                                            "rejectedValue": "",
                                                            "message": "비밀번호는 필수입니다."
                                                        }
                                                    ]
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Role 불일치",
                                        summary = "Role 일치하지 않는 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "USER_ROLE_MISMATCH",
                                            "message": "일치하지 않는 정보입니다.",
                                            "path": "/api/v1/users/authentication/login"
                                        }
                                        """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패 - 이메일 또는 비밀번호 불일치",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "비밀번호 불일치",
                                        summary = "비밀번호가 일치하지 않는 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "PASSWORD_MISMATCH",
                                            "message": "비밀번호가 일치하지 않습니다.",
                                            "path": "/api/v1/users/authentication/login"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "사용자 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "LOGIN_USER_NOT_FOUND",
                                            "message": "해당 이메일로 가입된 사용자를 찾을 수 없습니다.",
                                            "path": "/api/v1/users/authentication/login"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "기타 인증 실패",
                                        summary = "기타 인증 관련 오류",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "AUTHENTICATION_FAILED",
                                            "message": "인증에 실패했습니다.",
                                            "path": "/api/v1/users/authentication/login"
                                        }
                                        """
                                )

                        }
                )
        )
})
public @interface LoginApiDoc {
}
