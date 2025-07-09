package concert.mania.concert.domain.model;

import concert.mania.concert.domain.model.type.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 시스템 로그 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SystemLog {
    
    private Long id; // 로그 고유 식별자
    private LogLevel logLevel; // 로그 레벨 (INFO, WARN, ERROR)
    private String logger; // 로그를 생성한 로거 이름
    private String message; // 로그 메시지
    private String stackTrace; // 예외 발생 시 스택 트레이스
    private LocalDateTime timestamp; // 로그 생성 시간
    private String userId; // 관련 사용자 ID (있는 경우)
    private String requestUri; // 요청 URI (있는 경우)
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    

    /**
     * 로그에 스택 트레이스가 있는지 확인
     * @return 스택 트레이스 존재 여부
     */
    public boolean hasStackTrace() {
        return stackTrace != null && !stackTrace.isEmpty();
    }
    
    /**
     * 로그에 사용자 ID가 있는지 확인
     * @return 사용자 ID 존재 여부
     */
    public boolean hasUserId() {
        return userId != null && !userId.isEmpty();
    }
    
    /**
     * 로그에 요청 URI가 있는지 확인
     * @return 요청 URI 존재 여부
     */
    public boolean hasRequestUri() {
        return requestUri != null && !requestUri.isEmpty();
    }
    
    /**
     * 로그가 특정 시간 이후인지 확인
     * @param dateTime 기준 시간
     * @return 기준 시간 이후 여부
     */
    public boolean isAfter(LocalDateTime dateTime) {
        return timestamp.isAfter(dateTime);
    }
    
    /**
     * 로그가 특정 시간 이전인지 확인
     * @param dateTime 기준 시간
     * @return 기준 시간 이전 여부
     */
    public boolean isBefore(LocalDateTime dateTime) {
        return timestamp.isBefore(dateTime);
    }
    
    /**
     * 로그 메시지에 특정 텍스트가 포함되어 있는지 확인
     * @param text 검색할 텍스트
     * @return 텍스트 포함 여부
     */
    public boolean containsText(String text) {
        return message != null && message.contains(text);
    }
}