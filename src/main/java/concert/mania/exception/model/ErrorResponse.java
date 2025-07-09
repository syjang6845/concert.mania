
package concert.mania.exception.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"timestamp", "statusCode", "errorCode", "message", "path", "fieldErrors"})
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "에러 발생 시간", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private Integer statusCode;

    @Schema(description = "에러 코드",  example = "BAD_REQUEST")
    private String errorCode;

    @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
    private String message;

    @Schema(description = "요청 경로", example = "/api/v1/users")
    private String path;

    @Schema(description = "유효성 검사 에러 목록 (유효성 검사 실패시에만 포함)")
    private List<FieldError> fieldErrors;

    // 일반 에러용 생성자
    public ErrorResponse(ErrorCode code) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = code.getHttpStatus().value();
        this.errorCode = code.name();
        this.message = code.getMessage();
    }

    public ErrorResponse(ErrorCode code, String customMessage) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = code.getHttpStatus().value();
        this.errorCode = code.name();
        this.message = customMessage;
    }

    public ErrorResponse(ErrorCode code, String customMessage, String path) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = code.getHttpStatus().value();
        this.errorCode = code.name();
        this.message = customMessage;
        this.path = path;
    }

    // 유효성 검사 에러용 생성자
    public ErrorResponse(ErrorCode code, String customMessage, String path, List<FieldError> fieldErrors) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = code.getHttpStatus().value();
        this.errorCode = code.name();
        this.message = customMessage;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    // Factory Methods - 일반 에러용
    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(code, message);
    }

    public static ErrorResponse of(ErrorCode code, String message, String path) {
        return new ErrorResponse(code, message, path);
    }

    // Factory Methods - 유효성 검사 에러용
    public static ErrorResponse ofValidation(ErrorCode code, String message, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(code, message, path, fieldErrors);
    }

    public static ErrorResponse ofValidation(String message, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(ErrorCode.BAD_REQUEST, message, path, fieldErrors);
    }

    // 필드 에러 내부 클래스
    @Getter
    @AllArgsConstructor
    @Schema(description = "필드별 유효성 검사 에러")
    public static class FieldError {
        @Schema(description = "에러가 발생한 필드명", example = "email")
        private String field;

        @Schema(description = "거부된 값", example = "invalid-email")
        private Object rejectedValue;

        @Schema(description = "에러 메시지", example = "올바른 이메일 형식이 아닙니다.")
        private String message;
    }
}