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
 * 예약 API 문서화
 * 예약 관련 API 엔드포인트 및 사용 방법에 대한 문서
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Concert Mania API - 예약 관리",
                version = "1.0",
                description = """
                        # 예약 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 예약 관련 API를 설명합니다.
                        
                        ## 예약 관리 흐름
                        
                        1. **예약 확정**: `/api/v1/reservations/confirm` (POST) 엔드포인트를 통해 예약 확정
                        2. **예약 조회**: `/api/v1/reservations/{reservationId}` (GET) 엔드포인트를 통해 예약 ID로 예약 정보 조회
                        3. **예약 번호로 예약 조회**: `/api/v1/reservations/number/{reservationNumber}` (GET) 엔드포인트를 통해 예약 번호로 예약 정보 조회
                        4. **사용자의 예약 목록 조회**: `/api/v1/reservations/user/{userId}` (GET) 엔드포인트를 통해 사용자의 예약 목록 조회
                        
                        ## 예약 프로세스
                        
                        1. 사용자가 좌석을 선택하고 결제 진행
                        2. 결제 완료 후 예약 상태를 PENDING에서 COMPLETED로 변경
                        3. 예약 확정 시 예약 번호 생성
                        4. 사용자는 예약 ID 또는 예약 번호로 예약 정보 조회 가능
                        
                        ## 예약 상태
                        
                        - **PENDING**: 예약 대기 상태 (결제 전)
                        - **COMPLETED**: 예약 완료 상태 (결제 완료)
                        - **CANCELLED**: 예약 취소 상태
                        - **EXPIRED**: 예약 만료 상태 (미사용)
                        
                        ## 예외 조건
                        
                        - 존재하지 않는 예약 ID로 조회 시 오류 발생
                        - 본인의 예약만 조회 가능 (사용자 ID 검증)
                        - 이미 확정된 예약은 다시 확정할 수 없음
                        - 취소된 예약은 조회만 가능하고 변경 불가
                        
                        ## 보안 고려사항
                        
                        - 예약 조회 및 확정 시 사용자 인증 필수
                        - 본인의 예약만 조회 및 관리 가능
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
                @Tag(name = "Reservation", description = "예약 API")
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
public class ReservationApiDocumentation {
    // 이 클래스는 Swagger 문서화를 위한 것으로 실제 구현 내용은 없습니다.
}