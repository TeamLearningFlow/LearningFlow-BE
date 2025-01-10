package learningFlow.learningFlow_BE.web.dto.login;

import learningFlow.learningFlow_BE.web.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LoginResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDto {
        private UserResponseDto.UserInfoDto userInfo;
    }
}
