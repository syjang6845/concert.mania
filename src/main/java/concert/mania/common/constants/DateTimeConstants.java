package concert.mania.common.constants;

import java.time.format.DateTimeFormatter;

public final class DateTimeConstants {
    private DateTimeConstants() {
        // Utility class - prevent instantiation
    }

    public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN);

}
