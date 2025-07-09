package concert.mania.concert.infrastructure.web.docs.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import concert.mania.exception.model.ErrorResponse;
import concert.mania.concert.infrastructure.web.dto.response.ConcertResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "콘서트 상세 정보 조회",
        description = """
        **특정 콘서트의 상세 정보 조회**
        
        **처리 과정:**
        1. 콘서트 ID로 특정 콘서트 정보 조회
        2. 콘서트 상세 정보 반환
        
        **파라미터:**
        - concertId: 콘서트 ID (필수)
        
        **예외 조건:**
        - 존재하지 않는 콘서트 ID로 조회 시 오류 발생
        
        **반환 정보:**
        - 콘서트 ID, 이름, 설명, 날짜, 장소, 이미지 URL, 상태 등 정보
        """
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "콘서트 상세 정보 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ConcertResponse.class),
                        examples = @ExampleObject(
                                name = "조회 성공",
                                value = """
                                {
                                    "message": "콘서트 상세 정보 조회 성공",
                                    "statusCode": 200,
                                    "data": {
                                        "id": 1,
                                        "name": "2025 여름 콘서트",
                                        "description": "여름 시즌 특별 콘서트",
                                        "date": "2025-07-15T19:00:00",
                                        "venue": "서울 올림픽 공원",
                                        "imageUrl": "https://example.com/images/concert1.jpg",
                                        "status": "UPCOMING"
                                    }
                                }
                                """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "콘서트를 찾을 수 없음",
                                        value = """
                                        {
                                            "timestamp": "2025-06-17T10:00:00",
                                            "statusCode": 400,
                                            "errorCode": "CONCERT_NOT_FOUND",
                                            "message": "해당 ID의 콘서트를 찾을 수 없습니다.",
                                            "path": "/api/v1/concerts/999"
                                        }
                                        """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "서버 내부 오류",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "서버 오류",
                                value = """
                                {
                                    "timestamp": "2025-06-17T10:00:00",
                                    "statusCode": 500,
                                    "errorCode": "INTERNAL_SERVER_ERROR",
                                    "message": "서버에서 오류가 발생했습니다.",
                                    "path": "/api/v1/concerts/1"
                                }
                                """
                        )
                )
        )
})
public @interface GetConcertByIdApiDoc {
}