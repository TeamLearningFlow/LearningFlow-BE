package learningFlow.learningFlow_BE.web.dto.resource;

import jakarta.validation.constraints.NotBlank;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.*;

import java.util.List;

public class ResourceResponseDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResourceUrlDTO {
        String collectionTitle;
        InterestField interestField;
        ResourceType resourceType;
        @NotBlank
        String clientUrl;
        String urlTitle;
        Integer progress;
        String memoContents;
        List<episodeInformation> episodeInformationList;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class episodeInformation {
        Integer episodeNumber;
        String urlTitle;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchResultResourceDTO {
        String episodeName;
        String url;
        String resourceSource;
        Integer episodeNumber;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BlogResponseDTO {
        private String url;        // 요청된 블로그 URL
        private String htmlContent; // 블로그 HTML 데이터
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgressResponseDTO {
        private ResourceType resourceType;
        private Integer progress;
    }

}
