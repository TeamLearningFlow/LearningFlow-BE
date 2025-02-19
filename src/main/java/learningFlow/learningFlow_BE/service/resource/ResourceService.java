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

import java.util.Optional;

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
    public void updateUserCollection(CollectionEpisode episode, String loginId) {
        // UserCollection 조회
        Collection collection = episode.getCollection();
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_NOT_FOUND));

        // UserCollection 조회
        Optional<UserCollection> optionalUserCollection = userCollectionRepository.findByUserAndCollection(user, collection);
        Integer episodeNumber = episode.getEpisodeNumber();

        UserCollection userCollection;

        if (optionalUserCollection.isPresent()) {
            // UserCollection 이 존재하는 경우 episodeNumber 만 업데이트
            userCollection = optionalUserCollection.get();

            userCollection.updateUserCollection(episodeNumber);
        } else {
            // UserCollection 이 존재하지 않는 경우 새로 생성
            userCollection = new UserCollection();
            userCollection.setUserCollection(user, collection, episodeNumber);
        }
    }
    @Transactional
    public Boolean saveProgress(ResourceRequestDTO.ProgressRequestDTO request, String userId, Long episodeId) {
        UserEpisodeProgressId userEpisodeId = new UserEpisodeProgressId(episodeId, userId);
        UserEpisodeProgress userEpisode = userEpisodeProgressRepository.findById(userEpisodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_PROGRESS_NOT_FOUND));
        // 만약 진도가 80이상인 경우 완료로 저장
        Integer requestProgress = request.getProgress();
        if (requestProgress >= 80) userEpisode.setIsComplete(true);
        userEpisode.setCurrentProgress(requestProgress);
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
        return isComplete;
    }
}