package learningFlow.learningFlow_BE.web.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
@RestController
@RequestMapping("/proxy")
public class ProxyController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/blog")
    public ResponseEntity<byte[]> proxyBlog(@RequestParam String url) {
        try {
            // URL 검증 (기본적인 보안)
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return ResponseEntity.badRequest().body("Invalid URL".getBytes());
            }

            // 외부 URL로 요청
            URI targetUri = URI.create(url);
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0"); // 벨로그 같은 사이트에서 User-Agent 없으면 차단할 수도 있음

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(targetUri, HttpMethod.GET, entity, byte[].class);

            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error fetching URL: " + e.getMessage()).getBytes());
        }
    }
}
