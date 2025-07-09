
package concert.mania.config;

import concert.mania.common.constants.JwtConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final Environment env;

    @Bean
    public OpenAPI openAPI() {
        String port = env.getProperty("server.port", "8080");

        // 서버 목록 생성
        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url("http://localhost:" + port).description("로컬 개발 서버"));
        servers.add(new Server().url("/").description("현재 서버"));

        // OpenAPI 공통 설정 (기본 정보만)
        return new OpenAPI()
                .info(getCommonInfo())
                .addSecurityItem(getSecurityRequirement())
                .components(getSecurityComponents())
                .servers(servers);
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .displayName("사용자 API")
                .pathsToMatch(
                        "/api/v1/users/{userId}",
                        "/api/v1/users/{email}",
                        "/api/v1/users"
                        )
                .addOpenApiCustomizer(openApi -> openApi.info(getUserInfo()))
                .build();
    }


    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .displayName("인증 API")
                .pathsToMatch("/api/v1/users/authentication/**")
                .addOpenApiCustomizer(openApi -> openApi.info(getAuthInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi concertApi() {
        return GroupedOpenApi.builder()
                .group("concert")
                .displayName("콘서트 API")
                .pathsToMatch("/api/v1/concerts/**")
                .addOpenApiCustomizer(openApi -> openApi.info(getConcertInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi waitingApi() {
        return GroupedOpenApi.builder()
                .group("waiting")
                .displayName("대기열 API")
                .pathsToMatch("/api/v1/admin/**")
                .addOpenApiCustomizer(openApi -> openApi.info(getWaitingInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi seatApi() {
        return GroupedOpenApi.builder()
                .group("seats")
                .displayName("좌석 API")
                .pathsToMatch("/api/v1/seats/**", "/api/v1/concerts/*/seats/**", "/api/v1/seat-grades/*/seats/**")
                .addOpenApiCustomizer(openApi -> openApi.info(getSeatInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("payment")
                .displayName("결제 API")
                .pathsToMatch("/api/v1/payments/**")
                .addOpenApiCustomizer(openApi -> openApi.info(getPaymentInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi reservationsApi() {
        return GroupedOpenApi.builder()
                .group("reservations")
                .displayName("예약 API")
                .pathsToMatch("/api/v1/reservations/**")
                .addOpenApiCustomizer(openApi -> openApi.info(getReservationInfo()))
                .build();
    }


    // 공통 정보
    public Info getCommonInfo() {
        return new Info()
                .title("Concert Mania API Docs")
                .description("Concert Mania API 문서")
                .version("1.0")
                .contact(new Contact()
                        .name("Concert Mania Team")
                        .email("support@concertmania.com"));
    }

    // 각 그룹별 정보 설정
    public Info getAuthInfo() {
        return new Info()
                .title("Concert Mania API - 인증 관리")
                .version("1.0")
                .description("""
                        # 인증 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 인증 관련 API를 설명합니다.
                        
                        ## 인증 관리 흐름
                        
                        1. **로그인**: `/api/v1/users/authentication/login` (POST)
                        2. **로그아웃**: `/api/v1/users/authentication/logout` (POST)
                        3. **토큰 갱신**: `/api/v1/users/authentication/tokens/refresh` (POST)
                        
                        ## 보안 고려사항
                        
                        - JWT 토큰을 사용한 인증
                        - Access Token (2시간) + Refresh Token (7일)
                        - 로그아웃 시 토큰 무효화
                        """)
                .contact(new Contact()
                        .name("Concert Mania Team")
                        .email("support@concertmania.com"));
    }

    public Info getConcertInfo() {
        return new Info()
                .title("Concert Mania API - 콘서트 관리")
                .version("1.0")
                .description("""
                        # 콘서트 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 콘서트 관련 API를 설명합니다.
                        
                        ## 콘서트 관리 흐름
                        
                        1. **콘서트 목록 조회**: `/api/v1/concerts` (GET) - 모든 콘서트 목록 조회
                        2. **콘서트 상세 정보 조회**: `/api/v1/concerts/{concertId}` (GET) - 특정 콘서트 상세 정보
                        
                        ## 콘서트 정보
                        
                        - 콘서트 ID, 이름, 설명
                        - 콘서트 날짜 및 시간
                        - 콘서트 장소, 이미지 URL
                        - 콘서트 상태 (예정, 진행 중, 종료)
                        
                        ## 보안 고려사항
                        
                        - 콘서트 조회는 인증 없이 가능
                        - 콘서트 예매는 인증된 사용자만 가능
                        """)
                .contact(new Contact()
                        .name("Concert Mania Team")
                        .email("support@concertmania.com"));
    }

    public Info getWaitingInfo() {
        return new Info()
                .title("Concert Mania API - 대기열 관리")
                .version("1.0")
                .description("""
                        # 대기열 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 대기열 관련 API를 설명합니다.
                        
                        ## 대기열 관리 흐름
                        
                        1. **대기열 입장**: 콘서트 예매 시 자동으로 대기열 입장
                        2. **대기열 상태 확인**: 현재 대기 순서 및 예상 대기 시간 조회
                        3. **대기열 통과**: 순서가 되면 예매 페이지 접근 가능
                        
                        ## 대기열 시스템
                        
                        - FIFO (First In First Out) 방식
                        - 실시간 대기 순서 업데이트
                        - 자동 세션 관리
                        """)
                .contact(new Contact()
                        .name("Concert Mania Team")
                        .email("support@concertmania.com"));
    }

    public Info getSeatInfo() {
        return new Info()
                .title("Concert Mania API - 좌석 관리")
                .version("1.0")
                .description("""
                        # 좌석 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 좌석 관리 관련 API를 설명합니다.
                        
                        ## 좌석 관리 흐름
                        
                        1. **좌석 목록 조회**: 콘서트별 또는 좌석 등급별 좌석 조회
                        2. **좌석 선택**: 10분 동안 임시 점유
                        3. **좌석 잠금 시간 연장**: 필요시 10분 추가 연장
                        4. **좌석 선택 취소**: 임시 점유 해제
                        
                        ## 좌석 상태
                        
                        - **AVAILABLE**: 선택 가능
                        - **LOCKED**: 임시 점유 (10분)
                        - **RESERVED**: 예약 완료
                        - **UNAVAILABLE**: 선택 불가
                        """)
                .contact(new Contact()
                        .name("Concert Mania Team")
                        .email("support@concertmania.com"));
    }

    public Info getPaymentInfo() {
        return new Info()
                .title("Concert Mania API - 결제 관리")
                .version("1.0")
                .description("""
                        # 결제 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 결제 관련 API를 설명합니다.
                        
                        ## 결제 관리 흐름
                        
                        1. **결제 요청**: 선택한 좌석에 대한 결제 진행
                        2. **결제 확인**: 결제 완료 후 예약 확정
                        3. **결제 내역 조회**: 사용자별 결제 내역 확인
                        
                        ## 결제 방법
                        
                        - 신용카드, 계좌이체, 간편결제 지원
                        - 실시간 결제 상태 확인
                        - 결제 실패 시 자동 롤백
                        """)
                .contact(new Contact()
                        .name("Concert Mania Team")
                        .email("support@concertmania.com"));
    }

    public Info getReservationInfo() {
        return new Info()
                .title("Concert Mania API - 예약 관리")
                .version("1.0")
                .description("""
                        # 예약 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 예약 관련 API를 설명합니다.
                        
                        ## 예약 관리 흐름
                        
                        1. **예약 확정**: 결제 완료 후 예약 확정
                        2. **예약 조회**: 예약 ID 또는 예약 번호로 조회
                        3. **예약 목록 조회**: 사용자별 예약 목록 확인
                        
                        ## 예약 상태
                        
                        - **PENDING**: 예약 대기 (결제 전)
                        - **COMPLETED**: 예약 완료 (결제 완료)
                        - **CANCELLED**: 예약 취소
                        - **EXPIRED**: 예약 만료
                        """)
                .contact(new Contact()
                        .name("Concert Mania Team")
                        .email("support@concertmania.com"));
    }

    public Info getUserInfo() {
        return new Info()
                .title("Concert Mania API - 사용자 관리")
                .version("1.0")
                .description("""
                        # 사용자 관리 API 문서
                        
                        이 문서는 Concert Mania 애플리케이션의 사용자 관리 관련 API를 설명합니다.
                        
                        ## 사용자 관리 흐름
                        
                        1. **회원가입**: 새로운 사용자 계정 생성
                        2. **사용자 정보 조회**: 사용자 정보 확인
                        3. **회원탈퇴**: 사용자 계정 삭제
                        
                        ## 회원가입 전 필수 절차
                        
                        1. 이메일 인증 코드 발송 및 검증
                        2. 본인인증 완료
                        3. 약관 동의
                        4. 회원가입 진행
                        """)
                .contact(new Contact()
                        .name("Concert Mania Team")
                        .email("support@concertmania.com"));
    }

    public SecurityScheme getSecurityScheme() {
        SecurityScheme scheme = new SecurityScheme();
        scheme.setName("jwt");
        scheme.setType(SecurityScheme.Type.HTTP);
        scheme.setScheme(JwtConstants.BEARER_TYPE);
        scheme.setBearerFormat("JWT");
        return scheme;
    }

    public Components getSecurityComponents() {
        Components components = new Components();
        components.addSecuritySchemes(JwtConstants.BEARER_TYPE, getSecurityScheme());
        return components;
    }

    public SecurityRequirement getSecurityRequirement() {
        return new SecurityRequirement().addList(JwtConstants.BEARER_TYPE);
    }
}