package learningFlow.learningFlow_BE.service.resource;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.converter.ResourceConverter;
import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import learningFlow.learningFlow_BE.repository.*;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceRequestDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
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
    // 유저 + 에피소드 조회
    // 존재하지 않을 경우 -> 처음 -> 객체 생성 -> 저장 && 유저-컬렉션에 등록
    // 있을 경우 -> 에피소드 정보 불러오기
        // 처음인 경우 -> currentProgress = 0, embeddedUrl 생성
        // 처음이 아닐 경우 -> embeddedUrl, currentProgress 조회 -> 진도 저장
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
            UserEpisodeProgress userEpisodeProgress = new UserEpisodeProgress(userEpisodeProgressId, episode.getEpisodeNumber(), 0, episode.getResource().getResourceQuantity(), episode.getResource().getType());

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
    public ResourceType getResourceType(Long episodeId) {
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        return episode.getResource().getType();
    }
    @Transactional
    public Optional<Memo> getMemoContents(Long episodeId){
        return memoRepository.findByEpisodeId(episodeId);
    }

    @Transactional
    public void updateUserCollection(CollectionEpisode episode, String loginId) {
        log.info("Received loginId: {}", loginId);
        log.info("Received episode: {}", episode);
        // UserCollection 조회
        Collection collection = episode.getCollection();
        log.info("Extracted collection: {}", collection);

        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_NOT_FOUND));
        log.info("Found user: {}", user);

        // UserCollection 조회
        Optional<UserCollection> optionalUserCollection = userCollectionRepository.findByUserAndCollection(user, collection);
        log.info("UserCollection found: {}", optionalUserCollection.isPresent());

        Integer episodeNumber = episode.getEpisodeNumber();
        log.info("Episode number: {}", episodeNumber);

        UserCollection userCollection;

        if (optionalUserCollection.isPresent()) {
            // UserCollection 이 존재하는 경우 episodeNumber 만 업데이트
            userCollection = optionalUserCollection.get();
            log.info("Updating existing UserCollection with id: {}", userCollection.getId());

            userCollection.updateUserCollection(episodeNumber);
        } else {
            // UserCollection 이 존재하지 않는 경우 새로 생성
            userCollection = new UserCollection();
            userCollection.setUserCollection(user, collection, episodeNumber);
            log.info("Created new UserCollection");
        }
        // 저장
        UserCollection savedCollection = userCollectionRepository.save(userCollection);
        log.info("Saved UserCollection: {}", savedCollection);
    }
    @Transactional
    public void saveProgress(ResourceRequestDTO.ProgressRequestDTO request, String userId, Long episodeId) {
        UserEpisodeProgressId progressId = new UserEpisodeProgressId(episodeId, userId);
        UserEpisodeProgress progress = userEpisodeProgressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.USER_PROGRESS_NOT_FOUND));

        if (request.getResourceType() == ResourceType.VIDEO && request.getProgress() != null) {
            progress.setCurrentProgress(request.getProgress());
        } else if (request.getResourceType() == ResourceType.TEXT && request.getProgress() != null) {
            progress.setCurrentProgress(request.getProgress());
        } else {
            throw new ResourceHandler(ErrorStatus._BAD_REQUEST);
        }
        userEpisodeProgressRepository.save(progress);
    }
}