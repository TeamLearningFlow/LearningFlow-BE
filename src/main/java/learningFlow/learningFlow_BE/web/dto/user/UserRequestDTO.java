package learningFlow.learningFlow_BE.web.dto.user;

import jakarta.validation.constraints.*;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.Gender;
import learningFlow.learningFlow_BE.domain.enums.Job;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import lombok.Getter;

import java.time.LocalDate;
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

        @NotNull(message = "성별은 필수 선택값입니다")
        Gender gender;

        @NotNull(message = "선호하는 미디어 타입은 필수 선택값입니다")
        MediaType preferType;
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
        @NotNull(message = "직업은 필수 선택값입니다")
        Job job;

        @NotEmpty(message = "관심 분야는 최소 1개 이상 선택해야 합니다")
        @Size(max = 3, message = "관심 분야는 최대 3개까지만 선택이 가능합니다.")
        List<InterestField> interestFields;

        @NotNull(message = "성별은 필수 선택값입니다")
        Gender gender;

        @NotNull(message = "선호하는 미디어 타입은 필수 선택값입니다")
        MediaType preferType;
    }

    @Getter
    public static class FindPasswordDTO {
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수 입력값입니다")
        String email;
    }

    @Getter
    public static class ResetPasswordDTO {
        @NotBlank(message = "토큰은 필수 입력값입니다")
        String token;

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
    }
}

