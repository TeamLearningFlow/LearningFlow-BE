package learningFlow.learningFlow_BE.web.controller;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumTest {
    public static void main(String[] args) {
        // WebDriver 자동 설정
        WebDriverManager.chromedriver().setup();

        // Chrome 브라우저 실행
        WebDriver driver = new ChromeDriver();

        // Google 페이지 접속
        driver.get("https://www.google.com");

        // 현재 페이지 제목 출력
        System.out.println("Page Title: " + driver.getTitle());

        // 브라우저 종료
        driver.quit();
    }
}
