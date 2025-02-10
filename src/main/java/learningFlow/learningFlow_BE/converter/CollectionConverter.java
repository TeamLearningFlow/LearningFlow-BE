package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import learningFlow.learningFlow_BE.web.dto.search.SearchRequestDTO;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionConverter {

    public static List<CollectionResponseDTO.CollectionPreviewDTO> convertToHomeCollection(
            List<Collection> collections
    ) {
        return collections.stream()
                .map(collection -> CollectionConverter.toCollectionPreviewDTO(
                        collection,
                        CollectionResponseDTO.CollectionLearningInfo.builder()
                                .learningStatus("BEFORE")
                                .progressRate(null)
                                .resourceDTOList(new ArrayList<>())
                                .build(),
                        null))
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
            boolean hasNext,
            int totalPages,
            int currentPage,
            User currentUser,
            Map<Long, CollectionResponseDTO.CollectionLearningInfo> learningInfoMap,
            int totalCount
    ) {
        List<CollectionResponseDTO.CollectionPreviewDTO> list = collections.stream()
                .map(collection -> toCollectionPreviewDTO(
                        collection,
                        learningInfoMap.get(collection.getId()),
                        currentUser))
                .toList();

        return CollectionResponseDTO.SearchResultDTO.builder()
                .searchResults(list)
                .hasNext(hasNext)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalCount(totalCount)
                .build();
    }

    public static CollectionResponseDTO.CollectionPreviewDTO toCollectionPreviewDTO(
            Collection collection,
            CollectionResponseDTO.CollectionLearningInfo learningInfo,
            User currentUser
    ) {

        Integer textCount = countResourcesByType(collection,ResourceType.TEXT);

        Integer videoCount = countResourcesByType(collection, ResourceType.VIDEO);

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
                .resource(learningInfo.getResourceDTOList())
                .likesCount(collection.getBookmarkCount()) //북마크 -> 좋아요로 이름만 변경 
                .isLiked(currentUser != null && currentUser.hasBookmarked(collection.getId())) //북마크 -> 좋아요로 이름만 변경
                .progressRatePercentage(learningInfo.getProgressRate())
                .progressRatio(calculateProgressRatio(collection, learningInfo))
                .learningStatus(learningInfo.getLearningStatus())
                .startDate(learningInfo.getStartDate())
                .completedDate(learningInfo.getCompletedDate())
                .build();
    }

    private static String calculateProgressRatio(Collection collection, CollectionResponseDTO.CollectionLearningInfo learningInfo) {
        if (learningInfo.getProgressRate() == null) {
            return null;
        }

        int totalEpisodes = collection.getEpisodes().size();
        int currentEpisode = "COMPLETED".equals(learningInfo.getLearningStatus())
                ? totalEpisodes
                : learningInfo.getCurrentEpisode();

        double progressPercentage = (double) currentEpisode / totalEpisodes * 100;

        return String.format("%d / %d회차 (%.0f%%)",
                currentEpisode,
                totalEpisodes,
                progressPercentage);
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
                .lastAccessedTime(userCollection.getCompletedTime())
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
