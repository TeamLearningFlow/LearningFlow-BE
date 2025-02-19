package learningFlow.learningFlow_BE.web.dto.user;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.*;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.Job;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import lombok.Getter;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UserRequestDTO {

    @Getter
    public static class InitialRegisterDTO {
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수 입력값입니다")
        @Pattern(regexp = "^[^\\s]+@[^\\s]+\\.[^\\s]+$",
                message = "이메일에 공백이 포함될 수 없습니다")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])(?!.*\\s)[A-Za-z\\d@$!%*?&]{8,16}$",
                message = "비밀번호는 8-16자 사이, 영문 대/소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 하며 공백은 포함할 수 없습니다.")
        String password;
    }

    @Getter
    public static class CompleteRegisterDTO {
        @NotBlank(message = "이름은 필수 입력값입니다")
        String name;

        @NotNull(message = "직업은 필수 선택값입니다")
        Job job;

        @NotEmpty(message = "관심 분야는 최소 1개 이상 선택해야 합니다")
        @Size(max = 3, message = "관심 분야는 최대 3개까지만 선택이 가능합니다.")
        List<InterestField> interestFields;

        @NotNull(message = "선호하는 미디어 타입은 필수 선택값입니다")
        MediaType preferType;

        @NotEmpty
        String imgProfileUrl;

        // ✅ 기본 생성자에서 기본값 설정
        public CompleteRegisterDTO() {
            this.imgProfileUrl = "https://learningflow.s3.ap-northeast-2.amazonaws.com/%EC%98%A8%EB%B3%B4%EB%94%A9+%ED%8E%98%EC%9D%B4%EC%A7%80%EC%9A%A9.svg";
        }

        // ✅ @JsonSetter 사용 (JSON에서 필드가 누락된 경우 기본값 설정)
        @JsonSetter
        public void setImgProfileUrl(String imgProfileUrl) {
            this.imgProfileUrl = (imgProfileUrl == null || imgProfileUrl.isEmpty())
                    ? "https://learningflow.s3.ap-northeast-2.amazonaws.com/%EC%98%A8%EB%B3%B4%EB%94%A9+%ED%8E%98%EC%9D%B4%EC%A7%80%EC%9A%A9.svg"
                    : imgProfileUrl;
        }
    }

    @Getter
    public static class UserLoginDTO {
        @Email(message = "올바른 이메일 형식이어야 합니다")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        String password;

        boolean remember = false;
    }

    @Getter
    public static class AdditionalInfoDTO {

        @NotBlank(message = "이름은 필수 입력값입니다")
        String name;

        @NotNull(message = "직업은 필수 선택값입니다")
        Job job;

        @NotEmpty(message = "관심 분야는 최소 1개 이상 선택해야 합니다")
        @Size(max = 3, message = "관심 분야는 최대 3개까지만 선택이 가능합니다.")
        List<InterestField> interestFields;
        @NotNull(message = "선호하는 미디어 타입은 필수 선택값입니다")
        MediaType preferType;

        private String imgProfileUrl;


        // ✅ 기본 생성자에서 기본값 설정
        public AdditionalInfoDTO() {
            this.imgProfileUrl = "https://learningflow.s3.ap-northeast-2.amazonaws.com/%EC%98%A8%EB%B3%B4%EB%94%A9+%ED%8E%98%EC%9D%B4%EC%A7%80%EC%9A%A9.svg";
        }

        // ✅ @JsonSetter 사용 (JSON에서 필드가 누락된 경우 기본값 설정)
        @JsonSetter
        public void setImgProfileUrlUrl(String imgProfileUrl) {
            this.imgProfileUrl = (imgProfileUrl == null || imgProfileUrl.isEmpty())
                    ? "https://learningflow.s3.ap-northeast-2.amazonaws.com/%EC%98%A8%EB%B3%B4%EB%94%A9+%ED%8E%98%EC%9D%B4%EC%A7%80%EC%9A%A9.svg"
                    : imgProfileUrl;
        }
    }

    @Getter
    public static class FindPasswordDTO {
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수 입력값입니다")
        String email;
    }

    @Getter
    public static class ResetEmailDTO {
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수 입력값입니다")
        String email;
    }

    @Getter
    public static class ResetPasswordDTO {
        @NotBlank(message = "현재 비밀번호는 필수 입력값입니다")
        String currentPassword;

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])(?!.*\\s)[A-Za-z\\d@$!%*?&]{8,16}$",
                message = "비밀번호는 8-16자 사이, 영문 대/소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 하며 공백은 포함할 수 없습니다.")
        String newPassword;
    }

    @Getter
    public static class UpdateUserDTO {
        String name;
        Job job;
        List<InterestField> interestFields;
        String imgProfileUrl;
        String imgBannerUrl;

    }
}

