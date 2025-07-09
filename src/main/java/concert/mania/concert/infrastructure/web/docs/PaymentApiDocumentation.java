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
 * 결제 API 문서화
 * 결제 관련 API 엔드포인트 및 사용 방법에 대한 문서
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Concert Mania API - 결제 관리",
                version = "1.0",
                description = """
                        # 결제 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 결제 관련 API를 설명합니다.
                        
                        ## 결제 관리 흐름
                        
                        1. **결제 요청**: `/api/v1/payments` (POST) 엔드포인트를 통해 결제 요청
                        2. **결제 상태 확인**: `/api/v1/payments/{paymentId}` (GET) 엔드포인트를 통해 결제 상태 확인
                        3. **결제 취소**: `/api/v1/payments/{paymentId}/cancel` (POST) 엔드포인트를 통해 결제 취소
                        4. **외부 결제 ID로 결제 정보 조회**: `/api/v1/payments/external/{externalPaymentId}` (GET) 엔드포인트를 통해 외부 결제 ID로 결제 정보 조회
                        5. **결제 완료 콜백**: `/api/v1/payments/callback/complete` (POST) 엔드포인트를 통해 PG사에서 결제 완료 후 호출하는 콜백 처리
                        6. **결제 실패 콜백**: `/api/v1/payments/callback/fail` (POST) 엔드포인트를 통해 PG사에서 결제 실패 시 호출하는 콜백 처리
                        
                        ## 결제 프로세스
                        
                        1. 사용자가 좌석을 선택하고 결제 요청
                        2. 시스템에서 결제 정보 생성 및 PG사로 결제 요청 전달
                        3. 사용자가 PG사 결제 페이지에서 결제 진행
                        4. 결제 완료 또는 실패 시 PG사에서 콜백 API 호출
                        5. 결제 완료 시 예약 확정, 실패 시 좌석 선택 취소
                        
                        ## 결제 상태
                        
                        - **PENDING**: 결제 대기 상태
                        - **COMPLETED**: 결제 완료 상태
                        - **FAILED**: 결제 실패 상태
                        - **CANCELLED**: 결제 취소 상태
                        
                        ## 결제 방식
                        
                        - **CREDIT_CARD**: 신용카드 결제
                        - **BANK_TRANSFER**: 계좌이체
                        - **VIRTUAL_ACCOUNT**: 가상계좌
                        - **MOBILE_PAYMENT**: 모바일 결제
                        
                        ## 예외 조건
                        
                        - 존재하지 않는 결제 ID로 조회 시 오류 발생
                        - 이미 취소된 결제는 다시 취소할 수 없음
                        - 본인이 요청한 결제만 취소 가능
                        - 결제 완료 후 일정 시간이 지난 결제는 취소 불가능
                        
                        ## 보안 고려사항
                        
                        - 결제 요청 및 취소 시 사용자 인증 필수
                        - 콜백 API는 PG사의 IP 및 인증 정보 검증 필요
                        - 결제 정보는 암호화하여 저장
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
                @Tag(name = "Payment", description = "결제 API")
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
public class PaymentApiDocumentation {
    // 이 클래스는 Swagger 문서화를 위한 것으로 실제 구현 내용은 없습니다.
}