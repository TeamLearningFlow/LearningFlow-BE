package learningFlow.learningFlow_BE.web.dto.user;

import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.Role;
import learningFlow.learningFlow_BE.domain.enums.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class UserResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserInfoDTO {
        String name;
        /**
         * 어떤 필드들을 사용자 정보 조회 시에 보여줘야 할지 아직 안정해서 비워두었습니다.
         */
        // TODO: 사용자 정보 조회 시 DTO를 통해 보여줄 필드 정하기
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLoginResponseDTO {
        private String loginId;
        private String email;
        private String name;
        private Role role;
        private SocialType socialType;

        public static UserLoginResponseDTO from(User user) {
            return UserLoginResponseDTO.builder()
                    .loginId(user.getLoginId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole())
                    .socialType(user.getSocialType())
                    .build();
        }
    }
}
