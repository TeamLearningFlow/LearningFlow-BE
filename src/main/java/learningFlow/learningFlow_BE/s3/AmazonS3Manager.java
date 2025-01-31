package learningFlow.learningFlow_BE.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.GeneralException;
import learningFlow.learningFlow_BE.config.AmazonConfig;
import learningFlow.learningFlow_BE.domain.uuid.Uuid;
import learningFlow.learningFlow_BE.domain.uuid.UuidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType()); // Content-Type 설정
        metadata.setContentDisposition("inline");       // Content-Disposition 설정

        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));

        } catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
            throw new GeneralException(ErrorStatus.IMAGE_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String generateKeyName(Uuid uuid) {
        return uuid.getUuid();
    }

    public String uploadImageToS3(MultipartFile imageFile) {
        try {
            // UUID 생성 및 저장
            String imageUuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(imageUuid).build());

            // 이미지 업로드
            String imageKey = generateKeyName(savedUuid); // KeyName 생성
            String imageUrl = uploadFile(imageKey, imageFile); // 업로드된 URL 반환

            // 업로드 성공 여부 확인
            if (imageUrl == null || imageUrl.isEmpty()) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST); // 업로드 실패 시 예외 처리
            }

            return imageUrl; // 성공 시 URL 반환

        } catch (GeneralException e) {
            log.error("이미지 업로드 실패: {}", e.getMessage());
            throw e; // GeneralException은 그대로 전달
        } catch (Exception e) {
            log.error("이미지 업로드 중 내부 오류 발생: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.IMAGE_UPLOAD_FAILED); // 기타 예외 처리
        }
    }


}
