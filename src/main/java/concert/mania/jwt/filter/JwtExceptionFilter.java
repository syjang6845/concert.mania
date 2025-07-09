package concert.mania.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import concert.mania.exception.model.ApplicationException;
import concert.mania.exception.model.ErrorCode;
import concert.mania.exception.model.ErrorResponse;

import java.io.IOException;

import static concert.mania.exception.model.ErrorCode.*;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            filterChain.doFilter(request, response);
        } catch (ApplicationException e){
            log.error("error", e);
            ErrorCode error = e.getErrorCode();
            response.setStatus(error.getHttpStatus().value());
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String errorMessage = objectMapper.writeValueAsString(ErrorResponse.of(error));
            response.getWriter().write(errorMessage);
        }
        catch (RequestRejectedException e) {
            log.error("요청 거절 오류");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json;");
            response.setCharacterEncoding("UTF-8");

            ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
