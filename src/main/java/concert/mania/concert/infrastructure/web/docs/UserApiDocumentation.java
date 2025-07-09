package concert.mania.concert.infrastructure.web.docs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 사용자 API 문서화
 * 사용자 관리 관련 API 엔드포인트 및 사용 방법에 대한 문서
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Concert Mania API - 사용자 관리",
                version = "1.0",
                description = """
                        # 사용자 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 사용자 관리 관련 API를 설명합니다.
                        
                        ## 사용자 관리 흐름
                        
                        1. **회원가입**: `/api/v1/users` (POST) 엔드포인트를 통해 사용자 계정 생성
                        2. **사용자 정보 조회**: `/api/v1/users/{email}` (GET) 엔드포인트를 통해 사용자 정보 조회
                        3. **회원탈퇴**: `/api/v1/users/{userId}` (DELETE) 엔드포인트를 통해 사용자 계정 삭제
                        
                        ## 회원가입 전 필수 절차 (순서 중요!)
                        
                        1. 이메일 인증 코드 발송 (`/api/v1/emails/auth`)
                        2. 이메일 인증 코드 검증 (`/api/v1/emails/auth/validate`) → credential 획득
                        3. 본인인증 완료 → ci 값 획득
                        4. 회원가입 진행 (현재 API)
                        
                        ## 사용자 유형별 요구사항
                        
                        - **학습자 (STUDENT)**: 기본 정보만 필요
                        - **교육자 (EDUCATOR)**: affiliationName(기관명) 필수
                        - **14세 미만**: guardianConsent(보호자 동의) 필수
                        
                        ## 비밀번호 정책
                        
                        - 8자 이상 30자 이하
                        - 영문 + 숫자 + 특수문자 조합
                        - 허용 특수문자: !@#$%^&*()_+{}"';<>
                        
                        ## 필수 약관
                        
                        - termsAgree: 이용 약관 동의 (필수)
                        - privacyAgree: 개인정보처리방침 동의 (필수)
                        
                        ## 선택 약관
                        
                        - marketingAgree: 마케팅 수신 동의
                        - receiveNotification: 알림 수신 동의
                        
                        ## 회원탈퇴 처리 방식
                        
                        - 즉시 탈퇴 처리 (Soft Delete)
                        - 자동 로그아웃 처리
                        - Refresh Token 쿠키 삭제
                        - 탈퇴 이력 저장
                        - 관련 데이터 정리 (프로필 이미지 등)
                        
                        ## 보안 고려사항
                        
                        - 사용자 정보 조회 및 탈퇴 시 본인 확인 필수
                        - 회원탈퇴 시 현재 비밀번호 확인 필수
                        - 모든 API 호출 시 JWT 토큰 인증 필요 (회원가입 제외)
                        """,
                contact = @Contact(
                        name = "Concert Mania Team",
                        email = "support@concertmania.com"
                )
        ),
        security = {
                @SecurityRequirement(name = "Bearer")
        },
        tags = {
                @Tag(name = "사용자 관리 API", description = "사용자 계정 관리 관련 API")
        }
)
@SecuritySchemes({
        @SecurityScheme(
                name = "Bearer",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT",
                description = """
                        JWT 인증을 위한 설정입니다.
                        
                        로그인 API를 통해 발급받은 Access Token을 아래와 같이 입력하세요:
                        
                        ```
                        Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                        ```
                        """
        )
})
public class UserApiDocumentation {
    // 이 클래스는 Swagger 문서화를 위한 것으로 실제 구현 내용은 없습니다.
}