package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 500 Internal Server Error를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>예상치 못한 서버 내부 오류</li>
 *   <li>데이터베이스 연결 실패</li>
 *   <li>외부 API 호출 실패</li>
 *   <li>암호화/복호화 오류</li>
 *   <li>JSON 파싱 오류</li>
 *   <li>토큰 생성 실패</li>
 * </ul>
 *
 * <h3>관련 ErrorCode:</h3>
 * <ul>
 *   <li>INTERNAL_SERVER_ERROR - 기본 서버 오류</li>
 *   <li>EMAIL_SEND_FAILED - 이메일 전송 실패</li>
 *   <li>EMAIL_RESPONSE_PARSING_ERROR - 응답 파싱 오류</li>
 *   <li>ENCRYPTION_ERROR - 암호화 오류</li>
 *   <li>DECRYPTION_ERROR - 복호화 오류</li>
 *   <li>TOKEN_GENERATION_FAILED - 토큰 생성 실패</li>
 * </ul>
 *
 * @author userservice-team
 * @since 1.0
 */
public class InternalServerErrorException extends ApplicationException {

    /**
     * 기본 생성자 - INTERNAL_SERVER_ERROR ErrorCode 사용
     */
    public InternalServerErrorException() {
        super(INTERNAL_SERVER_ERROR);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode 서버 오류 유형별 구체적인 ErrorCode
     */
    public InternalServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public InternalServerErrorException(String message) {
        super(message, INTERNAL_SERVER_ERROR);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode 서버 오류 유형별 구체적인 ErrorCode
     */
    public InternalServerErrorException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}