package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO.UserInfoDTO;
import org.springframework.stereotype.Component;

@Component
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

    public UserInfoDTO convertToUserInfoDTO(User user) {
        return UserInfoDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .job(user.getJob())
                .interestFields(user.getInterestFields())
                .gender(user.getGender())
                .preferType(user.getPreferType())
                .profileImageUrl(user.getImage() != null ? user.getImage().getImageURL() : null)
                .build();
    }
}
