package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 400 Bad Request 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>잘못된 요청 데이터 (유효성 검증 실패)</li>
 *   <li>필수 파라미터 누락</li>
 *   <li>잘못된 데이터 형식</li>
 *   <li>이메일 인증 관련 오류</li>
 *   <li>비밀번호 불일치</li>
 * </ul>
 *
 * <h3>생성자 패턴:</h3>
 * <pre>
 * throw new BadRequestException();                                    // 기본 BAD_REQUEST 사용
 * throw new BadRequestException(ErrorCode.EMAIL_AUTH_INVALID);        // 특정 ErrorCode 지정
 * throw new BadRequestException("사용자 정의 메시지");                    // 커스텀 메시지
 * throw new BadRequestException("메시지", ErrorCode.PASSWORD_MISMATCH); // 메시지 + ErrorCode
 * </pre>
 *
 * @author userservice-team
 * @since 1.0
 */
public class BadRequestException extends ApplicationException {

    /**
     * 기본 생성자 - BAD_REQUEST ErrorCode 사용
     * 일반적인 잘못된 요청에 사용
     */
    public BadRequestException() {
        super(BAD_REQUEST);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode 구체적인 에러 상황에 맞는 ErrorCode
     */
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자 (기본 BAD_REQUEST ErrorCode)
     *
     * @param message 사용자 정의 에러 메시지
     */
    public BadRequestException(String message) {
        super(message, BAD_REQUEST);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode 구체적인 ErrorCode
     */
    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}