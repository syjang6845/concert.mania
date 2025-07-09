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
 * 콘서트 API 문서화
 * 콘서트 관련 API 엔드포인트 및 사용 방법에 대한 문서
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Concert Mania API - 콘서트 관리",
                version = "1.0",
                description = """
                        # 콘서트 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 콘서트 관련 API를 설명합니다.
                        
                        ## 콘서트 관리 흐름
                        
                        1. **콘서트 목록 조회**: `/api/v1/concerts` (GET) 엔드포인트를 통해 모든 콘서트 목록 조회
                        2. **콘서트 상세 정보 조회**: `/api/v1/concerts/{concertId}` (GET) 엔드포인트를 통해 특정 콘서트의 상세 정보 조회
                        
                        ## 콘서트 정보
                        
                        콘서트 정보에는 다음과 같은 내용이 포함됩니다:
                        
                        - 콘서트 ID
                        - 콘서트 이름
                        - 콘서트 설명
                        - 콘서트 날짜 및 시간
                        - 콘서트 장소
                        - 콘서트 이미지 URL
                        - 콘서트 상태 (예정, 진행 중, 종료 등)
                        
                        ## 예외 조건
                        
                        - 존재하지 않는 콘서트 ID로 조회 시 오류 발생
                        - 종료된 콘서트는 조회만 가능하고 예매는 불가능
                        
                        ## 보안 고려사항
                        
                        - 콘서트 목록 및 상세 정보 조회는 인증 없이 가능
                        - 콘서트 예매는 인증된 사용자만 가능
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
                @Tag(name = "Concert", description = "콘서트 API")
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
public class ConcertApiDocumentation {
    // 이 클래스는 Swagger 문서화를 위한 것으로 실제 구현 내용은 없습니다.
}