
package concert.mania.concert.infrastructure.web.docs.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.request.DeleteUserRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "사용자 회원탈퇴",
        description = """
        사용자 회원탈퇴 API
        
        **탈퇴 처리 방식:**
        - 즉시 탈퇴 처리 (Soft Delete)
        - 자동 로그아웃 처리
        - Refresh Token 쿠키 삭제 (@ClearRefreshToken)
        
        **탈퇴 시 처리 내용:**
        1. 현재 비밀번호 검증 (보안 확인)
        2. 탈퇴 이력 저장 (WithdrawHistory)
        3. 사용자 계정 삭제 처리
        4. 관련 데이터 정리 (프로필 이미지 등)
        5. 자동 로그아웃 및 토큰 무효화
        
        **보안 검증:**
        - 현재 비밀번호 확인 필수
        - 본인 확인 후 탈퇴 처리
        - 게이트웨이에서 사용자 인증 완료
        
        **헤더 정보 (게이트웨이에서 자동 주입):**
        - X-Request-user-id: 사용자 ID
        - X-Request-user-authority: 사용자 권한 정보
        
        **Request Body:**
        - password: 현재 비밀번호 (필수)
        - reason: 탈퇴 사유 (선택)
        
        **주의사항:**
        - 탈퇴 후 즉시 로그아웃 처리
        - 탈퇴 처리 후 복구 불가
        - 관련 데이터는 개인정보보호법에 따라 처리
        - 이미 탈퇴한 계정은 재탈퇴 불가
        """
)
@RequestBody(
        description = "회원탈퇴 요청 정보",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DeleteUserRequest.class),
                examples = {
                        @ExampleObject(
                                name = "기본 탈퇴",
                                summary = "탈퇴 사유 없는 기본 탈퇴",
                                value = """
                                {
                                    "password": "currentPassword123!",
                                    "reason": null
                                }
                                """
                        ),
                        @ExampleObject(
                                name = "사유와 함께 탈퇴",
                                summary = "탈퇴 사유를 포함한 탈퇴",
                                value = """
                                {
                                    "password": "currentPassword123!",
                                    "reason": "서비스 이용 빈도가 낮아서"
                                }
                                """
                        )
                }
        )
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "회원탈퇴 성공",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                name = "탈퇴 성공",
                                summary = "정상적으로 회원탈퇴 처리된 경우",
                                value = """
                                {
                                    "message": "회원탈퇴가 완료되었습니다.",
                                    "timestamp": "2025-06-17T10:00:00"
                                }
                                """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 입력",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "비밀번호 없음",
                                        summary = "비밀번호가 입력되지 않은 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "BAD_REQUEST",
                                            "message": "Password is required",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "비밀번호 불일치",
                                        summary = "입력한 비밀번호가 틀린 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "INVALID_PASSWORD",
                                            "message": "유효하지 않은 비밀번호입니다.",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "사용자 없음",
                                        summary = "존재하지 않는 사용자 ID",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "USER_NOT_FOUND",
                                            "message": "사용자를 찾을 수 없습니다.",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                )
                        }
                )
        )
})
public @interface DeleteUserApiDoc {
}