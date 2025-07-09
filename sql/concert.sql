-- ====================================================================
-- 콘서트 매니아 (Concert Mania) 데이터베이스 스키마
-- 설명: 콘서트 예매 시스템의 모든 테이블 생성 쿼리
-- 작성일: 2024-12-31
-- 버전: 1.0
-- ====================================================================

-- 데이터베이스 생성 (UTF-8 유니코드 지원)
CREATE DATABASE IF NOT EXISTS concert
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE concert;

-- ====================================================================
-- 1. 사용자 테이블 (users)
-- 설명: 시스템에 등록된 사용자 정보를 저장하는 테이블
-- 참고: 인증 및 권한 관리를 위한 기본 사용자 정보
-- ====================================================================
CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 고유 식별자 (PK)',
                                     email VARCHAR(256) NOT NULL UNIQUE COMMENT '사용자 이메일 (로그인 ID, 유니크)',
                                     name VARCHAR(64) NOT NULL COMMENT '사용자 이름',
                                     password VARCHAR(256) NOT NULL COMMENT '암호화된 비밀번호',
                                     role ENUM('ADMIN', 'USER') NULL COMMENT '사용자 역할 (USER, ADMIN)',
                                     withdraw VARCHAR(1) NOT NULL CHECK (withdraw IN ('N', 'Y')) COMMENT '탈퇴 여부 (N: 정상, Y: 탈퇴)',
                                     created_at DATETIME(6) NULL COMMENT '계정 생성일시',
                                     updated_at DATETIME(6) NULL COMMENT '계정 정보 수정일시'
) COMMENT '사용자 정보 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 2. 콘서트 테이블 (concerts)
-- 설명: 콘서트 정보를 저장하는 마스터 테이블
-- 참고: 콘서트 기본 정보, 예매 기간, 활성화 상태 관리
-- ====================================================================
CREATE TABLE IF NOT EXISTS concerts (
                                        concert_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '콘서트 고유 식별자 (PK)',
                                        title VARCHAR(255) NOT NULL COMMENT '콘서트 제목',
                                        description TEXT NOT NULL COMMENT '콘서트 상세 설명',
                                        start_date_time DATETIME(6) NOT NULL COMMENT '콘서트 시작 일시',
                                        end_date_time DATETIME(6) NOT NULL COMMENT '콘서트 종료 일시',
                                        venue VARCHAR(255) NOT NULL COMMENT '콘서트 개최 장소명',
                                        venue_address VARCHAR(255) NOT NULL COMMENT '콘서트 개최 장소 주소',
                                        reservation_open_date_time DATETIME(6) NOT NULL COMMENT '예매 오픈 일시',
                                        reservation_close_date_time DATETIME(6) NOT NULL COMMENT '예매 마감 일시',
                                        is_active CHAR(1) NOT NULL COMMENT '콘서트 활성화 상태 (Y/N)',
                                        created_at DATETIME(6) NULL COMMENT '콘서트 등록일시',
                                        updated_at DATETIME(6) NULL COMMENT '콘서트 정보 수정일시',

    -- 인덱스 설정
                                        INDEX idx_concert_is_active (is_active) COMMENT '활성 콘서트 조회 최적화',
                                        INDEX idx_concert_reservation_open (reservation_open_date_time) COMMENT '예매 오픈 일시 검색 최적화'
) COMMENT '콘서트 정보 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 3. 좌석 등급 테이블 (seat_grades)
-- 설명: 콘서트별 좌석 등급 정보를 저장하는 테이블
-- 참고: VIP, R석, S석, A석 등 좌석 등급별 가격 및 좌석 수 관리
-- ====================================================================
CREATE TABLE IF NOT EXISTS seat_grades (
                                           seat_grade_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '좌석 등급 고유 식별자 (PK)',
                                           concert_id BIGINT NOT NULL COMMENT '콘서트 ID (FK)',
                                           name VARCHAR(50) NOT NULL COMMENT '좌석 등급명 (VIP, R석, S석, A석 등)',
                                           price DECIMAL(10, 2) NOT NULL COMMENT '좌석 등급별 가격',
                                           capacity INT NOT NULL COMMENT '해당 등급 총 좌석 수',
                                           description TEXT NULL COMMENT '좌석 등급 설명',
                                           created_at DATETIME(6) NULL COMMENT '좌석 등급 등록일시',
                                           updated_at DATETIME(6) NULL COMMENT '좌석 등급 정보 수정일시',

    -- 외래키 제약조건
                                           CONSTRAINT FKipnrcpa3ruml3xcjtfsgfjvb5 FOREIGN KEY (concert_id) REFERENCES concerts(concert_id) ON DELETE CASCADE
) COMMENT '좌석 등급 정보 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 4. 좌석 테이블 (seats)
-- 설명: 콘서트별 개별 좌석 정보를 저장하는 테이블
-- 참고: 좌석 위치, 상태 관리 (예매 가능, 예매 완료, 임시 선택 등)
-- ====================================================================
CREATE TABLE IF NOT EXISTS seats (
                                     seat_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '좌석 고유 식별자 (PK)',
                                     concert_id BIGINT NOT NULL COMMENT '콘서트 ID (FK)',
                                     seat_grade_id BIGINT NOT NULL COMMENT '좌석 등급 ID (FK)',
                                     seat_row INT NOT NULL COMMENT '좌석 행 번호',
                                     seat_col INT NOT NULL COMMENT '좌석 열 번호',
                                     seat_number VARCHAR(10) NOT NULL COMMENT '좌석 번호 (A1, B2 등)',
                                     status VARCHAR(20) DEFAULT 'AVAILABLE' NOT NULL COMMENT '좌석 상태 (AVAILABLE: 예매가능, RESERVED: 예매완료, LOCKED: 임시선택)',
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL COMMENT '좌석 등록일시',
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '좌석 상태 수정일시',

    -- 외래키 제약조건
                                     CONSTRAINT seats_ibfk_1 FOREIGN KEY (concert_id) REFERENCES concerts(concert_id) ON DELETE CASCADE,
                                     CONSTRAINT seats_ibfk_2 FOREIGN KEY (seat_grade_id) REFERENCES seat_grades(seat_grade_id) ON DELETE CASCADE,

    -- 유니크 제약조건
                                     CONSTRAINT uk_seat_position UNIQUE (concert_id, seat_row, seat_col) COMMENT '동일 콘서트 내 좌석 위치 중복 방지',

    -- 인덱스 설정
                                     INDEX idx_seat_concert (concert_id) COMMENT '콘서트별 좌석 조회 최적화',
                                     INDEX idx_seat_grade (seat_grade_id) COMMENT '좌석 등급별 조회 최적화',
                                     INDEX idx_seat_row_col (seat_row, seat_col) COMMENT '좌석 위치 검색 최적화',
                                     INDEX idx_seat_status (status) COMMENT '좌석 상태별 조회 최적화'
) COMMENT '좌석 정보 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 5. 좌석 잠금 테이블 (seat_locks)
-- 설명: 예매 과정에서 좌석 임시 선택(잠금) 상태를 관리하는 테이블
-- 참고: 다른 사용자가 동시에 같은 좌석을 선택하는 것을 방지
-- ====================================================================
CREATE TABLE IF NOT EXISTS seat_locks (
                                          seat_lock_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '좌석 잠금 고유 식별자 (PK)',
                                          seat_id BIGINT NOT NULL COMMENT '잠금된 좌석 ID (FK)',
                                          user_id BIGINT NOT NULL COMMENT '좌석을 잠금한 사용자 ID (FK)',
                                          locked_at DATETIME(6) NOT NULL COMMENT '좌석 잠금 시작 시간',
                                          expires_at DATETIME(6) NOT NULL COMMENT '좌석 잠금 만료 시간 (일반적으로 5-10분)',
                                          created_at DATETIME(6) NULL COMMENT '잠금 레코드 생성일시',
                                          updated_at DATETIME(6) NULL COMMENT '잠금 상태 수정일시',

    -- 유니크 제약조건
                                          CONSTRAINT UKp4oo7sjypbuj1xjo337affrxm UNIQUE (seat_id),

    -- 인덱스 설정
                                          INDEX idx_seat_lock_expires_at (expires_at) COMMENT '만료된 잠금 정리 최적화'
) COMMENT '좌석 임시 잠금 관리 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 6. 예약 테이블 (reservations)
-- 설명: 사용자의 콘서트 예약 정보를 저장하는 마스터 테이블
-- 참고: 예약 상태, 총 금액, 예약 번호 관리
-- ====================================================================
CREATE TABLE IF NOT EXISTS reservations (
                                            reservation_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '예약 고유 식별자 (PK)',
                                            user_id BIGINT NOT NULL COMMENT '예약한 사용자 ID (FK)',
                                            concert_id BIGINT NOT NULL COMMENT '예약된 콘서트 ID (FK)',
                                            reservation_number VARCHAR(50) NOT NULL COMMENT '예약 번호',
                                            total_amount DECIMAL(10, 2) NOT NULL COMMENT '총 예약 금액',
                                            status ENUM('CANCELLED', 'COMPLETED', 'PENDING') NOT NULL COMMENT '예약 상태 (PENDING: 대기중, COMPLETED: 확정, CANCELLED: 취소)',
                                            cancelled_at DATETIME(6) NULL COMMENT '예약 취소 시간',
                                            completed_at DATETIME(6) NULL COMMENT '예약 완료 시간',
                                            created_at DATETIME(6) NULL COMMENT '예약 레코드 생성일시',
                                            updated_at DATETIME(6) NULL COMMENT '예약 상태 수정일시',

    -- 외래키 제약조건
                                            CONSTRAINT FK74j42bs8j7kyknnfvf69duu3j FOREIGN KEY (concert_id) REFERENCES concerts(concert_id),

    -- 유니크 제약조건
                                            CONSTRAINT UKha0pvynkykm6kk26japu4r5rf UNIQUE (reservation_number),

    -- 인덱스 설정
                                            INDEX idx_reservation_user_id (user_id) COMMENT '사용자별 예약 조회 최적화',
                                            INDEX idx_reservation_concert_id (concert_id) COMMENT '콘서트별 예약 조회 최적화',
                                            INDEX idx_reservation_status (status) COMMENT '예약 상태별 조회 최적화'
) COMMENT '예약 정보 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 7. 예약 상세 테이블 (reservation_details)
-- 설명: 예약에 포함된 개별 좌석 정보를 저장하는 테이블
-- 참고: 한 번의 예약에 여러 좌석이 포함될 수 있음
-- ====================================================================
CREATE TABLE IF NOT EXISTS reservation_details (
                                                   reservation_detail_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '예약 상세 고유 식별자 (PK)',
                                                   reservation_id BIGINT NOT NULL COMMENT '예약 ID (FK)',
                                                   seat_id BIGINT NOT NULL COMMENT '예약된 좌석 ID (FK)',
                                                   price DECIMAL(10, 2) NOT NULL COMMENT '해당 좌석의 예약 가격',
                                                   created_at DATETIME(6) NULL COMMENT '예약 상세 레코드 생성일시',
                                                   updated_at DATETIME(6) NULL COMMENT '예약 상세 수정일시',

    -- 외래키 제약조건
                                                   CONSTRAINT FKof2r4wnui3hs7bl1h810wck7n FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),

    -- 유니크 제약조건
                                                   CONSTRAINT UKrvl6wvo96jhqsyve21chn8oyc UNIQUE (seat_id)
) COMMENT '예약 상세 정보 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 8. 결제 테이블 (payments)
-- 설명: 예약에 대한 결제 정보를 저장하는 테이블
-- 참고: 결제 방법, 상태, 외부 결제 시스템 연동 정보 관리
-- ====================================================================
CREATE TABLE IF NOT EXISTS payments (
                                        payment_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '결제 고유 식별자 (PK)',
                                        reservation_id BIGINT NOT NULL COMMENT '결제 대상 예약 ID (FK)',
                                        amount DECIMAL(10, 2) NOT NULL COMMENT '결제 금액',
                                        method ENUM('BANK_TRANSFER', 'CREDIT_CARD', 'MOBILE') NOT NULL COMMENT '결제 방법 (CREDIT_CARD: 신용카드, BANK_TRANSFER: 계좌이체, MOBILE: 휴대폰)',
                                        status ENUM('CANCELLED', 'COMPLETED', 'FAILED', 'PENDING') NOT NULL COMMENT '결제 상태 (PENDING: 대기, COMPLETED: 성공, FAILED: 실패, CANCELLED: 취소)',
                                        external_payment_id VARCHAR(100) NOT NULL COMMENT '외부 결제 시스템 거래 ID',
                                        payment_details TEXT NULL COMMENT '결제 상세 정보',
                                        cancelled_at DATETIME(6) NULL COMMENT '결제 취소 시간',
                                        completed_at DATETIME(6) NULL COMMENT '결제 완료 시간',
                                        created_at DATETIME(6) NULL COMMENT '결제 레코드 생성일시',
                                        updated_at DATETIME(6) NULL COMMENT '결제 상태 수정일시',

    -- 외래키 제약조건
                                        CONSTRAINT FKp8yh4sjt3u0g6aru1oxfh3o14 FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),

    -- 유니크 제약조건
                                        CONSTRAINT UK13u8lknt38y8auyod5fh9oopx UNIQUE (external_payment_id),
                                        CONSTRAINT UKe7qdxh4fch1yfisduker8j6w2 UNIQUE (reservation_id),

    -- 인덱스 설정
                                        INDEX idx_payment_reservation_id (reservation_id) COMMENT '예약별 결제 조회 최적화',
                                        INDEX idx_payment_status (status) COMMENT '결제 상태별 조회 최적화'
) COMMENT '결제 정보 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 9. 대기열 엔트리 테이블 (waiting_queue_entries)
-- 설명: 콘서트 예매 대기열 시스템을 위한 테이블
-- 참고: 대량 접속 시 순서대로 예매 페이지 접근 권한 관리
-- ====================================================================
CREATE TABLE IF NOT EXISTS waiting_queue_entries (
                                                     waiting_queue_entry_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '대기열 엔트리 고유 식별자 (PK)',
                                                     user_id BIGINT NOT NULL COMMENT '대기열에 등록된 사용자 ID (FK)',
                                                     concert_id BIGINT NOT NULL COMMENT '대기열 대상 콘서트 ID (FK)',
                                                     queue_position INT NOT NULL COMMENT '대기열 순서 (1부터 시작)',
                                                     status ENUM('ADMITTED', 'CANCELLED', 'EXPIRED', 'WAITING') NOT NULL COMMENT '대기열 상태 (WAITING: 대기, ADMITTED: 활성, EXPIRED: 만료, CANCELLED: 취소)',
                                                     entered_at DATETIME(6) NOT NULL COMMENT '대기열 진입 시간',
                                                     admitted_at DATETIME(6) NULL COMMENT '예매 페이지 활성화 시간',
                                                     created_at DATETIME(6) NULL COMMENT '대기열 엔트리 생성일시',
                                                     updated_at DATETIME(6) NULL COMMENT '대기열 상태 수정일시',

    -- 외래키 제약조건
                                                     CONSTRAINT FKfoweeq97voe3jc1dhsx1xkkn2 FOREIGN KEY (concert_id) REFERENCES concerts(concert_id),

    -- 인덱스 설정
                                                     INDEX idx_waiting_queue_user_id (user_id) COMMENT '사용자별 대기열 조회 최적화',
                                                     INDEX idx_waiting_queue_concert_id (concert_id) COMMENT '콘서트별 대기열 조회 최적화',
                                                     INDEX idx_waiting_queue_status (status) COMMENT '대기열 상태별 조회 최적화'
) COMMENT '대기열 관리 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 10. 이벤트 테이블 (events)
-- 설명: 시스템 내 발생하는 비즈니스 이벤트를 저장하는 테이블
-- 참고: 이벤트 소싱, 비동기 처리, 알림 발송 등을 위한 이벤트 저장소
-- ====================================================================
CREATE TABLE IF NOT EXISTS events (
                                      event_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이벤트 고유 식별자 (PK)',
                                      event_type VARCHAR(100) NOT NULL COMMENT '이벤트 유형 (RESERVATION_COMPLETED, PAYMENT_COMPLETED, CONCERT_CREATED 등)',
                                      payload TEXT NOT NULL COMMENT '이벤트 데이터 (JSON 형태로 저장)',
                                      processed_at DATETIME(6) NULL COMMENT '이벤트 처리 완료 시간',
                                      status ENUM('FAILED', 'PENDING', 'PROCESSED') NOT NULL COMMENT '이벤트 상태 (PENDING: 대기, PROCESSED: 처리완료, FAILED: 실패)',
                                      created_at DATETIME(6) NULL COMMENT '이벤트 생성일시',
                                      updated_at DATETIME(6) NULL COMMENT '이벤트 상태 수정일시',

    -- 인덱스 설정
                                      INDEX idx_event_type (event_type) COMMENT '이벤트 유형별 조회 최적화',
                                      INDEX idx_event_status (status) COMMENT '이벤트 상태별 조회 최적화'
) COMMENT '비즈니스 이벤트 저장 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 11. 알림 테이블 (notifications)
-- 설명: 사용자에게 발송되는 알림 정보를 저장하는 테이블
-- 참고: 이메일, SMS, 푸시 알림 등 다양한 알림 채널 지원
-- ====================================================================
CREATE TABLE IF NOT EXISTS notifications (
                                             notification_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '알림 고유 식별자 (PK)',
                                             user_id BIGINT NOT NULL COMMENT '알림 수신자 ID (FK)',
                                             type VARCHAR(20) NOT NULL COMMENT '알림 유형 (EMAIL: 이메일, SMS: 문자, PUSH: 푸시)',
                                             title VARCHAR(255) NOT NULL COMMENT '알림 제목',
                                             content TEXT NOT NULL COMMENT '알림 내용',
                                             sent_at DATETIME(6) NULL COMMENT '알림 발송 시간',
                                             status ENUM('FAILED', 'PENDING', 'SENT') NOT NULL COMMENT '알림 상태 (PENDING: 대기, SENT: 발송완료, FAILED: 발송실패)',
                                             created_at DATETIME(6) NULL COMMENT '알림 레코드 생성일시',
                                             updated_at DATETIME(6) NULL COMMENT '알림 상태 수정일시'
) COMMENT '알림 정보 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 12. 시스템 로그 테이블 (system_logs)
-- 설명: 시스템 레벨의 로그 정보를 저장하는 테이블
-- 참고: 애플리케이션 오류, 성능 이슈, 시스템 상태 모니터링
-- ====================================================================
CREATE TABLE IF NOT EXISTS system_logs (
                                           log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '시스템 로그 고유 식별자 (PK)',
                                           log_level ENUM('DEBUG', 'ERROR', 'INFO', 'WARN') NOT NULL COMMENT '로그 레벨 (DEBUG, INFO, WARN, ERROR)',
                                           logger VARCHAR(255) NOT NULL COMMENT '로거명 (클래스명)',
                                           message TEXT NOT NULL COMMENT '로그 메시지',
                                           stack_trace TEXT NULL COMMENT '스택 트레이스 (에러 시)',
                                           request_uri VARCHAR(255) NULL COMMENT '요청 URI',
                                           user_id VARCHAR(100) NULL COMMENT '관련 사용자 ID',
                                           timestamp DATETIME(6) NOT NULL COMMENT '로그 발생 시간',
                                           created_at DATETIME(6) NULL COMMENT '로그 레코드 생성일시',
                                           updated_at DATETIME(6) NULL COMMENT '로그 수정일시',

    -- 인덱스 설정
                                           INDEX idx_system_log_level (log_level) COMMENT '로그 레벨별 조회 최적화',
                                           INDEX idx_system_log_timestamp (timestamp) COMMENT '로그 발생 시간 순 조회 최적화'
) COMMENT '시스템 로그 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 13. 비즈니스 로그 테이블 (business_logs)
-- 설명: 비즈니스 관련 로그 정보를 저장하는 테이블
-- 참고: 사용자 행동, 비즈니스 이벤트 추적
-- ====================================================================
CREATE TABLE IF NOT EXISTS business_logs (
                                             log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '비즈니스 로그 고유 식별자 (PK)',
                                             action VARCHAR(100) NOT NULL COMMENT '비즈니스 액션 (RESERVATION_CREATED, PAYMENT_COMPLETED 등)',
                                             entity_type VARCHAR(100) NOT NULL COMMENT '관련 엔티티 타입 (User, Concert, Reservation 등)',
                                             entity_id VARCHAR(100) NULL COMMENT '관련 엔티티 ID',
                                             user_id VARCHAR(100) NULL COMMENT '관련 사용자 ID',
                                             details TEXT NOT NULL COMMENT 'JSON 형태로 상세 정보 저장',
                                             timestamp DATETIME(6) NOT NULL COMMENT '로그 생성 시간',
                                             created_at DATETIME(6) NULL COMMENT '생성 시간',
                                             updated_at DATETIME(6) NULL COMMENT '수정 시간',

    -- 인덱스 설정
                                             INDEX idx_business_log_action (action) COMMENT '액션별 조회 최적화',
                                             INDEX idx_business_log_entity_type (entity_type) COMMENT '엔티티 타입별 조회 최적화',
                                             INDEX idx_business_log_timestamp (timestamp) COMMENT '시간별 조회 최적화'
) COMMENT '비즈니스 로그 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 14. 애플리케이션 메트릭 테이블 (application_metrics)
-- 설명: 애플리케이션 성능 메트릭을 저장하는 테이블
-- 참고: 응답시간, 처리량, 에러율 등 모니터링 데이터
-- ====================================================================
CREATE TABLE IF NOT EXISTS application_metrics (
                                                   metric_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '메트릭 고유 식별자 (PK)',
                                                   metric_name VARCHAR(100) NOT NULL COMMENT '메트릭 이름',
                                                   metric_value DOUBLE NOT NULL COMMENT '메트릭 값',
                                                   tags TEXT NULL COMMENT '태그 (JSON 형태)',
                                                   timestamp DATETIME(6) NOT NULL COMMENT '메트릭 생성 시간',
                                                   created_at DATETIME(6) NULL COMMENT '생성 시간',
                                                   updated_at DATETIME(6) NULL COMMENT '수정 시간'
) COMMENT '애플리케이션 메트릭 테이블' CHARSET=utf8mb4;

