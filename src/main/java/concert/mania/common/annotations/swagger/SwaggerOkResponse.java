package concert.mania.common.annotations.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.annotation.AliasFor;

/**
 * SwaggerOkResponse: API 성공 응답에 대한 커스텀 어노테이션
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation( // @Operation을 직접적인 메타-어노테이션으로 추가
        // summary, description 등 @Operation의 다른 기본값을 설정할 수 있습니다.
        // 여기서 description은 @SwaggerOkResponse의 description에 의해 오버라이드될 것입니다.
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200") // 이 @ApiResponse는 고정된 응답 코드 200을 가집니다.
})
public @interface SwaggerOkResponse {
    // @AliasFor를 사용하여, 이 'description' 속성이
    // '직접적인 메타-어노테이션'인 @Operation의 'description' 속성을 위한 별칭임을 명시합니다.
    @AliasFor(annotation = Operation.class, attribute = "description")
    String description() default "성공";
}