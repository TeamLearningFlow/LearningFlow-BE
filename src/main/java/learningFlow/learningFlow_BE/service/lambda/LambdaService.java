package learningFlow.learningFlow_BE.service.lambda;

import com.amazonaws.SdkClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import learningFlow.learningFlow_BE.domain.Resource;
import learningFlow.learningFlow_BE.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LambdaService {

    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;
    private final ResourceRepository resourceRepository;

    public void ClientUrlUpdate(Resource resource, String clientUrl){
        resource.setClientUrl(clientUrl);
        resourceRepository.save(resource);
    }
    public String invokeLambda(String url,int width, int height, Resource resource) {
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
            // 🔹 Lambda 내부 오류 체크
            if (response.functionError() != null) {
                log.error("❌ Lambda 내부 오류 발생: {}", response.functionError());
                throw new RuntimeException("Lambda 내부 오류: " + response.functionError());
            }
            // ✅ Lambda 응답을 JSON으로 변환
            JsonNode jsonResponse = objectMapper.readTree(responseJson);

            // ✅ Lambda 응답에서 `body` 추출 (JSON이 아닌 문자열일 가능성이 있음)
            String bodyString = jsonResponse.path("body").asText();

            // ✅ `body`가 이미 JSON 형식이라면, `asText()`가 아닌 `objectMapper.readTree()` 사용
            JsonNode bodyJson = objectMapper.readTree(bodyString);

            // ✅ `s3_url`을 올바르게 추출 (불필요한 `body` 중첩 제거)
            String s3Url = bodyJson.path("s3_url").asText();

            if (s3Url == null || s3Url.isEmpty()) {
                log.error("❌ Lambda 응답에서 `s3_url`을 찾을 수 없음: {}", responseJson);
                throw new RuntimeException("Lambda 호출 실패: s3_url이 없음");
            }

            ClientUrlUpdate(resource, s3Url);
            return s3Url;
        } catch (IOException e) {
            log.error("❌ JSON 파싱 오류 발생", e);
            return null; // JSON 파싱 오류 시에도 예외를 던지지 않고 null 반환
        } catch (SdkClientException e) {
            log.error("❌ Lambda 호출 실패 (네트워크 또는 AWS 문제)", e);
            return null; // AWS Lambda 호출 관련 예외 발생 시 null 반환
        } catch (Exception e) {
            log.error("❌ Lambda 호출 중 알 수 없는 예외 발생", e);
            return null;
        }
    }
}
