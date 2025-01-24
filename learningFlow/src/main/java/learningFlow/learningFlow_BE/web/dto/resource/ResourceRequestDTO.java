package learningFlow.learningFlow_BE.web.dto.resource;

import jakarta.validation.constraints.NotNull;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.Getter;

public class ResourceRequestDTO {

    @Getter
    public static class ProgressRequestDTO {
        @NotNull
        private Long episodeId;  // 강의 ID
        @NotNull
        private ResourceType resourceType; // 강의 타입 (VIDEO or TEXT)
        private Integer progress;  // 유튜브 강의 && 블로그 픽셀
    }
}
