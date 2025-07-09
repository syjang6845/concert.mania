package concert.mania.concert.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import concert.mania.security.model.CustomUserDetails;
import concert.mania.concert.application.port.out.query.UserQueryPort;
import concert.mania.concert.domain.model.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserQueryPort userQueryPort;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("사용자 정보 조회 시작 - email: {}", email);

        try {
            // 1. 사용자 존재 여부 검증
            User user = userQueryPort.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("존재하지 않는 사용자 - email: {}", email);
                        return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
                    });

            log.debug("사용자 정보 조회 완료 - userId: {}, email: {}", user.getId(), email);

            // 2. 계정 상태 검증을 위한 CustomUserDetails 생성
            return new CustomUserDetails(
                    user.getId(),
                    user.getPassword(),
                    user.getAuthority().name()
            );

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 예상치 못한 오류 발생 - email: {}", email, e);
            throw new UsernameNotFoundException("사용자 정보 조회 실패", e);
        }
    }
}
