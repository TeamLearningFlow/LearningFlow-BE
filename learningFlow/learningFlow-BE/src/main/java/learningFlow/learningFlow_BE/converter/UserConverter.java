package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;

public class UserConverter {

    public static UserResponseDTO.UserLoginResponseDTO toUserLoginResponseDTO(User user) {

        return UserResponseDTO.UserLoginResponseDTO.builder()
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .socialType(user.getSocialType())
                .build();
    }
}
