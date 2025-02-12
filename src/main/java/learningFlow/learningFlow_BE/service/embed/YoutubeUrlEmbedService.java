package learningFlow.learningFlow_BE.service.embed;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import learningFlow.learningFlow_BE.repository.CollectionEpisodeRepository;
import learningFlow.learningFlow_BE.repository.ResourceRepository;
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
    private final ResourceRepository resourceRepository;
    public Resource getResource(Long episodeId){
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        Resource resource = episode.getResource();
        // 유튜브고 임베드 url 미 생성인 경우
        if (resource.getType() == ResourceType.VIDEO
                && resource.getClientUrl() == null) {
            String url = EmbedUrl(resource.getUrl());
            resource.setClientUrl(url);
            return resourceRepository.save(resource);
        }
        // 이미 생성된 경우
        return resource;
    }

    public String EmbedUrl(String youtubeUrl){
        try {
            URI uri = new URI(youtubeUrl);
            String host = uri.getHost();
            String query = uri.getQuery();
            String path = uri.getPath();
            String adExistUrl = null;
            // 기본 형식: https://www.youtube.com/watch?v=<videoId>
            if (host.contains("youtube.com") && query != null && query.contains("v=")){
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("v=")){
                        String videoId = param.substring(2);
                        return  "https:///www.youtube-nocookie.com/embed" + videoId;
                    }
                }
            }
            // 축약형: https://youtu.be/<videoId>
            if (host.contains("youtube.be") && path != null && path.length() > 1) {
                String videoId = path.substring(1); // 맨 앞 "/" 제거
                return "https:///www.youtube-nocookie.com/embed/" + videoId;
            }
            throw new ResourceHandler(ErrorStatus.YOUTUBE_URI_SYNTAX_ERROR);
        } catch (URISyntaxException e) {
            throw new ResourceHandler(ErrorStatus.URI_SYNTAX_ERROR);
        }
    }
}
