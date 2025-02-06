package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.UserCollection;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO;
import learningFlow.learningFlow_BE.web.dto.user.UserResponseDTO.UserInfoDTO;

import java.util.List;

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

    public static UserInfoDTO convertToUserInfoDTO(User user) {
        return UserInfoDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .job(user.getJob())
                .interestFields(user.getInterestFields())
                .preferType(user.getPreferType())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    public static UserResponseDTO.UserMyPageResponseDTO convertToUserMyPageResponseDTO(
            User user,
            List<ResourceResponseDTO.RecentlyWatchedEpisodeDTO> recentlyWatchedEpisodeDTOList,
            List<CollectionResponseDTO.CollectionPreviewDTO> completedCollectionList
    ) {

        return UserResponseDTO.UserMyPageResponseDTO.builder()
                .userPreviewDTO(convertToUserPreviewDTO(user))
                .recentlyWatchedEpisodeList(recentlyWatchedEpisodeDTOList)
                .completedCollectionList(completedCollectionList)
                .build();
    }

    private static UserResponseDTO.UserPreviewDTO convertToUserPreviewDTO(User user) {

        return UserResponseDTO.UserPreviewDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .job(user.getJob().getDescription())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }
}