-- ====================================================================
-- 샘플 데이터 삽입 (선택사항)
-- ====================================================================

-- 관리자 사용자 생성
INSERT INTO users (email, name, password, role, withdraw, created_at, updated_at) VALUES
                                                                                      ('admin@concert.mania', '관리자', '$2a$10$XYZ123...', 'ADMIN', 'N', NOW(6), NOW(6)),
                                                                                      ('user@concert.mania', '일반사용자', '$2a$10$ABC456...', 'USER', 'N', NOW(6), NOW(6));

-- 샘플 콘서트 생성
INSERT INTO concerts (title, description, start_date_time, end_date_time, venue, venue_address,
                      reservation_open_date_time, reservation_close_date_time, is_active, created_at, updated_at) VALUES
    ('2024 신년 콘서트', '새해를 맞이하는 특별한 콘서트', '2024-12-31 20:00:00.000000', '2024-12-31 22:00:00.000000',
     '올림픽공원 SK핸드볼경기장', '서울시 송파구 올림픽로 424',
     '2025-01-01 10:00:00.000000', '2025-12-30 23:59:59.000000', 'Y', NOW(6), NOW(6));

-- 좌석 등급 생성
INSERT INTO seat_grades (concert_id, name, price, capacity, description, created_at, updated_at) VALUES
                                                                                                     (1, 'VIP', 150000.00, 100, 'VIP석 - 최고급 좌석', NOW(6), NOW(6)),
                                                                                                     (1, 'R석', 120000.00, 200, 'R석 - 우수한 시야', NOW(6), NOW(6)),
                                                                                                     (1, 'S석', 90000.00, 300, 'S석 - 일반석', NOW(6), NOW(6)),
                                                                                                     (1, 'A석', 60000.00, 400, 'A석 - 경제적인 선택', NOW(6), NOW(6));


