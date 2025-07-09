package concert.mania.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * Boolean 타입을 'Y' 또는 'N' 문자열로 변환하는 JPA 컨버터.
 * 데이터베이스에는 CHAR(1) 또는 VARCHAR(1) 타입으로 저장됩니다.
 */
@Slf4j
@Converter(autoApply = false) // autoApply = true 로 설정하면 모든 boolean 필드에 자동으로 적용됩니다.
// 특정 필드에만 적용하려면 false로 두고 @Convert 어노테이션을 사용합니다.
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return (attribute != null && attribute) ? "Y" : "N";
    }


    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return false;
        }
        // trim으로 공백 제거하고 대소문자 무시
        String trimmed = dbData.trim().toUpperCase();
        boolean result = "Y".equals(trimmed) || "YES".equals(trimmed) || "TRUE".equals(trimmed) || "1".equals(trimmed);
        log.debug("Converting Database '{}' (trimmed: '{}') to Boolean: {}", dbData, trimmed, result);
        return result;

    }
}