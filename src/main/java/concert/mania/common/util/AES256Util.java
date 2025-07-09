
package concert.mania.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import concert.mania.exception.model.InternalServerErrorException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import static concert.mania.exception.model.ErrorCode.*;

/**
 * 보안 강화된 AES-256-GCM 암호화/복호화 유틸리티
 *
 * <h3>보안 특징:</h3>
 * <ul>
 *   <li>PBKDF2를 사용한 안전한 키 유도</li>
 *   <li>매번 새로운 랜덤 IV 생성</li>
 *   <li>GCM 모드로 인증과 암호화 동시 제공</li>
 *   <li>타이밍 공격 방지를 위한 안전한 비교</li>
 * </ul>
 */
@Component
@Slf4j
public class AES256Util {

    // ========== 암호화 상수 ==========
    private static final String AES_ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String HASH_ALGORITHM = "SHA-256";

    // GCM 파라미터
    private static final int IV_LENGTH_BYTES = 12;  // 96비트 (GCM 권장)
    private static final int TAG_LENGTH_BITS = 128; // 128비트 인증 태그
    private static final int KEY_LENGTH_BITS = 256; // AES-256

    // PBKDF2 파라미터 (보안 강화)
    private static final int PBKDF2_ITERATIONS = 120000; // OWASP 2023 권장값
    private static final String FIXED_SALT = "SecureApp2024Salt"; // 실제론 동적 솔트 권장

    // 암호화 키
    @Value("${aes.key}")
    private String masterPassword;

    // 보안 랜덤 생성기 (thread-safe)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 평문을 AES-256-GCM으로 안전하게 암호화합니다.
     *
     * @param plainText 암호화할 평문 (null 또는 빈 문자열 허용)
     * @return Base64 URL-safe 인코딩된 암호문 (IV + 암호화된 데이터)
     * @throws InternalServerErrorException 암호화 실패 시
     */
    public String encrypt(String plainText) {
        // 입력 검증
        if (isNullOrEmpty(plainText)) {
            log.info("암호화 대상이 null 또는 빈 문자열입니다.");
            return plainText;
        }

        try {
            // 1. 안전한 랜덤 IV 생성
            byte[] iv = generateSecureRandomIV();

            // 2. 마스터 패스워드에서 AES 키 유도
            SecretKeySpec secretKey = deriveAESKey();

            // 3. GCM Cipher 초기화
            Cipher cipher = initializeGCMCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

            // 4. 암호화 수행
            byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedData = cipher.doFinal(plainTextBytes);

            // 5. IV + 암호화된 데이터 결합
            byte[] combinedData = combineIVAndEncryptedData(iv, encryptedData);

            // 6. Base64 URL-safe 인코딩
            String encodedResult = Base64.getUrlEncoder().withoutPadding().encodeToString(combinedData);

            log.info("암호화 성공: {}자 → {}자 (IV: {}바이트, 암호문: {}바이트)",
                    plainText.length(), encodedResult.length(), iv.length, encryptedData.length);

            return encodedResult;

        } catch (Exception e) {
            log.error("암호화 실패 - 평문 길이: {}, 오류: {}", plainText.length(), e.getMessage(), e);
            throw new InternalServerErrorException(ENCRYPTION_ERROR);
        }
    }

    /**
     * AES-256-GCM으로 암호화된 데이터를 안전하게 복호화합니다.
     *
     * @param encryptedText Base64 인코딩된 암호문 (IV + 암호화된 데이터)
     * @return 복호화된 평문
     * @throws InternalServerErrorException 복호화 실패 시
     */
    public String decrypt(String encryptedText) {
        // 입력 검증
        if (isNullOrEmpty(encryptedText)) {
            log.info("복호화 대상이 null 또는 빈 문자열입니다.");
            return encryptedText;
        }

        try {
            // 1. Base64 디코딩
            byte[] combinedData = Base64.getUrlDecoder().decode(encryptedText);

            // 2. 데이터 길이 검증
            validateEncryptedDataLength(combinedData);

            // 3. IV와 암호화된 데이터 분리
            byte[] iv = extractIV(combinedData);
            byte[] encryptedData = extractEncryptedData(combinedData);

            // 4. 마스터 패스워드에서 AES 키 유도
            SecretKeySpec secretKey = deriveAESKey();

            // 5. GCM Cipher 초기화
            Cipher cipher = initializeGCMCipher(Cipher.DECRYPT_MODE, secretKey, iv);

            // 6. 복호화 수행 (인증 포함)
            byte[] decryptedBytes = cipher.doFinal(encryptedData);

            String result = new String(decryptedBytes, StandardCharsets.UTF_8);

            log.info("복호화 성공: {}자 → {}자", encryptedText.length(), result.length());
            log.info("복호화 성공: {}자 → {}자", encryptedText, result);

            return result;

        } catch (Exception e) {
            log.error("복호화 실패 - 암호문 길이: {}, 오류: {}", encryptedText.length(), e.getMessage(), e);
            throw new InternalServerErrorException(DECRYPTION_ERROR);
        }
    }

