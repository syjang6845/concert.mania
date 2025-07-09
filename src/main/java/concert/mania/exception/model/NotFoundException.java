package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 404 Not Found 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>존재하지 않는 사용자 조회</li>
 *   <li>존재하지 않는 리소스 접근</li>
 *   <li>삭제된 엔티티 접근 시도</li>
 *   <li>잘못된 ID로 조회</li>
 *   <li>이메일 인증 정보 미존재</li>
 * </ul>
 *
 * <h3>관련 ErrorCode:</h3>
 * <ul>
 *   <li>NOT_FOUND - 기본 리소스 없음</li>
 * </ul>
 *
 * @author userservice-team
 * @since 1.0
 */
public class NotFoundException extends ApplicationException {

    /**
     * 기본 생성자 - NOT_FOUND ErrorCode 사용
     */
    public NotFoundException() {
        super(NOT_FOUND);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode 리소스별 구체적인 ErrorCode
     */
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public NotFoundException(String message) {
        super(message, NOT_FOUND);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode 리소스별 구체적인 ErrorCode
     */
    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}