package concert.mania.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 토큰 관련 추가 에러 코드
    MISSING_TOKEN("토큰이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_SIGNATURE("잘못된 토큰 서명입니다.", HttpStatus.UNAUTHORIZED),
    MALFORMED_TOKEN("형식이 잘못된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("지원되지 않는 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_RESET_TOKEN("유효하지 않은 패스워드 재설정 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_IN_REDIS("토큰이 서버에 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_PROCESSING_ERROR("토큰 처리 중 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED),

    // === AbstractAuthFilter 관련 새로운 에러 코드 ===
    INVALID_USER_TOKEN("유효하지 않은 사용자 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ADMIN_TOKEN("유효하지 않은 관리자 토큰입니다.", HttpStatus.UNAUTHORIZED),
    USER_TOKEN_EXPIRED("사용자 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    ADMIN_TOKEN_EXPIRED("관리자 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_HASH_VALIDATION_FAILED("토큰 해시 검증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    REDIS_TOKEN_NOT_FOUND("Redis에서 토큰을 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_AUTHORITY("유효하지 않은 권한입니다.", HttpStatus.FORBIDDEN),
    TOKEN_AUTHORITY_MISMATCH("토큰의 권한이 일치하지 않습니다.", HttpStatus.FORBIDDEN),
    USER_ID_EXTRACTION_FAILED("토큰에서 사용자 ID 추출에 실패했습니다.", HttpStatus.BAD_REQUEST),
    AUTHORITY_EXTRACTION_FAILED("토큰에서 권한 추출에 실패했습니다.", HttpStatus.BAD_REQUEST),


    // === 기본 HTTP 상태 ===
    INTERNAL_SERVER_ERROR("서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("요청을 수행할 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("존재하지 않는 엔티티입니다.", HttpStatus.NOT_FOUND),
    CONFLICT("리소스 충돌이 발생했습니다.", HttpStatus.CONFLICT),
    UNPROCESSABLE_ENTITY("요청은 올바르지만 처리할 수 없습니다.", HttpStatus.UNPROCESSABLE_ENTITY), // 422 추가
    TOO_MANY_REQUESTS("요청이 너무 많습니다.", HttpStatus.TOO_MANY_REQUESTS),
    REQUEST_TIMEOUT("요청 시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT),
    PAYLOAD_TOO_LARGE("요청 데이터가 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE),

    // === 사용자 관련 ===
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    USER_WITHDRAWN("탈퇴한 회원입니다.", HttpStatus.CONFLICT),
    USER_DELETE_REQUESTED("이미 탈퇴 요청한 계정입니다.", HttpStatus.CONFLICT),
    USER_SUSPENDED("정지된 회원입니다.", HttpStatus.FORBIDDEN),
    DUPLICATE_EMAIL("이미 사용중인 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATE_CLASSROOM_NAME("이미 존재하는 기관명입니다.", HttpStatus.CONFLICT),
    USER_ACCESS_DENIED("본인의 정보만 조회할 수 있습니다.", HttpStatus.FORBIDDEN),


    // === 인증/로그인 관련 ===
    LOGIN_ID_INVALID("올바르지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST),
    LOGIN_PASSWORD_INVALID("비밀번호를 확인해주세요.", HttpStatus.BAD_REQUEST),
    LOGIN_USER_NOT_FOUND("이메일을 확인해주세요.", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_FAILED("인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD("유효하지 않은 비밀번호입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_INVALID("비밀번호와 비밀번호 확인 값이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    SNS_LOGIN_ONLY("소셜 로그인 회원입니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_FOUND("유효한 Refresh Token을 찾을 수 없습니다. 세션이 만료되었거나 토큰이 변조되었을 수 있습니다. 다시 로그인해 주세요.", HttpStatus.UNAUTHORIZED),
    USER_ROLE_MISMATCH("일치하지 않는 정보입니다.", HttpStatus.BAD_REQUEST),

    //좌석 관련
    ALREADY_SEAT("이미 다른 사용자가 선택한 좌석입니다.", HttpStatus.BAD_REQUEST),

    // === JWT 토큰 관련 ===
    JWT_INVALID("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED("만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_MALFORMED("잘못된 형식의 JWT입니다.", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED("지원되지 않는 JWT 형식입니다.", HttpStatus.UNAUTHORIZED),
    JWT_SIGNATURE_ERROR("JWT 서명 검증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    JWT_MISSING("JWT 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_GENERATION_FAILED("토큰 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_LOGGED_OUT("로그아웃한 사용자입니다.", HttpStatus.UNAUTHORIZED),

    // === 이메일 인증 관련 ===
    EMAIL_AUTH_REQUIRED("이메일 인증이 필요합니다.", HttpStatus.BAD_REQUEST),
    EMAIL_AUTH_INVALID("유효하지 않은 이메일 인증입니다.", HttpStatus.BAD_REQUEST),
    EMAIL_AUTH_EXPIRED("인증 시간이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_AUTH_NOT_FOUND("이메일 인증 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_AUTH_FAILED("이메일 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),

    // === 이메일 전송 관련 ===
    EMAIL_SEND_FAILED("이메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SEND_CLIENT_ERROR("이메일 전송 요청이 잘못되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SEND_SERVER_ERROR("이메일 서버 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
    EMAIL_SERVICE_UNAVAILABLE("이메일 서비스를 사용할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    EMAIL_RESPONSE_PARSING_ERROR("이메일 응답 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // === 암호화 관련 ===
    ENCRYPTION_ERROR("암호화 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DECRYPTION_ERROR("복호화 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // === 본인인증 관련 (올바른 상태코드로 수정) ===
    IDENTITY_AUTH_TOKEN_REQUEST_FAILED("본인인증 토큰 요청에 실패했습니다.", HttpStatus.BAD_GATEWAY), // 502
    IDENTITY_AUTH_VALIDATION_FAILED("본인인증 검증에 실패했습니다.", HttpStatus.UNPROCESSABLE_ENTITY), // 422 (외부 검증 실패)
    IDENTITY_AUTH_SERVICE_TIMEOUT("본인인증 서비스 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT), // 504
    IDENTITY_AUTH_SERVICE_ERROR("본인인증 서비스 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY), // 502 추가
    IDENTITY_AUTH_NOT_FOUND("본인인증 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST), //400

    // === 외부 서비스 관련 ===
    SERVICE_UNAVAILABLE("외부 서비스를 사용할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    GATEWAY_TIMEOUT("외부 서비스 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT),

    // === 비즈니스 로직 관련 ===
    CLASS_STATUS_UPDATE_FORBIDDEN("제작 중인 클래스는 노출 상태를 변경할 수 없습니다.", HttpStatus.FORBIDDEN),

    ;

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(this.name(), this.message);
    }
}