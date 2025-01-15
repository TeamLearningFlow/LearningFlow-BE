package learningFlow.learningFlow_BE.web.dto.memo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemoResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MemoInfoDTO {
        String memoContents;
    }
}
