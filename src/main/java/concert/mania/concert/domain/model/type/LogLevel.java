package concert.mania.concert.domain.model.type;

/**
 * 로그 레벨 열거형
 */
public enum LogLevel {
    DEBUG("DEBUG", "디버그"),
    INFO("INFO", "정보"),
    WARN("WARN", "경고"),
    ERROR("ERROR", "오류");

    private final String code;
    private final String description;

    LogLevel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}