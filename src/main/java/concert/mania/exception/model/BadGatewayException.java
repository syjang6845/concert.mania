
package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 502 Bad Gateway 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>업스트림 서버로부터 잘못된 응답</li>
 *   <li>프록시/게이트웨이 서버 오류</li>
 *   <li>이메일 전송 서비스 서버 오류</li>
 *   <li>외부 API 서버 장애</li>
 *   <li>로드밸런서 뒤의 서버 오류</li>
 * </ul>
 *
 * <h3>관련 ErrorCode:</h3>
 * <ul>
 *   <li>EMAIL_SEND_SERVER_ERROR - 이메일 서버 오류 (기본값)</li>
 *   <li>SERVICE_UNAVAILABLE - 일반적인 서비스 불가</li>
 * </ul>
 *
 * @author userservice-team
 * @since 1.0
 */
public class BadGatewayException extends ApplicationException {

    /**
     * 기본 생성자 - EMAIL_SEND_SERVER_ERROR ErrorCode 사용
     * 가장 일반적인 502 오류 케이스인 이메일 서버 오류를 기본값으로 설정
     */
    public BadGatewayException() {
        super(EMAIL_SEND_SERVER_ERROR);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode Gateway 오류 관련 구체적인 ErrorCode
     */
    public BadGatewayException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public BadGatewayException(String message) {
        super(message, EMAIL_SEND_SERVER_ERROR);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode Gateway 오류 관련 구체적인 ErrorCode
     */
    public BadGatewayException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    
}