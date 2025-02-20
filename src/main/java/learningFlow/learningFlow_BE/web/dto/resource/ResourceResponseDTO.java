package learningFlow.learningFlow_BE.web.dto.resource;

import jakarta.validation.constraints.NotBlank;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        String episodeContents;
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
        Long episodeId;
        Integer episodeNumber;
        String urlTitle;
        Boolean isCompleted;
        ResourceType resourceType;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchResultResourceDTO {
        Long episodeId;
        String episodeName;
        String url;
        String resourceSource;
        Integer episodeNumber;
        Boolean today;
        Boolean completed;
        Integer progress;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgressResponseDTO {
        private ResourceType resourceType;
        private Integer progress;
        private Boolean isCompleted;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentlyWatchedEpisodeDTO {
        Long episodeId;
        Long collectionId;
        String collectionTitle;
        String resourceSource;
        Integer episodeNumber;
        String episodeName;
        String progressRatio;
        Integer currentProgress;
        Integer totalProgress;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class changeEpisodeIsCompleteDTO {
        Boolean isComplete;
    }
}
