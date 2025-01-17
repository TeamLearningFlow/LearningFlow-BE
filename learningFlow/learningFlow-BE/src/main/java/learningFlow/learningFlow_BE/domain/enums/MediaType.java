package learningFlow.learningFlow_BE.domain.enums;

import lombok.Getter;

@Getter
public enum MediaType {
    NO_PREFERENCE("상관없음"),
    VIDEO("영상이 좋아요"),
    TEXT("텍스트가 좋아요");

    private final String description;

    MediaType(String description) {
        this.description = description;
    }
}
