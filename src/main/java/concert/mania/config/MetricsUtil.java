package concert.mania.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 메트릭 유틸리티 클래스
 * 
 * 애플리케이션 내에서 메트릭을 쉽게 사용할 수 있도록 도와주는 유틸리티 클래스
 */
@Component
public class MetricsUtil {

    private final MeterRegistry meterRegistry;

    public MetricsUtil(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * 카운터 메트릭 증가
     * 
     * @param name 메트릭 이름
     * @param tags 메트릭 태그 (key1, value1, key2, value2, ...)
     */
    public void incrementCounter(String name, String... tags) {
        Counter counter = Counter.builder(name)
                .tags(tags)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * 타이머 메트릭으로 코드 실행 시간 측정
     * 
     * @param name 메트릭 이름
     * @param supplier 측정할 코드 블록
     * @param tags 메트릭 태그 (key1, value1, key2, value2, ...)
     * @return supplier의 실행 결과
     */
    public <T> T recordTimer(String name, Supplier<T> supplier, String... tags) {
        Timer timer = Timer.builder(name)
                .tags(tags)
                .register(meterRegistry);
        
        return timer.record(supplier);
    }

    /**
     * 타이머 메트릭으로 코드 실행 시간 측정 (반환값 없는 버전)
     * 
     * @param name 메트릭 이름
     * @param runnable 측정할 코드 블록
     * @param tags 메트릭 태그 (key1, value1, key2, value2, ...)
     */
    public void recordTimer(String name, Runnable runnable, String... tags) {
        Timer timer = Timer.builder(name)
                .tags(tags)
                .register(meterRegistry);
        
        timer.record(runnable);
    }

    /**
     * 직접 시간을 측정하여 타이머 메트릭에 기록
     * 
     * @param name 메트릭 이름
     * @param timeNanos 측정된 시간 (나노초)
     * @param tags 메트릭 태그 (key1, value1, key2, value2, ...)
     */
    public void recordTime(String name, long timeNanos, String... tags) {
        Timer timer = Timer.builder(name)
                .tags(tags)
                .register(meterRegistry);
        
        timer.record(timeNanos, TimeUnit.NANOSECONDS);
    }
}