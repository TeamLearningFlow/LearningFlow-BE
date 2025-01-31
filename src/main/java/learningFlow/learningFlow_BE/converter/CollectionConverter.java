package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;

import java.util.List;

public class CollectionConverter {

    public static List<CollectionResponseDTO.CollectionPreviewDTO> convertToHomeCollection(List<Collection> collections) {
        return collections.stream()
                .map(collection -> CollectionConverter.toCollectionPreviewDTO(collection, null))
                .toList();
    }

    public static SearchRequestDTO.SearchConditionDTO toSearchConditionDTO(
            String keyword,
            InterestField interestFields,
            Integer preferMediaType,
            List<Integer> difficulties,
            List<String> amounts,
            Integer sortType
    ) {
        return SearchRequestDTO.SearchConditionDTO.builder()
                .keyword(keyword)
                .interestFields(interestFields)
                .preferMediaType(preferMediaType)
                .difficulties(difficulties)
                .amounts(amounts)
                .sortType(sortType)
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

        Integer textCount = countResourcesByType(collection,ResourceType.TEXT);

        Integer videoCount = countResourcesByType(collection, ResourceType.VIDEO);

        List<ResourceResponseDTO.SearchResultResourceDTO> resourceDTOList = ResourceConverter.getResourceDTOList(collection);

        return CollectionResponseDTO.CollectionPreviewDTO.builder()
                .collectionId(collection.getId())
                .interestField(collection.getInterestField())
                .title(collection.getTitle())
                .creator(collection.getCreator())
                .keywords(collection.getKeywords())
                .difficulties(collection.getDifficulty())
                .amount(collection.getAmount())
                .runtime(getTotalHours(collection))
                .textCount(textCount)
                .videoCount(videoCount)
                .resource(resourceDTOList)
                .likesCount(collection.getBookmarkCount())
                .isLiked(currentUser != null && currentUser.hasBookmarked(collection.getId()))
                .build();
    }

    public static CollectionResponseDTO.CompletedCollectionDTO convertToCompletedCollectionDTO(
            UserCollection userCollection
    ) {
        return CollectionResponseDTO.CompletedCollectionDTO.builder()
                .collectionId(userCollection.getCollection().getId())
                .interestField(userCollection.getCollection().getInterestField())
                .collectionTitle(userCollection.getCollection().getTitle())
                .creator(userCollection.getCollection().getCreator())
                .keywords(userCollection.getCollection().getKeywords())
                .difficulties(userCollection.getCollection().getDifficulty())
                .runtime(getTotalHours(userCollection.getCollection()))
                .lastAccessedTime(userCollection.getLastAccessedAt())
                .build();
    }

    private static int getTotalHours(Collection collection) {
        int totalSeconds = collection.getEpisodes().stream()
                .map(CollectionEpisode::getResource)
                .mapToInt(Resource::getStudyDuration).sum();

        return (int) Math.ceil((double) totalSeconds / 3600);
    }

    private static int countResourcesByType(Collection collection, ResourceType type) {
        return (int) collection.getEpisodes().stream()
                .map(CollectionEpisode::getResource)
                .filter(resource -> resource.getType() == type)
                .count();
    }
}
