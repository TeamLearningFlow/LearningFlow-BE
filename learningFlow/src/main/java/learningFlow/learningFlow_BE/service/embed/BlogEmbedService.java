package learningFlow.learningFlow_BE.service.embed;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogEmbedService {
    private final ResourceRepository resourceRepository;

    public Resource getResource(Long episodeId) {
        Resource resource = resourceRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.RESOURCE_NOT_FOUND));

        // 이미 변환된 URL이 존재하면 바로 반환
        if (resource.getClientUrl() == null) {
            String proxyUrl = "/proxy/blog?url=" + URLEncoder.encode(resource.getUrl(), StandardCharsets.UTF_8);
            resource.setClientUrl(proxyUrl);
            return resourceRepository.save(resource);
        }

        return resource;
    }
}
