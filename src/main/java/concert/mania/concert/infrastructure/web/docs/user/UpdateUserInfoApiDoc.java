
package concert.mania.concert.infrastructure.web.docs.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
        summary = "사용자 정보 수정 (선생님 전용)",
        description = """
        **사용자 정보 수정 API**
        
        **처리 과정:**
        1. JWT 토큰으로 인증 확인
        2. 사용자 권한이 선생님(TEACHER)인지 검증
        3. 대상 사용자 존재 여부 확인
        4. 소속명(affiliationName) 정보 수정
        5. 수정된 사용자 정보 반환
        
        **보안 고려사항:**
        - JWT 토큰 인증 필수
        - 선생님 권한만 접근 가능
        - 유효한 사용자 ID 필요
        
        **수정 가능한 정보:**
        - 소속명 (affiliationName)
        """,
        security = @SecurityRequirement(name = "bearerAuth")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "사용자 정보 수정 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserProfileResponse.class),
                        examples = @ExampleObject(
                                name = "수정 성공",
                                value = """
                                {
                                    "id": 1,
                                    "email": "user@example.com",
                                    "name": "홍길동",
                                    "affiliationName": "새로운 소속명",
                                    "createdAt": "2025-06-17T10:00:00"
                                }
                                """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 - 유효하지 않은 입력 데이터",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "유효성 검사 실패",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "INVALID_INPUT",
                                            "message": "소속명은 필수입니다.",
                                            "path": "/api/v1/users/1"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "사용자 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "USER_NOT_FOUND",
                                            "message": "해당 ID의 사용자를 찾을 수 없습니다.",
                                            "path": "/api/v1/users/1"
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
                                            "path": "/api/v1/users/1"
                                        }
                                        """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "권한 실패 - 선생님만 접근 가능",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "권한 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 403,
                                            "errorCode": "FORBIDDEN",
                                            "message": "선생님만 사용자 정보를 수정할 수 있습니다.",
                                            "path": "/api/v1/users/1"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "사용자 ID 불일치",
                                        summary = "다른 사용자의 비밀번호 변경 시도",
                                        value = """
                                {
                                    "timestamp": "2025-06-17T10:00:00",
                                    "statusCode": 403,
                                    "errorCode": "USER_ACCESS_DENIED",
                                    "message": "본인의 정보만 수정할 수 있습니다.",
                                    "path": "/api/v1/users/password/change/1"
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
                                    "path": "/api/v1/users/1"
                                }
                                """
                        )
                )
        )
})
@Parameter(
        name = "userId",
        description = "수정할 사용자의 고유 ID",
        required = true,
        in = ParameterIn.PATH,
        schema = @Schema(type = "integer", format = "int64", minimum = "1")
)
public @interface UpdateUserInfoApiDoc {
}