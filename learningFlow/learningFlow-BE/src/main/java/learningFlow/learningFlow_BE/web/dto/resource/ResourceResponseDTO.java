package learningFlow.learningFlow_BE.web.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ResourceResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResourceUrlDTO {
        String embeddedUrl;
    }
}