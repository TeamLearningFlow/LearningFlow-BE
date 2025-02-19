package learningFlow.learningFlow_BE.web.dto.user;

import learningFlow.learningFlow_BE.domain.enums.*;
import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import learningFlow.learningFlow_BE.web.dto.resource.ResourceResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


public class UserResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserInfoDTO {
        String name;
        String email;
        Job job;
        List<InterestField> interestFields;
        MediaType preferType;
        String profileImgUrl;
        String bannerImgUrl;
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
        String loginId;
        String email;
        String name;
        Role role;
        SocialType socialType;
        String profileImgUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserMyPageResponseDTO {
        UserPreviewDTO userPreviewDTO;
        List<ResourceResponseDTO.RecentlyWatchedEpisodeDTO> recentlyWatchedEpisodeList;
        List<CollectionResponseDTO.CollectionPreviewDTO> completedCollectionList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPreviewDTO {
        String name;
        String email;
        String job;
        String profileImgUrl;
        String bannerImgUrl;
    }
}
