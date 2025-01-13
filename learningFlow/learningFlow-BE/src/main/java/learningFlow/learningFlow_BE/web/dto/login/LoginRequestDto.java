package learningFlow.learningFlow_BE.web.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class LoginRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterDto {
        String email;
        String password;
        String name;
        String job;
        List<String> interestFields;
        LocalDate birthDay;
        String gender;
        String preferType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDto {
        String email;
        String password;
    }
}
