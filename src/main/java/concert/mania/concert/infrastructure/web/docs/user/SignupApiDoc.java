package concert.mania.concert.infrastructure.web.docs.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.request.CreateUserRequest;
import concert.mania.concert.infrastructure.web.dto.response.UserProfileResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "사용자 회원가입",
        description = """
        새로운 사용자 회원가입 API
        
        **회원가입 전 필수 절차 (순서 중요!):**
        1. 이메일 인증 코드 발송 (/api/v1/emails/auth)
        2. 이메일 인증 코드 검증 (/api/v1/emails/auth/validate) → credential 획득
        3. 본인인증 완료 → ci 값 획득
        4. 회원가입 진행 (현재 API)
        
        **이메일 인증 검증 단계:**
        - credential 값이 존재하는지 확인
        - credential 값이 올바른지 검증
        - 인증이 완료된 상태인지 확인
        
        **사용자 유형별 요구사항:**
        - **학습자 (STUDENT)**: 기본 정보만 필요
        - **교육자 (EDUCATOR)**: affiliationName(기관명) 필수
        - **14세 미만**: guardianConsent(보호자 동의) 필수
        
        **비밀번호 정책:**
        - 8자 이상 30자 이하
        - 영문 + 숫자 + 특수문자 조합
        - 허용 특수문자: !@#$%^&*()_+{}"';<>
        
        **필수 약관:**
        - termsAgree: 이용 약관 동의 (필수)
        - privacyAgree: 개인정보처리방침 동의 (필수)
        
        **선택 약관:**
        - marketingAgree: 마케팅 수신 동의
        - receiveNotification: 알림 수신 동의
        
        **Custom Validation:**
        - @TeacherAffiliationRequired: EDUCATOR 역할 시 기관명 필수 검증
        
        **검증 순서:**
        1. 이메일 중복 확인
        2. 이메일 인증 완료 여부 확인
        3. 본인인증 정보 확인
        4. 사용자 생성
        """
)

@RequestBody(
        description = "회원가입 요청 정보",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateUserRequest.class),
                examples = {
                        @ExampleObject(
                                name = "학습자 회원가입",
                                summary = "일반 학습자 회원가입 예시",
                                value = """
                                {
                                    "email": "student@example.com",
                                    "password": "SecurePass123!",
                                    "credential": "encrypted_email_auth_token",
                                    "name": "홍길동",
                                    "ci": "encrypted_ci_value_from_identity_auth",
                                    "role": "STUDENT",
                                    "termsAgree": true,
                                    "privacyAgree": true,
                                    "receiveNotification": true,
                                    "marketingAgree": false,
                                    "guardianConsent": null,
                                    "affiliationName": null
                                }
                                """
                        ),
                        @ExampleObject(
                                name = "교육자 회원가입",
                                summary = "교육자 회원가입 예시 (기관명 필수)",
                                value = """
                                {
                                    "email": "eucator@school.edu",
                                    "password": "eucatorPass456!",
                                    "credential": "encrypted_email_auth_token",
                                    "name": "김선생",
                                    "ci": "encrypted_ci_value_from_identity_auth",
                                    "role": "EDUCATOR",
                                    "termsAgree": true,
                                    "privacyAgree": true,
                                    "receiveNotification": true,
                                    "marketingAgree": true,
                                    "guardianConsent": null,
                                    "affiliationName": "서울초등학교"
                                }
                                """
                        ),
                        @ExampleObject(
                                name = "14세 미만 회원가입",
                                summary = "보호자 동의가 필요한 미성년자 회원가입",
                                value = """
                                {
                                    "email": "child@parent.com",
                                    "password": "ChildPass789!",
                                    "credential": "encrypted_email_auth_token",
                                    "name": "이어린이",
                                    "ci": "encrypted_ci_value_from_identity_auth",
                                    "role": "STUDENT",
                                    "termsAgree": true,
                                    "privacyAgree": true,
                                    "receiveNotification": false,
                                    "marketingAgree": false,
                                    "guardianConsent": true,
                                    "affiliationName": null
                                }
                                """
                        )
                }
        )
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "회원가입 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserProfileResponse.class),
                        examples = @ExampleObject(
                                value = """
                                {
                                    "id": 12345,
                                    "email": "student@example.com",
                                    "name": "홍길동",
                                    "affiliationName": null,
                                    "role": "STUDENT",
                                    "registerType": "EMAIL",
                                    "marketingAgree": false,
                                    "receiveAgree": true,
                                    "createdAt": "2025-06-17T10:00:00"
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
                                        name = "필수 필드 누락",
                                        summary = "필수 입력 필드가 누락된 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "BAD_REQUEST",
                                            "message": "이메일을 입력하세요.",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "비밀번호 정책 위반",
                                        summary = "비밀번호가 정책에 맞지 않는 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "BAD_REQUEST",
                                            "message": "비밀번호는 영문+숫자+특수기호 조합으로 가능합니다.",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "이메일 인증 실패",
                                        summary = "credential 값이 유효하지 않은 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "EMAIL_AUTH_INVALID",
                                            "message": "이메일 인증이 유효하지 않습니다.",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "본인인증 실패",
                                        summary = "CI 값이 유효하지 않은 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "IDENTITY_AUTH_NOT_FOUND",
                                            "message": "본인인증 정보를 찾을 수 없습니다.",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "교육자 기관명 누락",
                                        summary = "EDUCATOR 역할에 기관명이 없는 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "EDUCATOR_AFFILIATION_REQUIRED",
                                            "message": "교육자는 기관명이 필수입니다.",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                ),
                                @ExampleObject(
                                        name = "약관 동의 필수",
                                        summary = "필수 약관에 동의하지 않은 경우",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "BAD_REQUEST",
                                            "message": "이용 약관 동의 여부는 필수입니다.",
                                            "path": "/api/v1/users"
                                        }
                                        """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "이미 가입된 이메일",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "중복 이메일",
                                summary = "이미 가입된 이메일로 회원가입 시도",
                                value = """
                                {
                                    "timestamp": "2025-06-17T10:00:00",
                                    "statusCode": 409,
                                    "errorCode": "DUPLICATE_EMAIL",
                                    "message": "이미 사용중인 이메일입니다.",
                                    "path": "/api/v1/users"
                                }
                                """
                        )
                )
        )
})
public @interface SignupApiDoc {
}