DELIMITER //

CREATE PROCEDURE GenerateSeatsFixed(
    IN p_concert_id BIGINT,
    IN p_seat_grade_id BIGINT,
    IN p_grade_prefix VARCHAR(10),
    IN p_start_row INT,
    IN p_rows INT,
    IN p_cols_per_row INT
)
BEGIN
    DECLARE v_row INT DEFAULT p_start_row;
    DECLARE v_col INT DEFAULT 1;
    DECLARE v_row_letter CHAR(1);
    DECLARE v_seat_number VARCHAR(20);
    DECLARE v_end_row INT DEFAULT p_start_row + p_rows - 1;

    WHILE v_row <= v_end_row DO
            -- 행 문자 생성 (A, B, C...)
            SET v_row_letter = CHAR(64 + (v_row - p_start_row + 1));
            SET v_col = 1;

            WHILE v_col <= p_cols_per_row DO
                    -- 좌석 번호 생성
                    SET v_seat_number = CONCAT(p_grade_prefix, '-', v_row_letter, v_col);

                    INSERT INTO seats (concert_id, seat_grade_id, seat_row, seat_col, seat_number, status, created_at, updated_at)
                    VALUES (p_concert_id, p_seat_grade_id, v_row, v_col, v_seat_number, 'AVAILABLE', NOW(), NOW());

                    SET v_col = v_col + 1;
                END WHILE;

            SET v_row = v_row + 1;
        END WHILE;
END//

DELIMITER ;

-- 겹치지 않는 행 범위로 좌석 생성
CALL GenerateSeatsFixed(1, 1, 'VIP', 1, 10, 10);   -- VIP석: 1-10행
CALL GenerateSeatsFixed(1, 2, 'R', 11, 10, 20);    -- R석: 11-20행
CALL GenerateSeatsFixed(1, 3, 'S', 21, 15, 20);    -- S석: 21-35행
CALL GenerateSeatsFixed(1, 4, 'A', 36, 20, 20);    -- A석: 36-55행
-- 프로시저 삭제 (필요시)
-- DROP PROCEDURE IF EXISTS GenerateSeats;

-- ====================================================================
-- 종료
-- ====================================================================