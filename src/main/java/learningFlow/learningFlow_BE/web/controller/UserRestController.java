package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.s3.AmazonS3Manager;
import learningFlow.learningFlow_BE.security.auth.PrincipalDetails;
import learningFlow.learningFlow_BE.service.user.UserService;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserRequestDTO.UpdateUserDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
@Slf4j
@Tag(name = "User", description = "사용자 정보 관리 및 마이페이지 API")
public class UserRestController {

    private final UserService userService;
    private final AmazonS3Manager amazonS3Manager;

//    @PostMapping(value = "/imgUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "이미지 업로드 API", description = "회원가입 절차에서 이미지를 업로드하는 API")
//    public ApiResponse<String> imageUpload(@RequestPart MultipartFile image) {
//        String imgUrl = amazonS3Manager.uploadImageToS3(image);
//        return ApiResponse.onSuccess(imgUrl); // ✅ 프론트에서 이 URL을 저장하고 사용
//    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "사용자 정보 수정 API", description = """
            로그인한 사용자의 프로필 정보를 수정하는 API입니다.
            
            [수정 가능 항목]
            1. 이름 (선택)
               - 실명 또는 닉네임
               
            2. 직업 (선택)
               - STUDENT: 대학생(휴학생)
               - ADULT: 성인
               - EMPLOYEE: 직장인
               - JOB_SEEKER: 이직/취업 준비생
               - OTHER: 기타
               
            3. 관심분야 (다중선택)
               - APP_DEVELOPMENT: 앱개발
               - WEB_DEVELOPMENT: 웹개발
               - PROGRAMMING_LANGUAGE: 컴퓨터언어
               - DEEP_LEARNING: 딥러닝
               - STATISTICS: 통계
               - DATA_ANALYSIS: 데이터분석
               - UI_UX: UX/UI
               - PLANNING: 기획
               - BUSINESS_PRODUCTIVITY: 업무생산성
               - FOREIGN_LANGUAGE: 외국어
               - CAREER: 취업
               
            4. 프로필 이미지 URL (선택)
               - 이미지 업로드 API를 통해 받은 URL 사용
               - 미입력시 기존(기본) 이미지 유지
               
            5. 배너 이미지 URL (선택)
               - 이미지 업로드 API를 통해 받은 URL 사용
               - 미입력시 기존(기본) 이미지 유지
            
            [응답 정보]
            수정된 사용자 정보 반환:
            - 이름, 이메일, 직업
            - 관심분야 목록
            - 프로필 이미지 URL
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.")
    })
    public ApiResponse<UserInfoDTO> updateUserInfo(
            @RequestBody @Valid UpdateUserDTO updateUserDTO, // ✅ JSON 데이터 - application/json
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                userService.updateUserInfo(principalDetails.getUser().getLoginId(), updateUserDTO)
        );
    }

    @GetMapping
    @Operation(summary = "사용자 조회 API", description = "사용자를 찾기 위한 API 입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.")
    })
    public ApiResponse<UserInfoDTO> getUserInfo(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                userService.getUserInfo(principalDetails.getUser().getLoginId())
        );
    }

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회 API", description = """
           사용자의 학습 현황과 완료한 컬렉션 목록을 조회합니다.
           
           [조회 정보]
           1. 사용자 프로필 정보
              - 이름, 이메일
              - 직업 (STUDENT/ADULT/EMPLOYEE/JOB_SEEKER/OTHER)
              - 프로필 이미지 URL
              
           2. 최근 학습 중인 에피소드 목록
              - 리소스 ID, 에피소드 번호
              - 컬렉션 정보 (ID, 제목)
              - 리소스 출처
              - 현재 진행률 (비디오: 시청 시간, 텍스트: 스크롤 위치)
              - 전체 진행률 (시청 완료 여부)
              
           3. 완료한 컬렉션 목록
              - 컬렉션 기본 정보 (제목, 생성자, 키워드 등)
              - 학습 기간 (시작일, 완료일)
              - 리소스 구성 (텍스트/영상 수)
              - 전체 학습 시간
              
           [정렬 기준]
           - 최근 학습: 최근 접근 순
           - 완료 컬렉션: 완료일 기준 내림차순
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.")
    })
    public ApiResponse<UserResponseDTO.UserMyPageResponseDTO> getMyPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(userService.getUserMyPageResponseDTO(principalDetails.getUser().getLoginId()));
    }

    @GetMapping("/likes")
    @Operation(summary = "좋아요한 컬렉션 조회 API", description = """
           사용자가 좋아요 표시한 컬렉션 목록을 조회합니다.
           
           [조회 결과]
           1. 컬렉션 기본 정보
              - ID, 제목, 생성자
              - 관심분야, 키워드
              - 난이도 (입문/초급/중급/실무) - 실무는 백엔드상에서만 존재.
              - 리소스 구성 (텍스트/영상 수)
              
           2. 학습 현황
              - 학습 상태 (시작 전/진행 중/완료)
              - 진행률, 시작일, 완료일
              - 현재 학습 중인 리소스 정보
              
           3. 좋아요 정보
              - 전체 좋아요 수
              - 본인의 좋아요 상태
           
           [페이지네이션]
           - 커서 기반 페이징
           - 한 페이지당 8개 조회
           - 좋아요 시간 기준 내림차순 정렬
           """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "로그인이 필요한 서비스입니다.")
    })
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (1부터 시작)")
    })
    public ApiResponse<CollectionResponseDTO.SearchResultDTO> getBookmarkedCollections(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ApiResponse.onSuccess(
                userService.getBookmarkedCollections(principalDetails.getUser().getLoginId(), page)
        );
    }
}