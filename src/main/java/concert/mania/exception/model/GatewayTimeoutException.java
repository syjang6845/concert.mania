package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 504 Gateway Timeout 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>외부 서비스 응답 시간 초과</li>
 *   <li>본인인증 서비스 타임아웃</li>
 *   <li>이메일 전송 서비스 지연</li>
 *   <li>결제 서비스 응답 지연</li>
 *   <li>파일 업로드 타임아웃</li>
 * </ul>
 *
 * <h3>관련 ErrorCode:</h3>
 * <ul>
 *   <li>GATEWAY_TIMEOUT - 기본 게이트웨이 타임아웃</li>
 *   <li>IDENTITY_AUTH_SERVICE_TIMEOUT - 본인인증 타임아웃</li>
 * </ul>
 *
 * @author userservice-team
 * @since 1.0
 */
public class GatewayTimeoutException extends ApplicationException {

    /**
     * 기본 생성자 - GATEWAY_TIMEOUT ErrorCode 사용
     */
    public GatewayTimeoutException() {
        super(GATEWAY_TIMEOUT);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode 타임아웃 관련 구체적인 ErrorCode
     */
    public GatewayTimeoutException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public GatewayTimeoutException(String message) {
        super(message, GATEWAY_TIMEOUT);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode 타임아웃 관련 구체적인 ErrorCode
     */
    public GatewayTimeoutException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}