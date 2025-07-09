package concert.mania.common.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import concert.mania.common.util.AES256Util;

/**
 * 보안 강화된 JPA String 암호화 컨버터
 *
 * <p>데이터베이스 저장 시 자동 암호화, 조회 시 자동 복호화를 수행합니다.
 * 복호화 실패 시 원본 데이터를 반환하여 기존 평문 데이터와의 호환성을 제공합니다.</p>
 */
@Converter
@Component
@RequiredArgsConstructor
@Slf4j
public class StringCryptoConverter implements AttributeConverter<String, String> {

    private final AES256Util aes256Util;

    /**
     * 엔티티 → 데이터베이스: 암호화
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.trim().isEmpty()) {
            log.debug("DB 저장: null 또는 빈 문자열은 암호화하지 않음");
            return attribute;
        }

        try {
            String encrypted = aes256Util.encrypt(attribute);
            log.debug("DB 저장용 암호화 성공: {}자 → {}자", attribute.length(), encrypted.length());
            return encrypted;

        } catch (Exception e) {
            log.error("DB 저장용 암호화 실패 - 원본: {}자, 오류: {}", attribute.length(), e.getMessage());
            // 암호화 실패 시 예외 전파 (데이터 손실 방지)
            throw new RuntimeException("데이터 암호화 실패", e);
        }
    }

    /**
     * 데이터베이스 → 엔티티: 복호화
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            log.debug("DB 조회: null 또는 빈 문자열은 복호화하지 않음");
            return dbData;
        }

        try {
            // 안전한 복호화 사용 (실패 시 원본 반환)
            String decrypted = aes256Util.decryptSafely(dbData);

            if (decrypted.equals(dbData)) {
                log.debug("DB 조회: 복호화 실패로 원본 반환 (평문 데이터로 추정)");
            } else {
                log.debug("DB 조회용 복호화 성공: {}자 → {}자", dbData.length(), decrypted.length());
            }

            return decrypted;

        } catch (Exception e) {
            log.error("DB 조회용 복호화 실패 - 암호문: {}자, 오류: {}", dbData.length(), e.getMessage());
            // 최후 수단으로 원본 반환
            return dbData;
        }
    }
}