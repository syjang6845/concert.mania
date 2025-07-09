package concert.mania.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration(){
        // 서킷 브레이커 설정을 정의
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 실패한 비율이 50% 이상일 때 요청 차단
                .waitDurationInOpenState(Duration.ofMillis(5000)) // 요청 차단 5초 대기
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // 카운트 기반
                .slidingWindowSize(10) // 10개의 요청 기준으로 실패율 계산
                .minimumNumberOfCalls(5) // 최소 5건 호출 이후 상태 판단
                .build();

        // 타임 리미터 설정을 정의
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4)) // 타임아웃 시간을 4초로 설정
                .build();

        // Resilience4J 서킷 브레이커 팩토리의 기본 설정을 커스터마이징
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig) // 타임 리미터 설정 적용
                .circuitBreakerConfig(circuitBreakerConfig) // 서킷 브레이커 설정 적용
                .build()
        );
    }
}
