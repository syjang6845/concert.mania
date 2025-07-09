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
 * 인증 API 문서화
 * 사용자 인증 관련 API 엔드포인트 및 인증 방식에 대한 문서
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Concert Mania API - 인증",
                version = "1.0",
                description = """
                        # 인증 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 인증 관련 API를 설명합니다.
                        
                        ## 인증 흐름
                        
                        1. **회원가입**: `/api/v1/users` (POST) 엔드포인트를 통해 사용자 계정 생성
                        2. **로그인**: `/api/v1/users/authentication/login` (POST) 엔드포인트를 통해 인증 토큰 발급
                        3. **API 호출**: 발급받은 Access Token을 사용하여 보호된 API 호출
                        4. **토큰 갱신**: Access Token 만료 시 `/api/v1/users/authentication/tokens/refresh` (POST) 엔드포인트를 통해 갱신
                        5. **로그아웃**: `/api/v1/users/authentication/logout` (POST) 엔드포인트를 통해 인증 세션 종료
                        
                        ## 인증 토큰 설정 방법
                        
                        ### Access Token
                        
                        - **형식**: JWT (JSON Web Token)
                        - **유효기간**: 30분
                        - **전송 방식**: HTTP Authorization 헤더
                        - **헤더 형식**: `Authorization: Bearer {token}`
                        
                        ```
                        // 예시
                        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTY5MDg2OTAwMH0.signature
                        ```
                        
                        ### Refresh Token
                        
                        - **형식**: JWT (JSON Web Token)
                        - **유효기간**: 3일
                        - **전송 방식**: HTTP Only 쿠키 (자동 전송)
                        - **쿠키 이름**: `refreshToken`
                        
                        ### Swagger에서 토큰 테스트하기
                        
                        1. 로그인 API를 호출하여 Access Token 획득
                        2. Swagger UI 상단의 'Authorize' 버튼 클릭
                        3. 발급받은 Access Token을 `Bearer {token}` 형식으로 입력
                        4. 'Authorize' 버튼 클릭하여 설정 완료
                        5. 이후 모든 보호된 API 호출 시 자동으로 토큰 적용
                        
                        ### 토큰 만료 처리
                        
                        - Access Token 만료 시 401 Unauthorized 응답
                        - 401 응답 수신 시 `/api/v1/users/authentication/tokens/refresh` API를 호출하여 새 토큰 발급
                        - Refresh Token도 만료된 경우 재로그인 필요
                        
                        ### 보안 고려사항
                        
                        - Access Token은 클라이언트 측에서 안전하게 저장 (localStorage 사용 시 XSS 공격 위험)
                        - Refresh Token은 HttpOnly 쿠키로 자동 관리되어 JavaScript에서 접근 불가
                        - 민감한 작업 수행 시 추가 인증 권장
                        """,
                contact = @Contact(
                        name = "Concert Mania Team",
                        email = "support@concertmania.com"
                )
        ),
        security = {
                @SecurityRequirement(name = "Bearer")
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
public class AuthApiDocumentation {
    // 이 클래스는 Swagger 문서화를 위한 것으로 실제 구현 내용은 없습니다.
}