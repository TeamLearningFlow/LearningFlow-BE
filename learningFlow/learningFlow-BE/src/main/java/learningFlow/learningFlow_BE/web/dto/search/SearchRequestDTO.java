package learningFlow.learningFlow_BE.web.dto.search;

import learningFlow.learningFlow_BE.domain.enums.MediaType;
import lombok.Builder;
import lombok.Getter;

public class SearchRequestDTO {

    @Getter
    @Builder
    public static class SearchConditionDTO {
        private String keyword;
        private MediaType mediaType;
        private Integer difficulty;
        private Integer amount;
    }
}
