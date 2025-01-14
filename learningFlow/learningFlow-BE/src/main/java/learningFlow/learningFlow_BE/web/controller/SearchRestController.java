package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import learningFlow.learningFlow_BE.validation.annotation.CheckPage;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
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

    @GetMapping("/search")
    @Operation(summary = "강의 검색 API", description = "키워드로 강의 에피소드를 검색하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "keyword", description = "검색 키워드", required = true),
            @Parameter(name = "mediaType", description = "미디어 타입 (VIDEO, TEXT, BOTH)", required = false),
            @Parameter(name = "difficulty", description = "난이도", required = false),
            @Parameter(name = "interest", description = "관심 분야", required = false),
            @Parameter(name = "page", description = "페이지 번호", required = true)
    })
    public ApiResponse<CollectionResponseDTO.CollectionListDto> searchEpisodes(
            @RequestParam String keyword,
            @RequestParam(required = false) MediaType mediaType, // 미디어 타입 필터
            @RequestParam(required = false) Integer difficulty, // 난이도 필터
            @RequestParam(required = false) String interest, // 관심 분야 필터
            @CheckPage Integer page
    ) {
        /**
         * @CheckPage 어노테이션은 페이지 값 쿼리 스트링으로 전달 받아서 해당 값에서 -1 해서 page에 매핑해주는 ArgumentResolver
         */
        // TODO: 강의 검색 로직 구현,
        //  검색 키워드에는 비단 collection의 제목만이 아닌 강사 이름, 난이도 기타 등등 다양한 값이 들어와서 전달되고 이건 나중에 JPA 사용해서 구현
        // TODO: 미디어 타입 필터, 난이도 필터, 관심 분야 필터는 필요할 때만 받아서 처리하기 때문에 QueryDsl로 처리
        return ApiResponse.onSuccess(null);
    }
}
