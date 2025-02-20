package learningFlow.learningFlow_BE.service.resource;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.repository.*;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResourceService {
    private final UserEpisodeProgressRepository userEpisodeProgressRepository;
    private final CollectionEpisodeRepository collectionEpisodeRepository;
    private final MemoRepository memoRepository;
    private final UserCollectionRepository userCollectionRepository;
    private final UserRepository userRepository;
    @Transactional
    public UserEpisodeProgress getUserEpisodeProgress(Long episodeId, String loginId){
        UserEpisodeProgressId userEpisodeProgressId = new UserEpisodeProgressId(episodeId, loginId);

        Optional<UserEpisodeProgress> episodeProgress = userEpisodeProgressRepository.findById(userEpisodeProgressId);

        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));

        updateUserCollection(episode, loginId);

        return episodeProgress.orElseGet(() -> {

            Integer resourceQuantity = episode.getResource().getResourceQuantity();
            if (resourceQuantity == null) throw new ResourceHandler(ErrorStatus.QUANTITY_IS_NULL);
            UserEpisodeProgress userEpisodeProgress = new UserEpisodeProgress(
                    userEpisodeProgressId,
                    episode.getEpisodeNumber(),
                    0,
                    episode.getResource().getResourceQuantity(),
                    false,
                    episode.getResource().getType()
            );

            log.info("resourceType", episode.getResource().getType());

            return userEpisodeProgressRepository.save(userEpisodeProgress);
        });
    }
    @Transactional
    public Collection getCollection(Long episodeId) {
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        return episode.getCollection();
    }
    @Transactional
    public Resource getResource(Long episodeId){
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        return episode.getResource();
    }

    @Transactional
    public Optional<Memo> getMemoContents(Long episodeId){
        return memoRepository.findByEpisodeId(episodeId);
    }

    @Transactional
    public List<UserEpisodeProgress> getEpisodeProgress(String loginId, Long episodeId){
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        Collection collection = episode.getCollection();
        List<CollectionEpisode> episodes = collectionEpisodeRepository.findByCollection(collection);
        List<Long> episodeIds = episodes.stream()
                .map(CollectionEpisode::getId)
                .collect(Collectors.toList());

        return userEpisodeProgressRepository
                .findByUserEpisodeProgressId_UserIdAndUserEpisodeProgressId_CollectionEpisodeIdIn(loginId, episodeIds);
    }

    @Transactional
    public void updateUserCollection(CollectionEpisode episode, String loginId) {
        Collection collection = episode.getCollection();
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_NOT_FOUND));

        Optional<UserCollection> optionalUserCollection = userCollectionRepository.findByUserAndCollection(user, collection);
        Integer episodeNumber = episode.getEpisodeNumber();

        UserCollection userCollection;
        if (optionalUserCollection.isPresent()) {
            userCollection = optionalUserCollection.get();

            userCollection.updateUserCollection(episodeNumber);
        } else {
            userCollection = new UserCollection();
            userCollection.setUserCollection(user, collection, episodeNumber);
        }
        userCollectionRepository.save(userCollection);
    }
    @Transactional
    public Boolean saveProgress(ResourceRequestDTO.ProgressRequestDTO request, String userId, Long episodeId) {
        UserEpisodeProgressId userEpisodeId = new UserEpisodeProgressId(episodeId, userId);
        UserEpisodeProgress userEpisode = userEpisodeProgressRepository.findById(userEpisodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_PROGRESS_NOT_FOUND));
        // 만약 진도가 80이상인 경우 완료로 저장
        Integer requestProgress = request.getProgress();
        if (requestProgress >= 80) {
            userEpisode.setIsComplete(true);
            checkUserCollectionComplete(episodeId, userId);
        }
        userEpisode.setCurrentProgress(requestProgress);
        userEpisodeProgressRepository.save(userEpisode);
        return userEpisode.getIsComplete();
    }

    @Transactional
    public Boolean changeEpisodeComplete(Long episodeId, String loginId){
        UserEpisodeProgressId userEpisodeId = new UserEpisodeProgressId(episodeId, loginId);
        UserEpisodeProgress userEpisodeProgress = userEpisodeProgressRepository.findById(userEpisodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_PROGRESS_NOT_FOUND));
        Boolean isComplete = userEpisodeProgress.getIsComplete();
        if (isComplete.equals(true)) isComplete = userEpisodeProgress.setIsComplete(false);
        else isComplete = userEpisodeProgress.setIsComplete(true);
        userEpisodeProgress.setCurrentProgress(0);
        userEpisodeProgressRepository.save(userEpisodeProgress);
        return isComplete;
    }

    @Transactional
    public void checkUserCollectionComplete(Long episodeId, String loginId){
        Collection collection = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND))
                .getCollection();
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_NOT_FOUND));
        UserCollection userCollection = userCollectionRepository.findByUserAndCollection(user, collection)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_COLLECTION_NOT_FOUND));
        List<CollectionEpisode> episodes = collection.getEpisodes();
        boolean allCompleted = true;
        for (CollectionEpisode episode : episodes) {
            UserEpisodeProgressId userEpisodeProgressId = new UserEpisodeProgressId(episode.getId(), loginId);
            UserEpisodeProgress userEpisodeProgress = userEpisodeProgressRepository.findById(userEpisodeProgressId)
                    .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_PROGRESS_NOT_FOUND));
            if (!userEpisodeProgress.getIsComplete().equals(Boolean.TRUE)) {
                allCompleted = false;
                break;
            }
        }
        if (allCompleted){
            userCollection.CompleteUserCollection();
            userCollectionRepository.save(userCollection);
        }
    }
}