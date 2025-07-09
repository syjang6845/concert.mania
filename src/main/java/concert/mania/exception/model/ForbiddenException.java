package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 403 Forbidden 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>권한이 없는 리소스 접근 시도</li>
 *   <li>관리자 권한이 필요한 기능 접근</li>
 *   <li>정지된 사용자의 접근</li>
 *   <li>소유자가 아닌 리소스 수정 시도</li>
 *   <li>특정 상태에서 허용되지 않는 작업</li>
 * </ul>
 *
 * <h3>관련 ErrorCode:</h3>
 * <ul>
 *   <li>FORBIDDEN - 기본 권한 없음</li>
 *   <li>USER_SUSPENDED - 정지된 회원</li>
 *   <li>CLASS_STATUS_UPDATE_FORBIDDEN - 특정 상태 변경 금지</li>
 * </ul>
 *
 * @author concert.mania-team
 * @since 1.0
 */
public class ForbiddenException extends ApplicationException {

    /**
     * 기본 생성자 - FORBIDDEN ErrorCode 사용
     */
    public ForbiddenException() {
        super(FORBIDDEN);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode 권한 관련 구체적인 ErrorCode
     */
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public ForbiddenException(String message) {
        super(message, FORBIDDEN);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode 권한 관련 구체적인 ErrorCode
     */
    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
