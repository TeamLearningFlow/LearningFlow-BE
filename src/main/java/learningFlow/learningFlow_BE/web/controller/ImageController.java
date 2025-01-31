//package learningFlow.learningFlow_BE.web.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
//import learningFlow.learningFlow_BE.s3.AmazonS3Manager;
//import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
//import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/image")
//public class ImageController {
//
//    private final AmazonS3Manager amazonS3Manager;
//
//    @PostMapping(value = "/upload")
//    @Operation(
//            summary = "이미지 업로드 API",
//            description = """
//              회원가입 절차와 회원 정보 수정에서 이미지를 업로드하는 컨트롤러입니다.
//                """
//    )
//    public ApiResponse<String> imageUpload(@RequestPart MultipartFile image) {
//            String imgUrl = amazonS3Manager.uploadImageToS3(image);
//        return ApiResponse.onSuccess(imgUrl);
//    }
//}
