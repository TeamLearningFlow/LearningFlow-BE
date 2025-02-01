package learningFlow.learningFlow_BE.service.embed;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.repository.CollectionEpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPOutputStream;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogEmbedService {
    private final CollectionEpisodeRepository collectionEpisodeRepository;

    @Async // 비동기 처리
    public CompletableFuture<byte[]> getBlogSource(Long episodeId) {

        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        String blogUrl = episode.getResource().getUrl();

        // Headless 모드 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // GUI 없이 실행
        options.addArguments("--no-sandbox");  // 리눅스 환경에서 필요한 옵션
        options.addArguments("--disable-dev-shm-usage");  // 메모리 문제 방지
        options.addArguments("--disable-gpu");  // GPU 가속 비활성화 (필요 시)

        WebDriver driver = null;

        try {
            String seleniumUrl = "http://172.31.38.3:4444";
            driver = new RemoteWebDriver(new URL(seleniumUrl), options);

            driver.get(blogUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // ✅ Duration.ofSeconds()로 변경
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // JavaScript 실행 후 전체 HTML 가져오기
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String fullHTML = (String) js.executeScript("return document.documentElement.outerHTML;");

            // 광고 제거
            String cleanedHtml = removeAdsFromHtml(fullHTML);

            // JSON 포맷으로 변환
            String jsonResponse = "{\"html\":\"" + escapeJson(cleanedHtml) + "\"}";

            // Gzip 압축
            byte[] gzippedResponse = compressGzip(jsonResponse);

            return CompletableFuture.completedFuture(gzippedResponse);
        }
        catch (IOException e) {
            throw new RuntimeException("Gzip 압축 중 오류 발생", e);  // IOException 처리
        } finally {
            driver.quit();
        }
    }

    // 광고 코드 제거
    private String removeAdsFromHtml(String html) {
        return html.replaceAll("(?i)<script[^>]*>(.*?)</script>", "") // 모든 <script> 태그 제거
                .replaceAll("(?i)<iframe[^>]*>(.*?)</iframe>", "") // 모든 <iframe> 태그 제거
                .replaceAll("(?i)<div[^>]*adsbygoogle[^>]*>.*?</div>", "") // Google Ads div 제거
                .replaceAll("(?i)<ins[^>]*adsbygoogle[^>]*>.*?</ins>", ""); // Google 광고 제거
    }

    // Gzip 압축 적용
    private byte[] compressGzip(String data) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(data.getBytes(StandardCharsets.UTF_8));
            gzipStream.flush(); //  flush() 호출
        }
        return byteStream.toByteArray();
    }

    // JSON 문자열 이스케이프 처리
    private String escapeJson(String json) {
        return json.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
