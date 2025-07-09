# 애플리케이션 메트릭 설정 가이드

이 문서는 Concert Mania 애플리케이션의 메트릭 모니터링 시스템 설정 및 사용 방법을 설명합니다.

## 기술 스택

- **Spring Boot Actuator**: 애플리케이션 모니터링 및 관리를 위한 기능 제공
- **Micrometer**: 애플리케이션 메트릭 수집 라이브러리
- **Prometheus**: 메트릭 데이터 수집 및 저장
- **Grafana**: 메트릭 데이터 시각화 및 대시보드

## 목적

- 시스템 성능 모니터링
- 자원 사용률 추적
- 병목 현상 진단
- 애플리케이션 상태 실시간 파악

## 설정 방법

### 1. 의존성 설정

`build.gradle` 파일에 다음 의존성이 추가되어 있습니다:

```gradle
// actuator and metrics
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

### 2. 애플리케이션 설정

`application.yml` 파일에 다음 설정이 추가되어 있습니다:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, prometheus, metrics, env, loggers
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.9, 0.95, 0.99
    tags:
      application: ${spring.application.name}
```

### 3. Docker Compose 설정

`docker-compose.yml` 파일에 Prometheus와 Grafana 서비스가 설정되어 있습니다.

### 4. Prometheus 설정

`prometheus/prometheus.yml` 파일에 메트릭 수집 설정이 되어 있습니다.

### 5. Grafana 설정

Grafana 대시보드와 데이터 소스 설정이 `grafana` 디렉토리에 있습니다.

## 메트릭 사용 방법

### 1. @Timed 어노테이션 사용

메서드 실행 시간을 측정하려면 `@Timed` 어노테이션을 사용합니다:

```java
@Timed(value = "metrics.example.timed", description = "Time taken to execute timed endpoint")
public String timedExample() {
    // 비즈니스 로직
    return "result";
}
```

### 2. MetricsUtil 클래스 사용

#### 카운터 메트릭

특정 이벤트 발생 횟수를 측정하려면:

```java
metricsUtil.incrementCounter("metrics.example.counter", 
        "method", "methodName", 
        "status", "success");
```

#### 타이머 메트릭

코드 블록 실행 시간을 측정하려면:

```java
String result = metricsUtil.recordTimer("metrics.example.timer",
        () -> {
            // 비즈니스 로직
            return "result";
        },
        "method", "methodName",
        "status", "success");
```

## 모니터링 접속 방법

1. **Prometheus**: http://localhost:9090
2. **Grafana**: http://localhost:3000 (기본 계정: admin/admin)

## 대시보드

기본 제공 대시보드:

- **Spring Boot Metrics**: JVM 메모리, HTTP 요청, 스레드, CPU 사용률 등 기본 메트릭 제공

## 커스텀 메트릭 추가 방법

1. `MetricsUtil` 클래스를 주입받아 사용
2. 중요한 비즈니스 로직에 카운터 또는 타이머 메트릭 추가
3. 필요한 경우 Grafana 대시보드에 새 패널 추가

## 주요 모니터링 지표

- **JVM 메모리 사용량**: 힙/비힙 메모리 사용 현황
- **HTTP 요청 처리량**: 초당 요청 수, 응답 시간
- **스레드 상태**: 활성 스레드 수, 데몬 스레드 수
- **CPU 사용률**: 시스템 및 프로세스 CPU 사용률
- **커스텀 비즈니스 메트릭**: 예약 처리량, 결제 성공률 등

## 문제 해결

- **메트릭이 보이지 않는 경우**: `/actuator/prometheus` 엔드포인트 접근 가능 여부 확인
- **Prometheus 연결 오류**: 네트워크 설정 및 방화벽 확인
- **Grafana 대시보드 로드 실패**: 데이터 소스 연결 상태 확인