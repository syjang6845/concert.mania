package concert.mania.security.service;


import org.springframework.security.core.Authentication;
import concert.mania.concert.domain.model.User;
import concert.mania.concert.domain.model.type.Authority;

import java.time.Duration;

public interface SecurityService {
    /**
     * 현재 인증 정보 조회
     * @return 현재 인증 정보
     */
    Authentication getCurrentAuthentication();

    /**
     * 현재 사용자 ID 조회
     * @return 현재 사용자 ID
     */
    Long getCurrentUserId();

    /**
     * 현재 사용자명 조회
     * @return 현재 사용자명
     */
    String getCurrentUsername();

    /**
     * 현재 사용자 권한 조회
     * @return 현재 사용자 권한
     */
    Authority getCurrentAuthority();

    /**
     * 사용자 도메인으로 인증 객체 생성
     * @param user 사용자 도메인
     * @return 인증 객체
     */
    Authentication createAuthentication(User user);

    /**
     * 사용자 ID와 권한으로 인증 객체 생성
     * @param userId 사용자 ID
     * @param authority 사용자 권한
     * @return 인증 객체
     */
    Authentication createAuthentication(Long userId, Authority authority);

    /**
     * 로그인 인증 객체 생성
     * @param email 이메일
     * @param password 비밀번호
     * @return 인증 객체
     */
    void createAuthenticationWithLogin(String email, String password);

    /**
     * 권한 검증
     * @param id 사용자 ID
     * @param authority 사용자 권한
     * @return 권한 검증 결과
     */
    boolean authorize(Long id, Authority authority);

    /**
     * 인증 상태 확인
     * @return 인증 상태
     */
    boolean isAuthenticated();

    /**
     * 현재 Access Token JTI 조회
     * @return Access Token JTI
     */
    String getCurrentAccessTokenJti();

    /**
     * 현재 Access Token 남은 만료시간 조회
     * @return Access Token 남은 만료시간
     */
    Duration getCurrentAccessTokenRemainingTtl();

    /**
     * 현재 Access Token 조회
     * @return Access Token
     */
    String getCurrentAccessToken();
}
