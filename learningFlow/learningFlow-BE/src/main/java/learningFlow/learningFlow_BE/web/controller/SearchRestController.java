package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.converter.SearchConverter;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import learningFlow.learningFlow_BE.service.search.SearchService;
import learningFlow.learningFlow_BE.web.dto.search.SearchResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Validated
@Slf4j
@Tag(name = "Search", description = "검색 API")
public class SearchRestController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "강의 검색 API", description = "키워드로 강의 에피소드를 검색하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "keyword", description = "검색어 (컬렉션 제목, 크리에이터, 키워드, 에피소드 제목)"),
            @Parameter(name = "mediaType", description = "미디어 타입 필터 (VIDEO, TEXT)"),
            @Parameter(name = "difficulty", description = "난이도 필터 (1: 입문, 2: 초급, 3: 중급, 4: 실무)"),
            @Parameter(name = "amount", description = "강의량 필터 (1~5: 짧아요, 6~10: 적당해요, 11이상: 많아요)"),
            @Parameter(name = "lastId", description = "마지막으로 조회된 컬렉션의 ID (첫 페이지는 0)")
    })
    public ApiResponse<SearchResponseDTO.SearchResultDTO> searchEpisodes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MediaType mediaType,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) Integer amount,
            @RequestParam(required = false, defaultValue = "0") Long lastId
    ) {
        return ApiResponse.onSuccess(searchService.search(SearchConverter.toSearchConditionDTO(keyword, mediaType, difficulty, amount), lastId));
    }
}
