package learningFlow.learningFlow_BE.web.dto.search;

import learningFlow.learningFlow_BE.domain.enums.InterestField;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class SearchRequestDTO {

    @Getter
    @Builder
    public static class SearchConditionDTO {
        private String keyword;
        private List<InterestField> interestFields;
        private Integer preferMediaType;
        private List<Integer> difficulties;
        private List<String> amounts;
    }
}
