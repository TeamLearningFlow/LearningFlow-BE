package learningFlow.learningFlow_BE.service.embed;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import learningFlow.learningFlow_BE.repository.CollectionEpisodeRepository;
import learningFlow.learningFlow_BE.service.lambda.LambdaService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
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
/*    private final LambdaService lambdaService;
    public String getResource(Long episodeId){
        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        Resource resource = episode.getResource();
        // 블로그 임베드 url 미 생성인 경우
        if (resource.getType() == ResourceType.TEXT
                && resource.getClientUrl() == null) {
            return lambdaService.invokeLambda(u);
        }
        // 이미 생성된 경우
        return resource.getClientUrl();
    }*/

/*
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
        // 서버 과부화 문제로 이미지 + 자바스크립트 요청 X -> 나중에 지워야함
        options.addArguments("--blink-settings=imagesEnabled=false");  // ✅ 이미지 로드 방지
        options.addArguments("--disable-javascript");  // ✅ JavaScript 실행 방지

        // 🔹 User-Agent를 일반적인 브라우저처럼 설정
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        options.addArguments("--disable-blink-features=AutomationControlled");


        WebDriver driver = null;

        try {
            String seleniumUrl = "http://172.31.38.3:4444";
            driver = new RemoteWebDriver(new URL(seleniumUrl), options);

            // ✅ 페이지 로드 타임아웃 설정 (기본 무한대기 방지)
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));

            driver.get(blogUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // ✅ Duration.ofSeconds()로 변경
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
        catch (TimeoutException e) {
            killChromeProcesses();
            throw new RuntimeException("페이지 로드 시간이 초과되었습니다.", e);
        }
        catch (IOException e) {
            killChromeProcesses();
            throw new RuntimeException("Gzip 압축 중 오류 발생", e);  // IOException 처리
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    System.out.println("WebDriver 종료 중 오류 발생: " + e.getMessage());
                }
            }
        }
    }
*/
/*
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

    public void killChromeProcesses() {
        try {
            System.out.println("🔄 실행 중인 Chrome 프로세스를 정리 중...");
            Runtime.getRuntime().exec("pkill -f chrome");  // ✅ 실행 중인 모든 Chrome 프로세스 종료
            System.out.println("✅ Chrome 프로세스 정리 완료");
        } catch (IOException e) {
            System.err.println("🚨 [ERROR] Chrome 프로세스 강제 종료 실패: " + e.getMessage());
        }
    }*/
}