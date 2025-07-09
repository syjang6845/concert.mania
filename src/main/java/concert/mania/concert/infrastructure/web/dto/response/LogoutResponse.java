package concert.mania.concert.infrastructure.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "로그아웃 응답")
public record LogoutResponse(
        @Schema(description = "응답 메시지", example = "로그아웃이 완료되었습니다.")
        String message,

        @Schema(description = "처리 시간 (Unix timestamp)", example = "1673456789000")
        Long timestamp
) {

    /**
     * 성공 메시지와 함께 LogoutResponse 생성
     */
    public static LogoutResponse success(String message) {
        return new LogoutResponse(message, Instant.now().toEpochMilli());
    }

    /**
     * 기본 성공 메시지로 LogoutResponse 생성
     */
    public static LogoutResponse success() {
        return success("로그아웃이 완료되었습니다.");
    }

    /**
     * 현재 시간으로 LogoutResponse 생성
     */
    public static LogoutResponse of(String message) {
        return new LogoutResponse(message, Instant.now().toEpochMilli());
    }
}