
package concert.mania.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import concert.mania.exception.model.*;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static concert.mania.exception.model.ErrorCode.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // === 커스텀 Exception 처리 (구체적 → 일반적 순서) ===

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        log.info("Bad request: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedException(UnAuthorizedException e, HttpServletRequest request) {
        log.warn("Unauthorized access: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
        log.warn("Forbidden access: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        log.info("Resource not found: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e, HttpServletRequest request) {
        log.info("Resource conflict: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }


    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(TooManyRequestsException e, HttpServletRequest request) {
        log.warn("Too many requests: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException e, HttpServletRequest request) {
        log.error("Internal server error: {}", e.getErrorCode().name(), e);
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<ErrorResponse> handleBadGatewayException(BadGatewayException e, HttpServletRequest request) {
        log.error("Bad gateway: {}", e.getErrorCode().name(), e);
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException e, HttpServletRequest request) {
        log.error("Service unavailable: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(GatewayTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleGatewayTimeoutException(GatewayTimeoutException e, HttpServletRequest request) {
        log.error("Gateway timeout: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    // === 유효성 검증 관련 (통합된 ErrorResponse 사용) ===

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.info("Validation failed for {} fields", e.getBindingResult().getFieldErrorCount());

        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .toList();

        ErrorResponse response = ErrorResponse.ofValidation(
                "유효성 검사에 실패했습니다.",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        log.info("Validation failed for {} fields", e.getFieldErrorCount());

        List<ErrorResponse.FieldError> fieldErrors = e.getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .toList();

        ErrorResponse response = ErrorResponse.ofValidation(
                "유효성 검사에 실패했습니다.",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    // === Spring Security 관련 ===

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.warn("Authentication failed - bad credentials");
        ErrorResponse response = ErrorResponse.of(LOGIN_PASSWORD_INVALID, LOGIN_PASSWORD_INVALID.getMessage(), request.getRequestURI());
        return ResponseEntity.status(LOGIN_PASSWORD_INVALID.getHttpStatus()).body(response);
    }

    // === JWT 관련 ===

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException e, HttpServletRequest request) {
        log.warn("JWT error: {}", e.getClass().getSimpleName());

        ErrorCode errorCode = switch (e.getClass().getSimpleName()) {
            case "ExpiredJwtException" -> JWT_EXPIRED;
            case "UnsupportedJwtException" -> JWT_UNSUPPORTED;
            case "MalformedJwtException" -> JWT_MALFORMED;
            case "SignatureException" -> JWT_SIGNATURE_ERROR;
            default -> JWT_INVALID;
        };

        ErrorResponse response = ErrorResponse.of(errorCode, errorCode.getMessage(), request.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // === 데이터베이스 관련 ===

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("Data integrity violation", e);
        ErrorResponse response = ErrorResponse.of(CONFLICT, CONFLICT.getMessage(), request.getRequestURI());
        return ResponseEntity.status(CONFLICT.getHttpStatus()).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.info("JPA entity not found: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(NOT_FOUND, NOT_FOUND.getMessage(), request.getRequestURI());
        return ResponseEntity.status(NOT_FOUND.getHttpStatus()).body(response);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handlePersistenceException(PersistenceException e, HttpServletRequest request) {
        log.error("Database persistence error", e);
        ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getMessage(), request.getRequestURI());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }

    // === 암호화 관련 ===

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        String message = e.getMessage();
        if (message != null && message.contains("decrypt")) {
            log.error("Decryption error occurred");
            ErrorResponse response = ErrorResponse.of(DECRYPTION_ERROR, DECRYPTION_ERROR.getMessage(), request.getRequestURI());
            return ResponseEntity.status(DECRYPTION_ERROR.getHttpStatus()).body(response);
        }
        log.warn("Invalid argument: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(BAD_REQUEST, BAD_REQUEST.getMessage(), request.getRequestURI());
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus()).body(response);
    }

    // === JSON 처리 관련 ===

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.info("=== HTTP Message Not Readable Exception ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("HTTP Method: {}", request.getMethod());
        log.info("Content-Type: {}", request.getContentType());
        log.info("Content-Length: {}", request.getContentLength());
        log.info("Character Encoding: {}", request.getCharacterEncoding());

        // 예외 상세 정보
        log.info("Exception message: {}", e.getMessage());
        log.info("Exception class: {}", e.getClass().getSimpleName());

        // 근본 원인 확인
        Throwable rootCause = e.getRootCause();
        if (rootCause != null) {
            log.info("Root cause: {}", rootCause.getMessage());
            log.info("Root cause class: {}", rootCause.getClass().getSimpleName());
        }

        // 요청 헤더 정보
        log.info("Request Headers:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName ->
                log.info("  {}: {}", headerName, request.getHeader(headerName))
        );


        ErrorResponse response = ErrorResponse.of(BAD_REQUEST, "요청 본문을 읽을 수 없습니다.", request.getRequestURI());
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus()).body(response);
    }

    @ExceptionHandler({JsonProcessingException.class, JsonMappingException.class})
    public ResponseEntity<ErrorResponse> handleJsonException(Exception e, HttpServletRequest request) {
        log.error("JSON processing error: {}", e.getClass().getSimpleName());
        ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR, "JSON 처리 중 오류가 발생했습니다.", request.getRequestURI());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }

    // === 기본 커스텀 예외 (fallback) ===

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException e, HttpServletRequest request) {
        log.info("Application exception: {}", e.getErrorCode().name());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    // === 최종 fallback ===

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error: {}", e.getClass().getSimpleName(), e);
        ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getMessage(), request.getRequestURI());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }
}