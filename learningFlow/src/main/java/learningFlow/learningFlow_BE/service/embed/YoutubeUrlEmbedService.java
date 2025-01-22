package learningFlow.learningFlow_BE.service.embed;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import learningFlow.learningFlow_BE.repository.CollectionEpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
@Transactional
public class YoutubeUrlEmbedService {
    private final CollectionEpisodeRepository collectionEpisodeRepository;
    public Resource getResource(Long episodeId){
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        Resource resource = episode.getResource();
        // 유튜브고 임베드 url 미 생성인 경우
        if (resource.getType() == ResourceType.VIDEO
                && resource.getClientUrl() == null) {
            String url = EmbedUrl(resource.getUrl());

        }
    }

    public String EmbedUrl(String youtubeUrl){
        try {
            URI uri = new URI(youtubeUrl);
            String host = uri.getHost();
            String query = uri.getQuery();
            // 기본 형식: https://www.youtube.com/watch?v=<videoId>
            if (host.contains("youtube.com") && query != null && query.contains("v=")){
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("v=")){
                        String videoId = param.substring(2);
                        return "https://www.youtube.com/embed/" + videoId;
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


}
