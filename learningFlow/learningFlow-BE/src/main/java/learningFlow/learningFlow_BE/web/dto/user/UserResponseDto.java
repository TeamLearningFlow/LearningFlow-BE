package learningFlow.learningFlow_BE.web.dto.user;

import learningFlow.learningFlow_BE.domain.Image;
import learningFlow.learningFlow_BE.domain.User;
import learningFlow.learningFlow_BE.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class UserResponseDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserInfoDto {
        String name;
        /**
         * 어떤 필드들을 사용자 정보 조회 시에 보여줘야 할지 아직 안정해서 비워두었습니다.
         */
        // TODO: 사용자 정보 조회 시 DTO를 통해 보여줄 필드 정하기
    }

}
