
package concert.mania.exception.model;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * HTTP 409 Conflict 에러를 나타내는 예외 클래스
 *
 * <h3>주요 사용 케이스:</h3>
 * <ul>
 *   <li>리소스 상태 충돌</li>
 *   <li>동시성 문제</li>
 *   <li>탈퇴한 회원 접근</li>
 *   <li>이미 처리된 요청 재시도</li>
 *   <li>비즈니스 규칙 위반</li>
 * </ul>
 *
 * <h3>관련 ErrorCode:</h3>
 * <ul>
 *   <li>CONFLICT - 기본 리소스 충돌</li>
 *   <li>USER_WITHDRAWN - 탈퇴한 회원</li>
 *   <li>USER_DELETE_REQUESTED - 탈퇴 요청된 계정</li>
 * </ul>
 *
 * @author userservice-team
 * @since 1.0
 */
public class ConflictException extends ApplicationException {

    /**
     * 기본 생성자 - CONFLICT ErrorCode 사용
     */
    public ConflictException() {
        super(CONFLICT);
    }

    /**
     * 특정 ErrorCode를 지정하는 생성자
     *
     * @param errorCode 충돌 상황별 구체적인 ErrorCode
     */
    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     */
    public ConflictException(String message) {
        super(message, CONFLICT);
    }

    /**
     * 커스텀 메시지와 특정 ErrorCode를 함께 사용하는 생성자
     *
     * @param message 사용자 정의 에러 메시지
     * @param errorCode 충돌 상황별 구체적인 ErrorCode
     */
    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}