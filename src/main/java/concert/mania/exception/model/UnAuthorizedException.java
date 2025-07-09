package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 401 Unauthorized 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>인증되지 않은 사용자의 접근 시도</li>
 *   <li>JWT 토큰 만료/무효</li>
 *   <li>로그인이 필요한 기능 접근</li>
 *   <li>잘못된 인증 정보</li>
 *   <li>토큰 서명 검증 실패</li>
 * </ul>
 *
 * <h3>관련 ErrorCode:</h3>
 * <ul>
 *   <li>UNAUTHORIZED - 기본 인증 실패</li>
 *   <li>JWT_EXPIRED - JWT 토큰 만료</li>
 *   <li>JWT_INVALID - 유효하지 않은 토큰</li>
 *   <li>AUTHENTICATION_FAILED - 인증 실패</li>
 * </ul>
 *
 * @author concert.mania-team
 * @since 1.0
 */
public class UnAuthorizedException extends ApplicationException {

    /**
     * 기본 생성자 - UNAUTHORIZED ErrorCode 사용
     */
    public UnAuthorizedException() {
        super(UNAUTHORIZED);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode JWT 관련, 인증 관련 구체적인 ErrorCode
     */
    public UnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public UnAuthorizedException(String message) {
        super(message, UNAUTHORIZED);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode JWT, 인증 관련 구체적인 ErrorCode
     */
    public UnAuthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
