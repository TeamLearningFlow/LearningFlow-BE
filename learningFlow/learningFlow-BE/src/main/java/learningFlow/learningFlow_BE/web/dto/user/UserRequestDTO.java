package learningFlow.learningFlow_BE.web.dto.user;

import jakarta.validation.constraints.*;
import learningFlow.learningFlow_BE.domain.enums.Category;
import learningFlow.learningFlow_BE.domain.enums.Gender;
import learningFlow.learningFlow_BE.domain.enums.Job;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class UserRequestDTO {

    @Getter
    public static class UserRegisterDTO {
        @NotBlank(message = "이름은 필수 입력값입니다")
        private String name;

        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수 입력값입니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])\\S{8,}$",
                message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 하며, 공백은 허용되지 않습니다.")
        private String password;

        @NotNull(message = "직업은 필수 선택값입니다")
        private Job job;

        @NotEmpty(message = "관심 분야는 최소 1개 이상 선택해야 합니다")
        private List<Category> interestFields;

        @NotNull(message = "생년월일은 필수 입력값입니다")
        private LocalDate birthDay;

        @NotNull(message = "성별은 필수 선택값입니다")
        private Gender gender;

        @NotNull(message = "선호하는 미디어 타입은 필수 선택값입니다")
        private MediaType preferType;

        private String imageUrl;  // 선택적 입력값
    }

    @Getter
    public static class UserLoginDTO {
        @Email(message = "올바른 이메일 형식이어야 합니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        private String password;

        private boolean rememberMe = false;
    }

    @Getter
    public static class AdditionalInfoDTO {
        @NotNull(message = "직업은 필수 선택값입니다")
        private Job job;

        @NotEmpty(message = "관심 분야는 최소 1개 이상 선택해야 합니다")
        private List<Category> interestFields;

        @NotNull(message = "생년월일은 필수 입력값입니다")
        private LocalDate birthDay;

        @NotNull(message = "성별은 필수 선택값입니다")
        private Gender gender;

        @NotNull(message = "선호하는 미디어 타입은 필수 선택값입니다")
        private MediaType preferType;
    }

    @Getter
    public static class FindPasswordDTO {
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수 입력값입니다")
        private String email;
    }

    @Getter
    public static class ResetPasswordDTO {
        @NotBlank(message = "토큰은 필수 입력값입니다")
        private String token;

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])\\S{8,}$",
                message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 하며, 공백은 허용되지 않습니다.")
        private String newPassword;
    }
}
