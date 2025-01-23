package learningFlow.learningFlow_BE.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/proxy")
@RequiredArgsConstructor
@Slf4j
public class ProxyController {
    private final RestTemplate restTemplate; // HTTP 요청을 보내는 Spring 제공 유틸리티, 외부 블로그 페이지의 HTML을 가져오기 위해 사용

    @GetMapping("/blog")
    public ResponseEntity<String> fetchBlogContent(@RequestParam String url) {
        try {
            HttpHeaders headers = new HttpHeaders(); // HTTP 요청 헤더를 생성
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

            HttpEntity<String> entity = new HttpEntity<>(headers); // HTTP 요청 엔티티를 생성 (헤더 포함).
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("블로그 데이터 가져오기 실패");
        }
    }
}
