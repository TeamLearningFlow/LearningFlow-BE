package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceRequestDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import org.springframework.http.ResponseEntity;

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
                .episodeContents(resource.getClientUrl())
                .urlTitle(resource.getTitle())
                .progress(userProgress.getCurrentProgress())
                .memoContents(memoContents)
                .episodeInformationList(episodeInformationList(collection))
                .build();
    }
    public static ResourceResponseDTO.ResourceUrlDTO watchBlogEpisode(Collection collection, UserEpisodeProgress userProgress, String pageResource,String resourceTitle, Optional<Memo> memo){
        String memoContents = "작성하신 글의 첫 줄은 노트의 제목이 됩니다, 최대 2,000자까지 입력하실 수 있어요";
        if (!memo.isEmpty())
            memoContents = memo.get().getContents();
        return ResourceResponseDTO.ResourceUrlDTO.builder()
                .collectionTitle(collection.getTitle())
                .interestField(collection.getInterestField())
                .resourceType(userProgress.getResourceType())
                .episodeContents(pageResource)
                .urlTitle(resourceTitle)
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

/*    public static ResourceResponseDTO.BlogResponseDTO proxyBlogResponse(String url,  ResponseEntity<String> response){
        return ResourceResponseDTO.BlogResponseDTO.builder()
                .url(url)
                .htmlContent(response.getBody())
                .build();
    }*/

    public static ResourceResponseDTO.ProgressResponseDTO toSaveProgressResponse(ResourceRequestDTO.ProgressRequestDTO request){
        return ResourceResponseDTO.ProgressResponseDTO.builder()
                .progress(request.getProgress())
                .resourceType(request.getResourceType())
                .build();

    }
}
