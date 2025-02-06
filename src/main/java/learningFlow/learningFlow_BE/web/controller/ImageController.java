package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.s3.AmazonS3Manager;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@Tag(name = "Image", description = "이미지 업로드 API")
public class ImageController {

    private final AmazonS3Manager amazonS3Manager;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드", description = """
           프로필 이미지를 S3에 업로드합니다.
           - 지원 형식: JPG, JPEG, PNG
           - 최대 용량: 5MB
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(schema = @Schema(implementation = ImageUploadResponse.class))
            }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 이미지 형식"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "이미지 업로드 실패")
    })
    @Parameters({
            @Parameter(name = "image", description = "업로드할 이미지 파일", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    })
    public ApiResponse<String> imageUpload(@RequestPart MultipartFile image) {
        String imgUrl = amazonS3Manager.uploadImageToS3(image);
        return ApiResponse.onSuccess(imgUrl);
    }

    @Schema(name = "ImageUploadResponse")
    private class ImageUploadResponse {
        @Schema(description = "업로드된 이미지 URL",
                example = "https://bucket-name.s3.region.amazonaws.com/image-uuid")
        String imageUrl;
    }
}
