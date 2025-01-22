package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;

import java.util.*;

public class ResourceConverter {
    public static ResourceResponseDTO.ResourceUrlDTO watchEpisode(Collection collection, UserEpisodeProgress userProgress, Resource resource, Optional<Memo> memo){
        String memoContents = "작성하신 글의 첫 줄은 노트의 제목이 됩니다, 최대 2,000자까지 입력하실 수 있어요";
        if (!memo.isEmpty())
            memoContents = memo.get().getContents();
        return ResourceResponseDTO.ResourceUrlDTO.builder()
                .collectionTitle(collection.getTitle())
                .interestField(collection.getInterestField())
                .resourceType(userProgress.getResourceType())
                .clientUrl(resource.getClientUrl())
                .urlTitle(resource.getTitle())
                .progress(userProgress.getCurrentProgress())
                .memoContents(memoContents)
                .episodeInformationList(episodeInformationList(collection))
                .build();
    }

    public static List<ResourceResponseDTO.episodeInformation> episodeInformationList(Collection collection) {
        List<ResourceResponseDTO.episodeInformation> episodeInformationList = new ArrayList<>();

        for (CollectionEpisode episode : collection.getEpisodes()) {
            episodeInformationList.add(new ResourceResponseDTO.episodeInformation(
                    episode.getEpisodeNumber(),
                    episode.getResource().getTitle()
            ));
        }
        Collections.sort(episodeInformationList, Comparator.comparingInt(ResourceResponseDTO.episodeInformation::getEpisodeNumber));
        return episodeInformationList;
    }
}
