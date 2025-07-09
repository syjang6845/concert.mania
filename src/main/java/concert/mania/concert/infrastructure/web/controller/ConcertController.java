package concert.mania.concert.infrastructure.web.controller;
import concert.mania.concert.domain.model.Concert;
import concert.mania.concert.infrastructure.web.docs.concert.GetAllConcertsApiDoc;
import concert.mania.concert.infrastructure.web.docs.concert.GetConcertByIdApiDoc;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import concert.mania.concert.application.port.in.ConcertQueryUseCase;
import concert.mania.concert.infrastructure.web.dto.response.ConcertResponse;
import concert.mania.concert.infrastructure.web.dto.response.SuccessResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
@Slf4j
public class ConcertController {

    private final ConcertQueryUseCase concertQueryUseCase;

    /**
     * 모든 콘서트 목록 조회
     * @return 콘서트 목록
     */
    @GetMapping
    @GetAllConcertsApiDoc
    public ResponseEntity<SuccessResponse> getAllConcerts() {
        List<Concert> concerts = concertQueryUseCase.getAllConcerts();
        List<ConcertResponse> response = concerts.stream()
                .map(ConcertResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(SuccessResponse.of("콘서트 목록 조회 성공", HttpStatus.OK.value(), response));
    }

    /**
     * 콘서트 상세 정보 조회
     * @param concertId 콘서트 ID
     * @return 콘서트 상세 정보
     */
    @GetMapping("/{concertId}")
    @GetConcertByIdApiDoc
    public ResponseEntity<SuccessResponse> getConcertById(@PathVariable Long concertId) {
        Concert concert = concertQueryUseCase.getConcertById(concertId);
        ConcertResponse response = ConcertResponse.from(concert);

        return ResponseEntity.ok(SuccessResponse.of("콘서트 상세 정보 조회 성공", HttpStatus.OK.value(), response));
    }
}
