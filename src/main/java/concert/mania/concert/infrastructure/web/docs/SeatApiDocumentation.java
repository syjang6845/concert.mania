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
 * 좌석 API 문서화
 * 좌석 관리 관련 API 엔드포인트 및 사용 방법에 대한 문서
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Concert Mania API - 좌석 관리",
                version = "1.0",
                description = """
                        # 좌석 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 좌석 관리 관련 API를 설명합니다.
                        
                        ## 좌석 관리 흐름
                        
                        1. **콘서트 좌석 목록 조회**: `/api/v1/concerts/{concertId}/seats` (GET) 엔드포인트를 통해 콘서트의 모든 좌석 조회
                        2. **좌석 등급별 좌석 목록 조회**: `/api/v1/concerts/{concertId}/seat-grades/{seatGradeId}/seats` (GET) 엔드포인트를 통해 특정 등급의 좌석 조회
                        3. **좌석 선택**: `/api/v1/seats/{seatId}/select` (POST) 엔드포인트를 통해 좌석 임시 점유
                        4. **좌석 잠금 시간 연장**: `/api/v1/seats/{seatId}/extend` (POST) 엔드포인트를 통해 좌석 잠금 시간 연장
                        5. **좌석 선택 취소**: `/api/v1/seats/{seatId}/cancel` (POST) 엔드포인트를 통해 좌석 임시 점유 취소
                        6. **좌석 잠금 정보 조회**: `/api/v1/seats/{seatId}/lock` (GET) 엔드포인트를 통해 좌석 잠금 정보 조회
                        
                        ## 좌석 선택 프로세스
                        
                        1. 대기열 입장 완료 상태인 사용자만 좌석 선택 가능
                        2. 좌석 선택 시 10분 동안 임시 점유 상태로 변경
                        3. 10분 내에 결제를 완료하지 않으면 자동으로 좌석 선택 취소
                        4. 필요 시 좌석 잠금 시간 연장 가능 (10분 추가)
                        5. 좌석 선택 취소 시 즉시 다른 사용자가 선택 가능한 상태로 변경
                        
                        ## 좌석 상태
                        
                        - **AVAILABLE**: 선택 가능한 상태
                        - **LOCKED**: 임시 점유 상태 (10분)
                        - **RESERVED**: 예약 완료 상태 (결제 완료)
                        - **UNAVAILABLE**: 선택 불가능한 상태 (시스템에 의해 차단)
                        
                        ## 예외 조건
                        
                        - 대기열 입장 완료 상태가 아닌 사용자는 좌석 선택 불가
                        - 이미 선택된 좌석은 다른 사용자가 선택 불가
                        - 본인이 선택한 좌석만 취소 가능
                        - 좌석 잠금 시간 연장은 본인이 선택한 좌석만 가능
                        
                        ## 보안 고려사항
                        
                        - 좌석 선택/취소/연장 시 사용자 ID 검증 필수
                        - 대기열 상태 검증을 통한 무단 접근 방지
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
                @Tag(name = "Seat", description = "좌석 관리 API")
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
public class SeatApiDocumentation {
    // 이 클래스는 Swagger 문서화를 위한 것으로 실제 구현 내용은 없습니다.
}