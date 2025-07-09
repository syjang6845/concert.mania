package concert.mania.concert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 비즈니스 로그 도메인 모델
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BusinessLog {
    
    private Long id; // 로그 고유 식별자
    private String action; // 비즈니스 액션 (RESERVATION_CREATED, PAYMENT_COMPLETED 등)
    private String entityType; // 관련 엔티티 타입 (User, Concert, Reservation 등)
    private String entityId; // 관련 엔티티 ID
    private String userId; // 관련 사용자 ID
    private String details; // JSON 형태로 상세 정보 저장
    private LocalDateTime timestamp; // 로그 생성 시간
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
    
    /**
     * 로그가 특정 액션에 관한 것인지 확인
     * @param actionName 확인할 액션 이름
     * @return 해당 액션 여부
     */
    public boolean isAction(String actionName) {
        return action != null && action.equals(actionName);
    }
    
    /**
     * 로그가 특정 엔티티 타입에 관한 것인지 확인
     * @param type 확인할 엔티티 타입
     * @return 해당 엔티티 타입 여부
     */
    public boolean isEntityType(String type) {
        return entityType != null && entityType.equals(type);
    }
    
    /**
     * 로그가 특정 엔티티 ID에 관한 것인지 확인
     * @param id 확인할 엔티티 ID
     * @return 해당 엔티티 ID 여부
     */
    public boolean isEntityId(String id) {
        return entityId != null && entityId.equals(id);
    }
    
    /**
     * 로그가 특정 사용자에 관한 것인지 확인
     * @param id 확인할 사용자 ID
     * @return 해당 사용자 여부
     */
    public boolean isUserId(String id) {
        return userId != null && userId.equals(id);
    }
    
    /**
     * 로그가 특정 시간 이후인지 확인
     * @param dateTime 기준 시간
     * @return 기준 시간 이후 여부
     */
    public boolean isAfter(LocalDateTime dateTime) {
        return timestamp != null && timestamp.isAfter(dateTime);
    }
    
    /**
     * 로그가 특정 시간 이전인지 확인
     * @param dateTime 기준 시간
     * @return 기준 시간 이전 여부
     */
    public boolean isBefore(LocalDateTime dateTime) {
        return timestamp != null && timestamp.isBefore(dateTime);
    }
    
    /**
     * 로그 상세 정보에 특정 텍스트가 포함되어 있는지 확인
     * @param text 검색할 텍스트
     * @return 텍스트 포함 여부
     */
    public boolean containsText(String text) {
        return details != null && details.contains(text);
    }
}