package concert.mania.config;

import concert.mania.jwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import concert.mania.jwt.filter.JwtExceptionFilter;
import concert.mania.concert.domain.model.type.Authority;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // 🔥 변경

    private final JwtExceptionFilter jwtExceptionFilter;

    // 🔥 Environment 주입 (프로파일 체크용)
    private final Environment environment;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        String[] getAllowList = {
                "/actuator/health",
                "/actuator/info",
                "/actuator/prometheus",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/webjars/**",
                "/swagger-ui/index.html"
        };

        String[] postAllowList = {
                "/api/v1/users",
                "/api/v1/users/authentication/login",
        };

        String[] authenticationAllowList = {
                "/api/v1/users/authentication/login",
                "/api/v1/users/authentication/tokens/refresh"
        };

        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> {
                    // 🔥 AuthorizeHttpRequestsConfigurer 체인 방식으로 수정
                    var requests = auth
                            // 맨 처음에 actuator/health를 명시적으로 허용
                            .requestMatchers("/actuator/health").permitAll()
                            .requestMatchers(HttpMethod.GET, getAllowList).permitAll()
                            .requestMatchers(HttpMethod.POST, postAllowList).permitAll()
                            .requestMatchers(HttpMethod.POST, authenticationAllowList).permitAll();

                    // 🔥 나머지 권한 설정 (return 없이 체인 방식으로)
                    requests
                            .requestMatchers(HttpMethod.POST, "/api/v1/users/authentication/logout")
                            .hasAnyAuthority(Authority.ROLE_USER.name())
                            .requestMatchers(HttpMethod.GET, "/api/v1/users/{email}," +
                                    "/api/v1/concerts",
                                    "api/v1/concerts/{concertId}/seat-grades/{seatGradeId}/seats")
                            .hasAnyAuthority(Authority.ROLE_USER.name(), Authority.ROLE_ADMIN.name())
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{userId}")
                            .hasAnyAuthority(Authority.ROLE_USER.name(), Authority.ROLE_ADMIN.name())
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/users/{userId}")
                            .hasAnyAuthority(Authority.ROLE_USER.name())
                            .anyRequest().authenticated();

                })
                .sessionManagement((sessionManagementConfig) -> sessionManagementConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtExceptionFilter, JwtAuthenticationFilter.class)
                .build();
    }
}