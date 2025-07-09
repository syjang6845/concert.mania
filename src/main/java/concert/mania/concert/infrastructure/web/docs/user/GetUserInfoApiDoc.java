package concert.mania.concert.infrastructure.web.docs.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.response.UserProfileResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "사용자 정보 조회",
        description = """
        **이메일로 사용자 정보 조회**
        
        **처리 과정:**
        1. 이메일 주소로 사용자 정보 조회
        2. 현재 인증된 사용자 ID와 조회한 사용자 ID 일치 검증
        3. 본인의 정보만 조회 가능
        4. 사용자 프로필 정보 반환
        
        **보안 고려사항:**
        - JWT 토큰 인증 필수
        - 본인의 정보만 조회 가능 (ID 일치 검증)
        - 탈퇴하거나 정지된 회원 정보는 조회 불가
        
        **반환 정보:**
        - 사용자 ID, 이메일, 이름, 소속명, 가입일
        """,
        security = @SecurityRequirement(name = "bearerAuth")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "사용자 정보 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserProfileResponse.class),
                        examples = @ExampleObject(
                                name = "조회 성공",
                                value = """
                                {
                                    "id": 1,
                                    "email": "user@example.com",
                                    "name": "홍길동",
                                    "affiliationName": "테스트 회사",
                                    "createdAt": "2025-06-17T10:00:00"
                                }
                                """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 - 탈퇴한 회원 또는 정지된 회원",
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
                                                    "message": "해당 이메일로 가입된 사용자를 찾을 수 없습니다.",
                                                    "path": "/api/v1/users/user@example.com"
                                        }
                                        """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패 - 토큰이 없거나 유효하지 않음",
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
                                            "path": "/api/v1/users/user@example.com"
                                        }
                                        """
                                )
                        }
                )
        ),

        @ApiResponse(
                responseCode = "403",
                description = "권한 실패 - 본인의 정보만 조회 가능",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "사용자 ID 불일치",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 401,
                                            "errorCode": "UNAUTHORIZED", 
                                            "message": "본인의 정보만 조회할 수 있습니다.",
                                            "path": "/api/v1/users/user@example.com"
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
                                    "path": "/api/v1/users/user@example.com"
                                }
                                """
                        )
                )
        )
})
public @interface GetUserInfoApiDoc {
}