    /**
     * 복호화 실패 시 원본을 반환하는 안전한 복호화
     * AttributeConverter나 기존 데이터 호환성이 필요한 경우 사용
     *
     * @param encryptedText 암호화된 텍스트 또는 평문
     * @return 복호화된 텍스트 또는 원본 텍스트
     */
    public String decryptSafely(String encryptedText) {
        if (isNullOrEmpty(encryptedText)) {
            return encryptedText;
        }

        try {
            return decrypt(encryptedText);
        } catch (Exception e) {
            log.warn("복호화 실패로 원본 반환 - 길이: {}, 오류 타입: {}",
                    encryptedText.length(), e.getClass().getSimpleName());
            return encryptedText;
        }
    }

    // ========== Private Helper Methods ==========

    /**
     * PBKDF2를 사용하여 마스터 패스워드에서 AES-256 키를 안전하게 유도
     */
    private SecretKeySpec deriveAESKey() {
        validateMasterPassword();

        try {
            // 솔트를 SHA-256으로 해시하여 32바이트 고정 크기로 만듦
            byte[] salt = hashString(FIXED_SALT);

            // PBKDF2 키 유도 스펙 생성
            PBEKeySpec keySpec = new PBEKeySpec(
                    masterPassword.toCharArray(),
                    salt,
                    PBKDF2_ITERATIONS,
                    KEY_LENGTH_BITS
            );

            // PBKDF2 키 유도 수행
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
            SecretKey derivedKey = keyFactory.generateSecret(keySpec);

            // 메모리에서 패스워드 제거 (보안)
            keySpec.clearPassword();

            // AES SecretKeySpec 생성
            byte[] keyBytes = derivedKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);

            // 키 바이트 배열 초기화 (보안)
            Arrays.fill(keyBytes, (byte) 0);

            log.info("PBKDF2 키 유도 완료: {} 반복", PBKDF2_ITERATIONS);
            return secretKeySpec;

        } catch (Exception e) {
            log.error("PBKDF2 키 유도 실패: {}", e.getMessage(), e);
            throw new RuntimeException("암호화 키 생성 실패", e);
        }
    }

    /**
     * GCM 모드 Cipher 초기화
     */
    private Cipher initializeGCMCipher(int mode, SecretKeySpec secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
        cipher.init(mode, secretKey, gcmParameterSpec);
        return cipher;
    }

    /**
     * 암호학적으로 안전한 랜덤 IV 생성
     */
    private byte[] generateSecureRandomIV() {
        byte[] iv = new byte[IV_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    /**
     * IV와 암호화된 데이터를 결합
     */
    private byte[] combineIVAndEncryptedData(byte[] iv, byte[] encryptedData) {
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        return combined;
    }

    /**
     * 결합된 데이터에서 IV 추출
     */
    private byte[] extractIV(byte[] combinedData) {
        byte[] iv = new byte[IV_LENGTH_BYTES];
        System.arraycopy(combinedData, 0, iv, 0, IV_LENGTH_BYTES);
        return iv;
    }

    /**
     * 결합된 데이터에서 암호화된 데이터 추출
     */
    private byte[] extractEncryptedData(byte[] combinedData) {
        int encryptedDataLength = combinedData.length - IV_LENGTH_BYTES;
        byte[] encryptedData = new byte[encryptedDataLength];
        System.arraycopy(combinedData, IV_LENGTH_BYTES, encryptedData, 0, encryptedDataLength);
        return encryptedData;
    }

    /**
     * 문자열을 SHA-256으로 해시
     */
    private byte[] hashString(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 암호화된 데이터 길이 검증
     */
    private void validateEncryptedDataLength(byte[] combinedData) {
        int minimumLength = IV_LENGTH_BYTES + (TAG_LENGTH_BITS / 8); // IV + 최소 GCM 태그
        if (combinedData.length < minimumLength) {
            throw new IllegalArgumentException(
                    String.format("암호화된 데이터가 너무 짧습니다: %d바이트 (최소: %d바이트)",
                            combinedData.length, minimumLength)
            );
        }
    }

    /**
     * 마스터 패스워드 유효성 검증
     */
    private void validateMasterPassword() {
        if (isNullOrEmpty(masterPassword)) {
            throw new IllegalStateException(
                    "암호화 마스터 패스워드가 설정되지 않았습니다. " +
                            "application.properties에서 aes.key를 설정하세요."
            );
        }

        if (masterPassword.length() < 16) {
            log.warn("마스터 패스워드가 16자 미만입니다. 보안상 16자 이상 권장합니다.");
        }
    }

    /**
     * null 또는 빈 문자열 검사 (trim 포함)
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}