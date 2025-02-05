package learningFlow.learningFlow_BE.service.lambda;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class LambdaService {

    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;

    public String invokeLambda(String url,int width, int height) {
        try {
            String payload = String.format("{\"url\":\"%s\", \"width\":%d, \"height\":%d}", url, width, height);
            SdkBytes payloadBytes = SdkBytes.fromUtf8String(payload);

            // Lambda 요청
            InvokeRequest request = InvokeRequest.builder()
                    .functionName("docker-selenium-lambda-prod-demo")
                    .payload(payloadBytes)
                    .build();

            // Lambda 실행
            InvokeResponse response = lambdaClient.invoke(request);
            String responseJson = response.payload().asUtf8String();

            // JSON 파싱하여 `s3_url`만 추출
            JsonNode jsonResponse = objectMapper.readTree(responseJson);
            String s3Url = jsonResponse.path("body").path("s3_url").asText();

            if (s3Url == null || s3Url.isEmpty()) {
                log.error("❌ Lambda 응답에서 `s3_url`을 찾을 수 없음: {}", responseJson);
                throw new RuntimeException("Lambda 호출 실패: s3_url이 없음");
            }

            log.info("✅ Lambda 호출 성공: {}", s3Url);
            return s3Url;
        } catch (Exception e) {
            log.error("❌ Lambda 호출 중 예외 발생", e);
            throw new RuntimeException("Lambda 호출 실패", e);
        }
    }
}
