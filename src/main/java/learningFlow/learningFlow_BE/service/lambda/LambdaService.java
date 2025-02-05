package learningFlow.learningFlow_BE.service.lambda;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Service
public class LambdaService {

    private final LambdaClient lambdaClient;
    private final String functionName = "docker-selenium-lambda-prod-demo"; // 🚀 Lambda 함수 이름

    public LambdaService() {
        this.lambdaClient = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2) // 🚀 Lambda가 배포된 리전
                .credentialsProvider(DefaultCredentialsProvider.create()) // AWS 기본 자격 증명 사용
                .build();
    }

    // 🚀 Lambda 실행 (테스트용 - 기본 JSON 데이터 전달)
    public String invokeLambda(String url,int width, int height) {
        String payload = String.format("{\"url\":\"%s\", \"width\":%d, \"height\":%d}", url, width, height);

        // 🔹 SdkBytes 변환 (AWS SDK v2에서 payload는 byte[]가 아닌 SdkBytes 사용)
        SdkBytes payloadBytes = SdkBytes.fromUtf8String(payload);

        InvokeRequest request = InvokeRequest.builder()
                .functionName(functionName)
                .payload(payloadBytes)
                .build();

        // Lambda 실행
        InvokeResponse response = lambdaClient.invoke(request);

        // 응답을 문자열로 변환하여 반환
        return response.payload().asUtf8String();
    }
}
