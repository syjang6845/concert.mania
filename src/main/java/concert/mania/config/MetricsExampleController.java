package concert.mania.config;

import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 메트릭 사용 예제 컨트롤러
 * 
 * 메트릭 사용 방법을 보여주는 예제 컨트롤러
 */
@RestController
@RequestMapping("/api/metrics-example")
public class MetricsExampleController {

    private final MetricsUtil metricsUtil;

    public MetricsExampleController(MetricsUtil metricsUtil) {
        this.metricsUtil = metricsUtil;
    }

    /**
     * @Timed 어노테이션을 사용한 메트릭 측정 예제
     * 
     * @return 간단한 응답 메시지
     */
    @GetMapping("/timed")
    @Timed(value = "metrics.example.timed", description = "Time taken to execute timed endpoint")
    public String timedExample() {
        // 비즈니스 로직 실행
        try {
            Thread.sleep(100); // 예제를 위한 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "Timed endpoint executed successfully";
    }

    /**
     * MetricsUtil을 사용한 카운터 메트릭 예제
     * 
     * @return 간단한 응답 메시지
     */
    @GetMapping("/counter")
    public String counterExample() {
        // 카운터 메트릭 증가
        metricsUtil.incrementCounter("metrics.example.counter", 
                "method", "counterExample", 
                "status", "success");
        
        return "Counter metric incremented";
    }

    /**
     * MetricsUtil을 사용한 타이머 메트릭 예제
     * 
     * @return 비즈니스 로직 실행 결과
     */
    @GetMapping("/timer")
    public String timerExample() {
        // 타이머 메트릭으로 코드 실행 시간 측정
        return metricsUtil.recordTimer("metrics.example.timer",
                () -> {
                    // 비즈니스 로직 실행
                    try {
                        Thread.sleep(200); // 예제를 위한 지연
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "Timer metric recorded";
                },
                "method", "timerExample",
                "status", "success");
    }
}