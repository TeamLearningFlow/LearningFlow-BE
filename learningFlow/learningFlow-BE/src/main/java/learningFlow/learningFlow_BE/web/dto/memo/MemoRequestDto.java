package learningFlow.learningFlow_BE.web.dto.memo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class MemoRequestDto {

    @Getter
    public static class MemoJoinDto {

        @NotBlank(message = "메모 내용은 필수입니다.")
        private String contents;

    }
}
