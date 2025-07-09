package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 429 Too Many Requests 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>API 요청 한도 초과</li>
 *   <li>Rate Limiting 적용</li>
 *   <li>이메일 인증 요청 과다</li>
 *   <li>비밀번호 재설정 요청 과다</li>
 *   <li>동일 IP에서 과도한 요청</li>
 * </ul>
 *
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>1분에 5번 이상 이메일 인증 요청</li>
 *   <li>시간당 API 호출 한도 초과</li>
 *   <li>스팸 방지를 위한 요청 제한</li>
 * </ul>
 *
 * @author userservice-team
 * @since 1.0
 */
public class TooManyRequestsException extends ApplicationException {

    /**
     * 기본 생성자 - TOO_MANY_REQUESTS ErrorCode 사용
     */
    public TooManyRequestsException() {
        super(TOO_MANY_REQUESTS);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode Rate Limiting 관련 구체적인 ErrorCode
     */
    public TooManyRequestsException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public TooManyRequestsException(String message) {
        super(message, TOO_MANY_REQUESTS);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode Rate Limiting 관련 구체적인 ErrorCode
     */
    public TooManyRequestsException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}