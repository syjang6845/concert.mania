package concert.mania.concert.domain.service;


import jakarta.servlet.http.HttpServletResponse;
import concert.mania.concert.domain.model.type.Authority;

public interface UserAuthDomainService {
    void performLogout(Long userId, Authority authority, HttpServletResponse response);
}
