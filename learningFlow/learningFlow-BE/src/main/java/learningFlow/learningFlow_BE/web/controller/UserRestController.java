package learningFlow.learningFlow_BE.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import learningFlow.learningFlow_BE.apiPayload.ApiResponse;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
@Slf4j
@Tag(name = "User", description = "사용자 관련 API")
public class UserRestController {

    @PutMapping("/user") // TODO : 이 부분 PutMapping, PatchMapping 중에 뭐가 맞을지 얘기해봐야 할 것 같아요...
    @Operation(summary = "사용자 정보 수정 API", description = "사용자 정보를 수정하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<UserResponseDTO.UserInfoDTO> updateUserInfo(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User user
    ) {
        // TODO: 사용자 정보 수정 로직 구현
        /**
         * RequestBody를 Map으로 구현한 이유
         * 사용자가 일부 필드만 수정한 경우 해당 필드에 대해서만 requestBody에 담겨져 오기 때문에 따로 정해진 DTO 두지 않고
         * Map으로 두면 JSON이 자동으로 Map에 매핑이 되어서 Map에서 Key값 확인하고 해당 필드에 대해서만(로직 따로 구현) 수정 진행하면 됩니당.
         * {
         *   "name": "새로운 이름",
         *   "nickname": "새로운 닉네임"
         * }
         * 이 Request-Body로 오면
         * requestBody.put("name", "새로운 이름");
         * requestBody.put("nickname", "새로운 닉네임");
         * 이런식으로 spring이 알아서 넣어준다.
         * {
         *   "name": "새로운 이름",
         * }
         * 이렇게만 오면 requestBody.put("name", "새로운 이름"); 이것만 수행해준다.
         * 이렇게 하면 저렇게 동적으로 원하는 필드만 오는 경우에 request-body에서 가져올 수 있는 대신 로직이 살짝 복잡해지긴 할 것 같아요.
         *
         * 이 방식이 어려울 것 같으면.. 항상 모든 필드를 전부 다 전송 받게끔 하는 DTO 만들게 하는게 구현은 쉬울 것 같아서 나중에 생각해봐야 할 부분!
         */

        /**
         * Spring Security 사용하기 때문에 @AuthenticationPrincipal를 통해 바로 현재 인증된 사용자의 정보를 가져올 수 있어요
         */

        /**
         * 반환 타입은 UserInfoDto로 정보 조회 시에 User에게 보여줄 적당한 필드 UserInfoDto에 추가하면 될 것 같아요.
         * 현재는 가장 기본인 name 필드만 넣어놓았어요.(아무 필드도 안넣으면 실행이 안되서..)
         */

        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/user")
    @Operation(summary = "사용자 정보 조회 API", description = "로그인한 사용자의 정보를 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<UserResponseDTO.UserInfoDTO> getUserInfo(@AuthenticationPrincipal User user) {

        /**
         * Spring Security 사용하기 때문에 @AuthenticationPrincipal를 통해 바로 현재 인증된 사용자의 정보를 가져올 수 있어요.
         */

        /**
         * 반환 타입은 UserInfoDto로 정보 조회 시에 User에게 보여줄 적당한 필드 UserInfoDto에 추가하면 될 것 같아요.
         * 현재는 가장 기본인 name 필드만 넣어놓았어요.(아무 필드도 안넣으면 실행이 안되서..)
         */

        // TODO: 사용자 정보 조회 로직 구현
        return ApiResponse.onSuccess(null);
    }

}
