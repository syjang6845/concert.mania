
package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 503 Service Unavailable 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>서비스 점검 중</li>
 *   <li>서버 과부하로 인한 일시적 서비스 중단</li>
 *   <li>외부 서비스 점검/장애</li>
 *   <li>이메일 서비스 점검</li>
 *   <li>데이터베이스 연결 불가</li>
 * </ul>
 *
 * <h3>관련 ErrorCode:</h3>
 * <ul>
 *   <li>SERVICE_UNAVAILABLE - 기본 서비스 불가</li>
 *   <li>EMAIL_SERVICE_UNAVAILABLE - 이메일 서비스 불가</li>
 * </ul>
 *
 * <h3>특징:</h3>
 * <ul>
 *   <li>일시적인 오류 (나중에 재시도 가능)</li>
 *   <li>Retry-After 헤더와 함께 사용 권장</li>
 * </ul>
 *
 * @author userservice-team
 * @since 1.0
 */
public class ServiceUnavailableException extends ApplicationException {

    /**
     * 기본 생성자 - SERVICE_UNAVAILABLE ErrorCode 사용
     */
    public ServiceUnavailableException() {
        super(SERVICE_UNAVAILABLE);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode 서비스 불가 관련 구체적인 ErrorCode
     */
    public ServiceUnavailableException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public ServiceUnavailableException(String message) {
        super(message, SERVICE_UNAVAILABLE);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode 서비스 불가 관련 구체적인 ErrorCode
     */
    public ServiceUnavailableException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}