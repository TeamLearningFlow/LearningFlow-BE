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
            List<UserCollection> inProgressUserCollectionList,
            List<UserCollection> completedUserCollectionList
    ) {
        List<ResourceResponseDTO.RecentlyWatchedEpisodeDTO> recentlyWatchedEpisodeList = inProgressUserCollectionList.stream()
                .map(ResourceConverter::convertToRecentlyWatchedEpisodeDTO).toList();

        List<CollectionResponseDTO.CompletedCollectionDTO> completedCollectionList = completedUserCollectionList.stream()
                .map(CollectionConverter::convertToCompletedCollectionDTO).toList();

        return UserResponseDTO.UserMyPageResponseDTO.builder()
                .recentlyWatchedEpisodeList(recentlyWatchedEpisodeList)
                .completedCollectionList(completedCollectionList)
                .build();
    }
}
