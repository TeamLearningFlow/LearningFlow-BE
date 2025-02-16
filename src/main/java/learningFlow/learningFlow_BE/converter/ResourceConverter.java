package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceRequestDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.UserCollection;
import java.util.*;

public class ResourceConverter {
    public static ResourceResponseDTO.ResourceUrlDTO watchEpisode(Collection collection, UserEpisodeProgress userProgress, Resource resource, Optional<Memo> memo){
        String memoContents = "작성하신 글의 첫 줄은 노트의 제목이 됩니다, 최대 2,000자까지 입력하실 수 있어요";
        if (memo.isPresent())
            memoContents = memo.get().getContents();
        return ResourceResponseDTO.ResourceUrlDTO.builder()
                .collectionTitle(collection.getTitle())
                .interestField(collection.getInterestField())
                .resourceType(userProgress.getResourceType())
                .episodeContents(resource.getClientUrl())
                .urlTitle(resource.getTitle())
                .progress(userProgress.getCurrentProgress())
                .memoContents(memoContents)
                .episodeInformationList(episodeInformationList(collection,userProgress))
                .build();
    }
    public static ResourceResponseDTO.ResourceBlogUrlDTO watchBlogEpisode(
            Collection collection,
            UserEpisodeProgress userProgress,
            String pageResource,
            String resourceTitle,
            Optional<Memo> memo){
        String memoContents = "작성하신 글의 첫 줄은 노트의 제목이 됩니다, 최대 2,000자까지 입력하실 수 있어요";
        if (memo.isPresent())
            memoContents = memo.get().getContents();
        return ResourceResponseDTO.ResourceBlogUrlDTO.builder()
                .collectionTitle(collection.getTitle())
                .interestField(collection.getInterestField())
                .resourceType(userProgress.getResourceType())
                .episodeContents(pageResource)
                .urlTitle(resourceTitle)
                .progress(userProgress.getCurrentProgress())
                .memoContents(memoContents)
                .episodeInformationList(episodeInformationList(collection, userProgress))
                .build();
    }

    public static List<ResourceResponseDTO.episodeInformation> episodeInformationList(
            Collection collection, UserEpisodeProgress userEpisodeProgress
    ) {
        List<ResourceResponseDTO.episodeInformation> episodeInformationList = new ArrayList<>();

        for (CollectionEpisode episode : collection.getEpisodes()) {
            episodeInformationList.add(new ResourceResponseDTO.episodeInformation(
                    episode.getEpisodeNumber(),
                    episode.getResource().getTitle(),
                    userEpisodeProgress.getIsComplete()
            ));
        }
        episodeInformationList.sort(Comparator.comparingInt(ResourceResponseDTO.episodeInformation::getEpisodeNumber));
        return episodeInformationList;
    }

    public static ResourceResponseDTO.ProgressResponseDTO toSaveProgressResponse(ResourceRequestDTO.ProgressRequestDTO request) {
        return ResourceResponseDTO.ProgressResponseDTO.builder()
                .progress(request.getProgress())
                .resourceType(request.getResourceType())
                .build();
    }

    public static ResourceResponseDTO.changeEpisodeIsCompleteDTO toChangeEpisodeIsCompleteDTO(Boolean isComplete){
        return ResourceResponseDTO.changeEpisodeIsCompleteDTO.builder()
                .isComplete(isComplete)
                .build();
    }

    public static ResourceResponseDTO.SearchResultResourceDTO convertToResourceDTO(
            CollectionEpisode episode
    ) {
        return ResourceResponseDTO.SearchResultResourceDTO.builder()
                .episodeId(episode.getId())
                .episodeName(episode.getEpisodeName())
                .url(episode.getResource().getUrl())
                .resourceSource(extractResourceSource(episode.getResource().getUrl()))
                .episodeNumber(episode.getEpisodeNumber())
                .build();
    }

    public static ResourceResponseDTO.RecentlyWatchedEpisodeDTO convertToRecentlyWatchedEpisodeDTO(
            UserCollection userCollection,
            UserEpisodeProgress userEpisodeProgress
    ) {
        return ResourceResponseDTO.RecentlyWatchedEpisodeDTO.builder()
                .resourceId(getResourceId(userCollection))
                .collectionId(userCollection.getCollection().getId())
                .collectionTitle(userCollection.getCollection().getTitle())
                .resourceSource(extractResourceSource(getResourceUrl(userCollection)))
                .episodeNumber(userCollection.getUserCollectionStatus())
                .episodeName(getEpisodeName(userCollection))
                .progressRatio(calculateProgressRatio(userCollection))
                .currentProgress(userEpisodeProgress.getCurrentProgress())
                .totalProgress(userEpisodeProgress.getTotalProgress())
                .build();
    }

    public static List<ResourceResponseDTO.SearchResultResourceDTO> convertToResourceDTOWithToday(
            List<CollectionEpisode> episodes,
            int nextEpisodeNumber
    ) {
        return episodes.stream()
                .map(episode -> ResourceResponseDTO.SearchResultResourceDTO.builder()
                            .episodeId(episode.getId())
                            .episodeName(episode.getEpisodeName())
                            .url(episode.getResource().getUrl())
                            .resourceSource(extractResourceSource(episode.getResource().getUrl()))
                            .episodeNumber(episode.getEpisodeNumber())
                            .today(episode.getEpisodeNumber().equals(nextEpisodeNumber))
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

    private static String getResourceUrl(UserCollection userCollection) {
        return userCollection.getCollection().getEpisodes().stream()
                .filter(episode -> episode.getEpisodeNumber().equals(userCollection.getUserCollectionStatus()))
                .findFirst()
                .map(episode -> episode.getResource().getUrl())
                .orElse(null);
    }

    private static Long getResourceId(UserCollection userCollection) {
        return userCollection.getCollection().getEpisodes().stream()
                .filter(episode -> episode.getEpisodeNumber().equals(userCollection.getUserCollectionStatus()))
                .findFirst()
                .map(episode -> episode.getResource().getId())
                .orElse(null);
    }

    private static String getEpisodeName(UserCollection userCollection) {
        return userCollection.getCollection().getEpisodes().stream()
                .filter(episode -> episode.getEpisodeNumber().equals(userCollection.getUserCollectionStatus()))
                .findFirst()
                .map(CollectionEpisode::getEpisodeName)
                .orElse(null);
    }

    private static String calculateProgressRatio(UserCollection userCollection) {
        int currentEpisode = userCollection.getUserCollectionStatus();
        int totalEpisodes = userCollection.getCollection().getEpisodes().size();
        double progressPercentage = ((double) currentEpisode / totalEpisodes) * 100;
        return String.format("%d / %d회차 (%.0f%%)", currentEpisode, totalEpisodes, progressPercentage);
    }
}
