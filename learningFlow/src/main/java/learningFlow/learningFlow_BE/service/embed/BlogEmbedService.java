package learningFlow.learningFlow_BE.service.embed;

import io.github.bonigarcia.wdm.WebDriverManager;
import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.handler.ResourceHandler;
import learningFlow.learningFlow_BE.domain.CollectionEpisode;
import learningFlow.learningFlow_BE.repository.CollectionEpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogEmbedService {
    private final CollectionEpisodeRepository collectionEpisodeRepository;
    @Transactional
    public String getBlogSource(Long episodeId) {

        CollectionEpisode episode = collectionEpisodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceHandler(ErrorStatus.EPISODE_NOT_FOUND));
        String blogUrl = episode.getResource().getUrl();

        WebDriverManager.chromedriver().setup();

        // test
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // 브라우저를 띄우지 않음 (필요 시 제거 가능)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver();

        try {
            driver.get(blogUrl);
            Thread.sleep(50000); // 5초 동안 브라우저가 열린 상태로 유지
            return driver.getPageSource();// 블로그 페이지의 HTML 반환
        }  catch (InterruptedException e){
            throw new RuntimeException(e);
        }
        finally {
            driver.quit(); // WebDriver 종료
        }
    }
}
