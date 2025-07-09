package concert.mania.concert.infrastructure.web.docs.userauth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import concert.mania.exception.model.ErrorResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "사용자 로그아웃",
        description = """
        사용자 로그아웃 API
        
        **로그아웃 처리 방식:**
        - 서버에서 토큰 버전 무효화 (Token Versioning)
        - Refresh Token 쿠키 삭제 (HttpOnly 쿠키 만료)
        - Redis에서 토큰 관련 데이터 삭제
        
        **보안 특징:**
        - 모든 기기에서 동시 로그아웃 처리
        - 토큰 탈취 시에도 안전하게 무효화
        - 쿠키 완전 삭제로 클라이언트 정리
        
        **처리 과정:**
        1. Refresh Token 쿠키에서 사용자 정보 추출
        2. 해당 사용자의 토큰 버전 증가 (기존 토큰 모두 무효화)
        3. Redis에서 관련 토큰 데이터 삭제
        4. Refresh Token 쿠키 삭제 (Max-Age=0)
        
        **클라이언트 처리:**
        - Access Token을 localStorage/sessionStorage에서 삭제
        - 로그인 페이지로 리다이렉트
        - API 호출 시 인증 헤더 제거
        
        **특이사항:**
        - Refresh Token이 없어도 200 응답 (중복 로그아웃 허용)
        - 이미 로그아웃된 상태여도 정상 처리
        - 클라이언트에서 안전하게 여러 번 호출 가능
        """
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "로그아웃 성공"
        ),
        @ApiResponse(
                responseCode = "401",
                description = "로그인하지 않은 사용자 (선택적 처리)",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "토큰 없음",
                                        summary = "Refresh Token이 없는 경우 (optional)",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "JWT_MISSING",
                                            "message": "JWT 토큰이 없습니다.",
                                            "path": "/api/v1/users/authentication/logout",
                                            "note": "실제로는 200 응답으로 처리될 수 있음"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "만료된 토큰",
                                        summary = "이미 만료된 Refresh Token인 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "JWT_EXPIRED",
                                            "message": "만료된 토큰입니다.",
                                            "path": "/api/v1/users/authentication/logout",
                                            "note": "만료된 토큰도 정상적으로 로그아웃 처리됨"
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
                                summary = "로그아웃 처리 중 서버 오류 발생",
                                value = """
                                {
                                    "timestamp": "2025-06-17T10:00:00",
                                    "statusCode": 500,
                                    "errorCode": "INTERNAL_SERVER_ERROR",
                                    "message": "서버 에러가 발생했습니다.",
                                    "path": "/api/v1/users/authentication/logout"
                                }
                                """
                        )
                )
        )
})
public @interface LogoutApiDoc {
}