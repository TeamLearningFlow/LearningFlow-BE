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
        String episodeContents;
        String urlTitle;
        Integer progress;
        String memoContents;
        List<episodeInformation> episodeInformationList;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResourceBlogUrlDTO {
        String collectionTitle;
        InterestField interestField;
        ResourceType resourceType;
        @NotBlank
        byte[] episodeContents;
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
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgressResponseDTO {
        private ResourceType resourceType;
        private Integer progress;
    }

}
