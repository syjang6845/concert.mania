package concert.mania.concert.infrastructure.web.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SuccessResponse(
        @Schema(description = "성공 메시지", nullable = false, example = "성공 메세지")
        String message,
        @Schema(description = "상태 코드", nullable = false)
        Integer statusCode,
        @Schema(description = "응답 데이터", nullable = true)
        Object data
){
    public static SuccessResponse of(String message, int statusCode) {
        return SuccessResponse.builder()
                .message(message)
                .statusCode(statusCode)
                .build();
    }

    public static SuccessResponse of(String message, int statusCode, Object data) {
        return SuccessResponse.builder()
                .message(message)
                .statusCode(statusCode)
                .data(data)
                .build();
    }
}
