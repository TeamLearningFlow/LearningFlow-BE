package learningFlow.learningFlow_BE.web.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class SearchResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultDTO {
        private List<CollectionPreviewDTO> searchResults;
        private Long lastId;     // 마지막 컬렉션의 ID
        private Boolean hasNext; // 다음 페이지 존재 여부
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionPreviewDTO {
        Long id;
        String title;
        String creator;
        List<String> keywords;
        Integer difficulty;
        Integer amount;
    }
}
