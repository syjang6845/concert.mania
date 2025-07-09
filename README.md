# Concert Mania

## 1. 프로젝트 개요 및 기술 스택

### 프로젝트 개요
Concert Mania는 콘서트 예매 시스템으로, 사용자가 콘서트를 검색하고 예약할 수 있는 플랫폼입니다. 이 시스템은 대규모 트래픽을 처리할 수 있도록 설계되었으며, 실시간 좌석 예약, 결제 처리, 대기열 관리 등의 기능을 제공합니다.

### 기술 스택
- **백엔드**: Java 21, Spring Boot
- **데이터베이스**: MySQL 8.0
- **캐시**: Redis 7
- **메시지 큐**: RabbitMQ
- **모니터링**: Prometheus, Grafana
- **컨테이너화**: Docker, Docker Compose

## 2. 실행 방법 및 API 사용 가이드

### Docker를 이용한 실행 방법

#### 사전 요구사항
- Docker 및 Docker Compose가 설치되어 있어야 합니다.
- Git이 설치되어 있어야 합니다.

#### 프로젝트 클론 및 빌드
1. 프로젝트를 클론합니다.
   ```bash
   git clone <repository-url>
   cd consert.mania
   ```

2. Gradle을 사용하여 프로젝트를 빌드합니다.
   ```bash
   ./gradlew clean build
   ```

#### Docker 이미지 빌드
Docker 이미지를 빌드합니다.
```bash
docker build -t concert-mania:1.1.0 .
```

#### Docker Compose를 이용한 실행
1. Docker Compose를 사용하여 모든 서비스를 시작합니다.
   ```bash
   docker-compose up -d
   ```

2. 서비스 상태 확인:
   ```bash
   docker-compose ps
   ```

3. 로그 확인:
   ```bash
   docker-compose logs -f
   ```

4. 서비스 중지:
   ```bash
   docker-compose down
   ```

### API 사용 가이드
애플리케이션이 실행되면 다음 URL에서 Swagger UI를 통해 API 문서를 확인할 수 있습니다:
```
http://localhost:8080/swagger-ui.html
```

Swagger UI는 다음과 같은 API 그룹으로 구성되어 있습니다:
- **사용자 API**: 회원가입, 사용자 정보 관리
- **인증 API**: 로그인, 로그아웃, 토큰 갱신
- **콘서트 API**: 콘서트 목록 및 상세 정보 조회
- **좌석 API**: 좌석 조회 및 선택
- **예약 API**: 예약 생성 및 관리
- **결제 API**: 결제 처리
- **대기열 API**: 대기열 관리

주요 API 엔드포인트:
- `/api/v1/concerts`: 콘서트 목록 조회 및 상세 정보
- `/api/v1/users`: 사용자 관리
- `/api/v1/reservations`: 예약 관리
- `/api/v1/payments`: 결제 처리
- `/api/v1/seats`: 좌석 관리

## 3. 데이터베이스 설정 (concert.sql)

프로젝트는 `concert.sql` 파일을 사용하여 MySQL 데이터베이스 스키마를 초기화합니다. 이 파일은 다음과 같은 테이블을 생성합니다:

- `users`: 사용자 정보
- `concerts`: 콘서트 정보
- `seat_grades`: 좌석 등급 정보
- `seats`: 개별 좌석 정보
- `seat_locks`: 좌석 임시 잠금 정보
- `reservations`: 예약 정보
- `reservation_details`: 예약 상세 정보
- `payments`: 결제 정보
- `waiting_queue_entries`: 대기열 정보
- 그 외 이벤트, 알림, 로그 관련 테이블

### concert.sql 활용 방법

Docker Compose를 사용하면 `concert.sql` 파일이 자동으로 MySQL 컨테이너에 마운트되어 데이터베이스가 초기화됩니다. 수동으로 적용하려면 다음 명령어를 사용할 수 있습니다:

```bash
# MySQL 컨테이너에 접속
docker exec -it mysql mysql -u jsy -p

# 비밀번호 입력 (기본값: 1234)

# concert 데이터베이스 선택
USE concert;

# SQL 파일 실행 (컨테이너 외부에서)
docker exec -i mysql mysql -u jsy -p1234 concert < sql/concert.sql
```

## 4. 모니터링 설정 (Prometheus, Grafana)

### Prometheus

Prometheus는 시스템 및 서비스의 메트릭을 수집하고 저장하는 데 사용됩니다. 다음 서비스에서 메트릭을 수집합니다:

- Spring Boot 애플리케이션 (`/actuator/prometheus` 엔드포인트)
- MySQL (mysql-exporter)
- Redis (redis-exporter)
- RabbitMQ (rabbitmq-exporter)

#### Prometheus 접속 방법
```
http://localhost:9090
```

주요 기능:
- 메트릭 조회: `Graph` 탭에서 PromQL을 사용하여 메트릭 쿼리
- 타겟 상태 확인: `Status > Targets`에서 모니터링 대상 상태 확인

### Grafana

Grafana는 Prometheus에서 수집한 메트릭을 시각화하는 데 사용됩니다. 미리 구성된 대시보드를 제공합니다:

- MySQL 메트릭 대시보드
- Redis 메트릭 대시보드
- RabbitMQ 메트릭 대시보드
- Spring Boot 애플리케이션 메트릭 대시보드
- Prometheus 자체 메트릭 대시보드

#### Grafana 접속 방법
```
http://localhost:3000
```

기본 로그인 정보:
- 사용자명: admin
- 비밀번호: admin

#### 대시보드 사용 방법
1. 로그인 후 좌측 메뉴에서 `Dashboards` 선택
2. 원하는 대시보드 선택 (MySQL, Redis, RabbitMQ, Spring Boot 등)
3. 시간 범위 조정, 패널 확대 등 다양한 기능 활용

