package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.Collection;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceRequestDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.UserCollection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResourceConverter {
    public static ResourceResponseDTO.ResourceUrlDTO watchEpisode(Collection collection, UserEpisodeProgress userProgress, Resource resource, Optional<Memo> memo
                                                                  ,List<UserEpisodeProgress> userEpisodeProgressList){
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
                .episodeInformationList(episodeInformationList(collection,userProgress, userEpisodeProgressList))
                .build();
    }
    public static ResourceResponseDTO.ResourceBlogUrlDTO watchBlogEpisode(
            Collection collection,
            UserEpisodeProgress userProgress,
            String pageResource,
            String resourceTitle,
            Optional<Memo> memo,
            List<UserEpisodeProgress> userEpisodeProgressList){
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
                .episodeInformationList(episodeInformationList(collection, userProgress, userEpisodeProgressList))
                .build();
    }

    public static List<ResourceResponseDTO.episodeInformation> episodeInformationList(
            Collection collection, UserEpisodeProgress userEpisodeProgress, List<UserEpisodeProgress> userEpisodeProgressList
    ) {
        List<ResourceResponseDTO.episodeInformation> episodeInformationList = new ArrayList<>();

        // userEpisodeProgressList를 에피소드 ID를 키로 하는 Map으로 변환
        Map<Long, UserEpisodeProgress> progressMap = userEpisodeProgressList.stream()
                .collect(Collectors.toMap(
                        progress -> progress.getUserEpisodeProgressId().getCollectionEpisodeId(),
                        Function.identity()
                ));

        // 컬렉션에 속한 각 에피소드마다 진행 상태를 매핑
        for (CollectionEpisode episode : collection.getEpisodes()) {
            // 해당 에피소드에 대해 UserEpisodeProgress가 존재하면 isComplete를 가져오고,
            // 없으면 기본값(false)를 사용
            Boolean isComplete = progressMap.containsKey(episode.getId())
                    ? progressMap.get(episode.getId()).getIsComplete() : false;

            episodeInformationList.add(new ResourceResponseDTO.episodeInformation(
                    episode.getId(),
                    episode.getEpisodeNumber(),
                    episode.getResource().getTitle(),
                    isComplete,
                    episode.getResource().getType()
            ));
        }/*
        // 유저 episodeProgress에서 가져올 것
        for (CollectionEpisode episode : collection.getEpisodes()) {
            episodeInformationList.add(new ResourceResponseDTO.episodeInformation(
                    episode.getId(),
                    episode.getEpisodeNumber(),
                    episode.getResource().getTitle(),
                    userEpisodeProgress.getIsComplete(),
                    episode.getResource().getType()
            ));
        }*/
        episodeInformationList.sort(Comparator.comparingInt(ResourceResponseDTO.episodeInformation::getEpisodeNumber));
        return episodeInformationList;
    }

    public static ResourceResponseDTO.ProgressResponseDTO toSaveProgressResponse(ResourceRequestDTO.ProgressRequestDTO request, Boolean isCompleted) {
        return ResourceResponseDTO.ProgressResponseDTO.builder()
                .progress(request.getProgress())
                .resourceType(request.getResourceType())
                .isCompleted(isCompleted)
                .build();
    }

    public static ResourceResponseDTO.changeEpisodeIsCompleteDTO toChangeEpisodeIsCompleteDTO(Boolean isComplete){
        return ResourceResponseDTO.changeEpisodeIsCompleteDTO.builder()
                .isComplete(isComplete)
                .build();
    }

    public static ResourceResponseDTO.SearchResultResourceDTO convertToResourceDTO(
            CollectionEpisode episode,
            UserEpisodeProgress userProgress, // added: 사용자 진도 정보 추가
            Integer currentEpisodeNumber    // added: 현재 학습 중인 에피소드 번호 추가
    ) {
        return ResourceResponseDTO.SearchResultResourceDTO.builder()
                .episodeId(episode.getId())
                .episodeName(episode.getEpisodeName())
                .url(episode.getResource().getUrl())
                .resourceSource(extractResourceSource(episode.getResource().getUrl()))
                .episodeNumber(episode.getEpisodeNumber())
                // added: today 값 설정 - 다음 학습할 에피소드인지 확인
                .today(episode.getEpisodeNumber().equals(currentEpisodeNumber + 1))
                // added: completed 값 설정 - 현재 에피소드보다 번호가 작으면 완료된 것
                .completed(userProgress != null && userProgress.getIsComplete())
                // added: progress 값 설정 - 해당 에피소드의 진도율
                .progress(userProgress != null ? userProgress.getCurrentProgress() : null)
                .build();
    }

    public static ResourceResponseDTO.SearchResultResourceDTO convertToResourceDTO(
            CollectionEpisode episode,
            UserEpisodeProgress progress
    ) {
        return ResourceResponseDTO.SearchResultResourceDTO.builder()
                .episodeId(episode.getId())
                .episodeName(episode.getEpisodeName())
                .url(episode.getResource().getUrl())
                .resourceSource(extractResourceSource(episode.getResource().getUrl()))
                .episodeNumber(episode.getEpisodeNumber())
                .progress(progress != null ? progress.getCurrentProgress() : null)
                .build();
    }

    public static ResourceResponseDTO.SearchResultResourceDTO convertToResourceDTO(
            CollectionEpisode episode
    ) {
        return convertToResourceDTO(episode, null);
    }

    public static ResourceResponseDTO.RecentlyWatchedEpisodeDTO convertToRecentlyWatchedEpisodeDTO(
            UserCollection userCollection,
            UserEpisodeProgress userEpisodeProgress
    ) {
        return ResourceResponseDTO.RecentlyWatchedEpisodeDTO.builder()
                .episodeId(getEpisodeId(userCollection))
                .collectionId(userCollection.getCollection().getId())
                .collectionTitle(userCollection.getCollection().getTitle())
                .resourceSource(extractResourceSource(getResourceUrl(userCollection)))
                .episodeNumber(userCollection.getUserCollectionStatus())
                .episodeName(getEpisodeName(userCollection))
                .progressRatio(calculateProgressRatio(userCollection))
                .currentProgress(userEpisodeProgress != null ? userEpisodeProgress.getCurrentProgress() : 0)
                .totalProgress(userEpisodeProgress != null ? userEpisodeProgress.getTotalProgress() : 0)
                .build();
    }

    private static Long getEpisodeId(UserCollection userCollection) {
        // added: episodeId를 찾는 메소드 추가
        return userCollection.getCollection().getEpisodes().stream()
                .filter(episode -> episode.getEpisodeNumber().equals(userCollection.getUserCollectionStatus()))
                .findFirst()
                .map(CollectionEpisode::getId)
                .orElse(null);
    }

    public static List<ResourceResponseDTO.SearchResultResourceDTO> convertToResourceDTOWithToday(
            List<CollectionEpisode> episodes,
            int nextEpisodeNumber,
            int lastCompletedEpisode
    ) {
        return episodes.stream()
                .map(episode -> ResourceResponseDTO.SearchResultResourceDTO.builder()
                        .episodeId(episode.getId())
                        .episodeName(episode.getEpisodeName())
                        .url(episode.getResource().getUrl())
                        .resourceSource(extractResourceSource(episode.getResource().getUrl()))
                        .episodeNumber(episode.getEpisodeNumber())
                        .today(episode.getEpisodeNumber().equals(nextEpisodeNumber))
                        .completed(episode.getEpisodeNumber() <= lastCompletedEpisode)
                        .build())
                .toList();
    }

    public static ResourceResponseDTO.RecentlyWatchedEpisodeDTO convertToRecentlyWatchedEpisodeDTO(
            UserCollection userCollection,
            UserEpisodeProgress userEpisodeProgress,
            CollectionEpisode currentEpisode,  // added: 파라미터 추가
            int totalEpisodes,                 // added: 파라미터 추가
            double progressPercentage          // added: 파라미터 추가
    ) {

        int progressRatePercentage = (int) Math.round(progressPercentage);

        return ResourceResponseDTO.RecentlyWatchedEpisodeDTO.builder()
                .episodeId(currentEpisode.getId())
                .imgUrl(userCollection.getCollection().getCollectionImgUrl())
                .collectionId(userCollection.getCollection().getId())
                .collectionTitle(userCollection.getCollection().getTitle())
                .resourceSource(extractResourceSource(currentEpisode.getResource().getUrl()))
                .episodeNumber(userCollection.getUserCollectionStatus())
                .episodeName(currentEpisode.getEpisodeName())
                .progressRatio(String.format("%d / %d회차 (%.0f%%)",
                        userCollection.getUserCollectionStatus(),
                        totalEpisodes,
                        progressPercentage))
                .progressRatePercentage(progressRatePercentage)
                .currentProgress(userEpisodeProgress != null ? userEpisodeProgress.getCurrentProgress() : 0)
                .totalProgress(userEpisodeProgress != null ? userEpisodeProgress.getTotalProgress() : 0)
                .build();
    }

    public static String extractResourceSource(String url) {

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
            return "tistory";
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
