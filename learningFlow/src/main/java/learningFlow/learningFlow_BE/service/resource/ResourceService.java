package learningFlow.learningFlow_BE.service.resource;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.*;
import learningFlow.learningFlow_BE.repository.CollectionEpisodeRepository;
import learningFlow.learningFlow_BE.repository.UserEpisodeProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceService {
    private final UserEpisodeProgressRepository userEpisodeProgressRepository;
    private final CollectionEpisodeRepository collectionEpisodeRepository;
    // 유저 + 에피소드 조회
    // 존재하지 않을 경우 -> 처음 -> 객체 생성 -> 저장
    // 있을 경우 -> 에피소드 정보 불러오기
        // 처음인 경우 -> currentProgress = 0, embeddedUrl 생성
        // 처음이 아닐 경우 -> embeddedUrl, currentProgress 조회
    @Transactional
    public UserEpisodeProgress getUserEpisodeProgress(Long episodeId, String loginId){
        UserEpisodeProgressId userEpisodeProgressId = new UserEpisodeProgressId(episodeId, loginId);

        Optional<UserEpisodeProgress> episodeProgress = userEpisodeProgressRepository.findById(userEpisodeProgressId);

        return episodeProgress.orElseGet(() -> {
            CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                    .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
            Integer resourceQuantity = episode.getResource().getResourceQuantity();
            if (resourceQuantity == null) throw new ResourceHandler(ErrorStatus.QUANTITY_IS_NULL);
            UserEpisodeProgress userEpisodeProgress = new UserEpisodeProgress(userEpisodeProgressId, episode.getEpisodeNumber(), 0, episode.getResource().getResourceQuantity(), episode.getResource().getType());
            return userEpisodeProgressRepository.save(userEpisodeProgress);
        });
    }

    public Collection getCollection(Long episodeId) {
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        return episode.getCollection();
    }

    // 분량 체크
    public Integer getResourceQuantity(Long episodeId){
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId).get();
        Integer resourceQuantity = episode.getResource().getResourceQuantity();
        if (resourceQuantity == null){

        }
    }
}