## 5. 문제 해결 및 유지 관리

### 로그 확인
```bash
# 전체 서비스 로그
docker-compose logs -f

# 특정 서비스 로그 (예: java-app)
docker-compose logs -f java-app
```

### 서비스 재시작
```bash
# 특정 서비스 재시작 (예: java-app)
docker-compose restart java-app

# 모든 서비스 재시작
docker-compose restart
```

### 데이터 백업
```bash
# MySQL 데이터 백업
docker exec mysql mysqldump -u jsy -p1234 concert > backup.sql
```

### 시스템 업데이트
1. 코드 변경 후 새 버전 빌드
   ```bash
   ./gradlew clean build
   ```

2. Docker 이미지 재빌드
   ```bash
   docker build -t concert-mania:latest .
   ```

3. 서비스 업데이트 및 재시작
   ```bash
   docker-compose up -d --build java-app
   ```

## 6. 구현 현황

### 구현된 기능

#### 1. 핵심 도메인 모델
- `User.java` - 사용자 모델
- `Concert.java` - 콘서트 모델
- `Reservation.java` - 예약 모델
- `SeatGrade.java` - 좌석 등급 모델
- 기타 도메인 모델들 (Agreement.java 등)

#### 2. 애플리케이션 서비스
- `UserApplicationService.java` - 사용자 관련 서비스
- `UserAuthApplicationService.java` - 사용자 인증 서비스
- `ConcertApplicationService.java` - 콘서트 관련 서비스
- `ReservationApplicationService.java` - 예약 관련 서비스
- `PaymentApplicationService.java` - 결제 관련 서비스
- `WaitingQueueApplicationService.java` - 대기열 관련 서비스
- `SeatApplicationService.java` - 좌석 관련 서비스

#### 3. 웹 인터페이스 (컨트롤러)
- `ConcertController.java` - 콘서트 관련 API
- `UserController.java` - 사용자 관련 API
- 기타 컨트롤러들

#### 4. 인프라스트럭처 구현
- **데이터베이스 연동**
  - JPA 엔티티 구현 (`UserJpaEntity.java`, `ConcertJpaEntity.java` 등)
  - 매퍼 구현 (`UserMapper.java` 등)
- **메시징 시스템**
  - 메시지 프로듀서 구현 (`PaymentProducer.java`, `WaitingQueueProducer.java`)
  - 메시지 컨슈머 구현 (`PaymentConsumer.java`, `DetailedPaymentSuccessConsumer.java` 등)
- **Redis 캐시 구현**
  - `RedisAccessTokenAdapter.java`
  - `WaitingQueueRedisCommandAdapter.java`
  - 기타 Redis 관련 어댑터들
- **스케줄러 구현**
  - `PaymentScheduler.java`
  - `WaitingQueueScheduler.java`
  - `SeatLockScheduler.java`
- **결제 시스템**
  - `MockPaymentGatewayAdapter.java` - 테스트용 결제 게이트웨이 어댑터

#### 5. 테스트 코드
- `UserApplicationServiceTest.java`
- `UserAuthApplicationServiceTest.java`
- `ConcertManiaApplicationTests.java`

#### 6. 인프라 설정
- Docker 및 Docker Compose 설정
- MySQL 데이터베이스 설정
- Redis 캐시 설정
- RabbitMQ 메시지 큐 설정
- Prometheus 및 Grafana 모니터링 설정

#### 7. API 문서화
- Swagger UI를 통한 API 문서화

#### 8. 대규모 트래픽 처리 기능
- 대기열 시스템 (Redis 기반)
- 좌석 잠금 메커니즘
- 비동기 메시지 처리 (RabbitMQ 기반)
- 스케줄링된 작업 처리

### 미구현된 기능

#### 1. 테스트 코드 확장
- 다음 서비스에 대한 테스트 코드 필요:
  - ConcertApplicationService
  - ReservationApplicationService
  - PaymentApplicationService
  - WaitingQueueApplicationService
  - SeatApplicationService
- 다음 유형의 테스트 추가 필요:
  - 통합 테스트 (Integration Tests)
  - 인프라스트럭처 계층 테스트 (Infrastructure Tests)
  - 컨트롤러 테스트 (Controller Tests)
  - 성능 테스트 (Performance Tests)

#### 2. 결제 시스템 확장
- 실제 결제 게이트웨이 연동 (PG사 연동)
- 결제 실패 시나리오 처리
- 결제 취소 및 환불 프로세스

#### 3. 외부 시스템 연동
- 이메일/SMS 알림 서비스
- 외부 인증 서비스 (OAuth 등)
- 외부 API 연동 (공연 정보 제공 API 등)

#### 4. 보안 기능 강화
- 사용자 권한 관리 시스템 강화
- API 요청 제한 (Rate Limiting)
- 보안 취약점 대응 (XSS, CSRF 등)
- 민감 정보 암호화 강화

#### 5. 모니터링 및 로깅 시스템 개선
- 사용자 행동 분석 로깅
- 에러 추적 및 알림 시스템
- 성능 병목 현상 모니터링
- 비즈니스 메트릭 대시보드

#### 6. 대규모 트래픽 처리 최적화
- 데이터베이스 샤딩 전략
- 캐시 전략 최적화
- 분산 시스템 확장성 개선
- 장애 복구 메커니즘

#### 7. 사용자 경험 개선
- 실시간 좌석 상태 업데이트
- 개인화된 추천 시스템
- 사용자 피드백 시스템
- 다국어 지원

#### 8. 문서화 개선
- 상세한 API 사용 예제
- 에러 코드 및 처리 방법 문서화
- 개발자 가이드 확장
- 시스템 아키텍처 다이어그램
