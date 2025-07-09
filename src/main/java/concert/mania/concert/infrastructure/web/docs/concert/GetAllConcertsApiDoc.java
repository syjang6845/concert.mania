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
        summary = "콘서트 목록 조회",
        description = """
        **모든 콘서트 목록 조회**
        
        **처리 과정:**
        1. 모든 콘서트 정보 조회
        2. 콘서트 목록 반환
        
        **반환 정보:**
        - 콘서트 ID, 이름, 설명, 날짜, 장소, 이미지 URL, 상태 등 정보
        """
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "콘서트 목록 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ConcertResponse.class),
                        examples = @ExampleObject(
                                name = "조회 성공",
                                value = """
                                {
                                    "message": "콘서트 목록 조회 성공",
                                    "statusCode": 200,
                                    "data": [
                                        {
                                            "id": 1,
                                            "name": "2025 여름 콘서트",
                                            "description": "여름 시즌 특별 콘서트",
                                            "date": "2025-07-15T19:00:00",
                                            "venue": "서울 올림픽 공원",
                                            "imageUrl": "https://example.com/images/concert1.jpg",
                                            "status": "UPCOMING"
                                        },
                                        {
                                            "id": 2,
                                            "name": "가을 재즈 페스티벌",
                                            "description": "가을 재즈 특별 공연",
                                            "date": "2025-09-20T18:30:00",
                                            "venue": "부산 영화의 전당",
                                            "imageUrl": "https://example.com/images/concert2.jpg",
                                            "status": "UPCOMING"
                                        }
                                    ]
                                }
                                """
                        )
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
                                    "path": "/api/v1/concerts"
                                }
                                """
                        )
                )
        )
})
public @interface GetAllConcertsApiDoc {
}