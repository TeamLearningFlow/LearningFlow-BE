package learningFlow.learningFlow_BE.service.embed;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogEmbedService {
    private final ResourceRepository resourceRepository;

    public Resource getResource(Long episodeId) {
        Resource resource = resourceRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.RESOURCE_NOT_FOUND));

        // 이미 변환된 URL이 존재하면 바로 반환
        if (resource.getClientUrl() != null) {
            return resource;
        }

        // 변환된 URL 생성 및 저장
        String embedUrl = getEmbedUrl(resource.getUrl());
        resource.setClientUrl(embedUrl);
        return resourceRepository.save(resource);
    }

    private String getEmbedUrl(String blogUrl) {
        if (blogUrl.contains("brunch.co.kr")) {
            return generateProxyUrl(blogUrl);
        } else if (blogUrl.contains("tistory.com")) {
            return generateProxyUrl(blogUrl);
        } else if (blogUrl.contains("naver.com")) {
            return generateProxyUrl(blogUrl);
        } else if (blogUrl.contains("velog.io")) {
            return generateProxyUrl(blogUrl);
        } else {
            throw new ResourceHandler(ErrorStatus.UNSUPPORTED_BLOG_PLATFORM);
        }
    }

    private String generateProxyUrl(String blogUrl) {
        return "/proxy/blog?url=" + blogUrl; // 저장될 변환된 URL
    }
}
