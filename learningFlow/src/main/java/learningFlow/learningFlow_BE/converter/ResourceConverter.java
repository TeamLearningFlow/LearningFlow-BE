package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.UserCollection;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;

import java.util.List;

public class ResourceConverter {

    public static List<ResourceResponseDTO.SearchResultResourceDTO> getResourceDTOList(Collection collection) {

        return collection.getEpisodes().stream()
                .map(episode -> ResourceResponseDTO.SearchResultResourceDTO.builder()
                        .resourceId(episode.getResource().getId())
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

    public static ResourceResponseDTO.RecentlyWatchedEpisodeDTO convertToRecentlyWatchedEpisodeDTO(
            UserCollection userCollection
    ) {
        return ResourceResponseDTO.RecentlyWatchedEpisodeDTO.builder()
                .resourceId(getResourceId(userCollection))
                .CollectionTitle(userCollection.getCollection().getTitle())
                .resourceSource(extractResourceSource(getResourceUrl(userCollection)))
                .episodeNumber(userCollection.getUserCollectionStatus())
                .episodeName(getEpisodeName(userCollection))
                .progressRatio(calculateProgressRatio(userCollection))
                .build();
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
