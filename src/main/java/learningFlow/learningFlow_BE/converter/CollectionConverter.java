package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;

import java.util.List;

public class CollectionConverter {

    public static SearchRequestDTO.SearchConditionDTO toSearchConditionDTO(
            String keyword,
            InterestField interestFields,
            Integer preferMediaType,
            List<Integer> difficulties,
            List<String> amounts
    ) {
        return SearchRequestDTO.SearchConditionDTO.builder()
                .keyword(keyword)
                .interestFields(interestFields)
                .preferMediaType(preferMediaType)
                .difficulties(difficulties)
                .amounts(amounts)
                .build();
    }

    public static CollectionResponseDTO.SearchResultDTO toSearchResultDTO(
            List<Collection> collections,
            Long lastId,
            boolean hasNext,
            int totalPages,
            int currentPage,
            User currentUser
    ) {
        List<CollectionResponseDTO.CollectionPreviewDTO> list = collections.stream()
                .map(collection -> toCollectionPreviewDTO(collection, currentUser))
                .toList();

        return CollectionResponseDTO.SearchResultDTO.builder()
                .searchResults(list)
                .lastId(lastId)
                .hasNext(hasNext)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .build();
    }

    public static CollectionResponseDTO.CollectionPreviewDTO toCollectionPreviewDTO(Collection collection, User currentUser) {

        int totalSeconds = collection.getEpisodes().stream()
                .map(CollectionEpisode::getResource)
                .mapToInt(Resource::getRuntime).sum();

        int totalHours = (int) Math.ceil(totalSeconds / 3600);

        Integer textCount = countResourcesByType(collection,ResourceType.TEXT);

        Integer videoCount = countResourcesByType(collection, ResourceType.VIDEO);

        List<ResourceResponseDTO.SearchResultResourceDTO> resourceDTOList = getResourceDTOList(collection);

        return CollectionResponseDTO.CollectionPreviewDTO.builder()
                .id(collection.getId())
                .interestField(collection.getInterestField())
                .title(collection.getTitle())
                .creator(collection.getCreator())
                .keywords(collection.getKeywords())
                .difficulties(collection.getDifficulty())
                .amount(collection.getAmount())
                .runtime(totalHours)
                .textCount(textCount)
                .videoCount(videoCount)
                .resource(resourceDTOList)
                .bookmarkCount(collection.getBookmarkCount())
                .isBookmarked(currentUser != null && currentUser.hasBookmarked(collection.getId()))
                .build();
    }

    private static List<ResourceResponseDTO.SearchResultResourceDTO> getResourceDTOList(Collection collection) {

        return collection.getEpisodes().stream()
                .map(episode -> ResourceResponseDTO.SearchResultResourceDTO.builder()
                        .episodeName(episode.getEpisodeName())
                        .url(episode.getResource().getUrl())
                        .resourceSource(extractResourceSource(episode.getResource().getUrl()))
                        .episodeNumber(episode.getEpisodeNumber())
                        .build())
                .toList();

    }

    private static String extractResourceSource(String url) {

        String lowerCaseUrl = url.toLowerCase();

        if (lowerCaseUrl.contains("youtube")) {
            return "youtube";
        } else if (lowerCaseUrl.contains("velog")) {
            return "velog";
        } else if (lowerCaseUrl.contains("naver")) {
            return "naverBlog";
        } else if (lowerCaseUrl.contains("tistory")) {
            return "tistory";
        } else {
            return "unknown";
        }
    }

    private static int countResourcesByType(Collection collection, ResourceType type) {
        return (int) collection.getEpisodes().stream()
                .map(CollectionEpisode::getResource)
                .filter(resource -> resource.getType() == type)
                .count();
    }


}
