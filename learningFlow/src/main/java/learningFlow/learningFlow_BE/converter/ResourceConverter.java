package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.UserEpisodeProgress;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;

public class ResourceConverter {
    public static ResourceResponseDTO.ResourceUrlDTO watchEpisode(Collection collection, UserEpisodeProgress userProgress){
        return ResourceResponseDTO.ResourceUrlDTO.builder()
                .collectionTitle(collection.getTitle())
                .interestField(collection.getInterestField())
                .resourceType(userProgress.getResourceType())
                .clientUrl()
                .progress(userProgress.getCurrentProgress())
                .memoContents()
                .episodeInformationList().build();
    }
}
