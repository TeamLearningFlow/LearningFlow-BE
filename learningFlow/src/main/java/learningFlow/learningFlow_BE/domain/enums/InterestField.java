package learningFlow.learningFlow_BE.domain.enums;

import lombok.Getter;

@Getter
public enum InterestField { // 어떤 주제의 컬렉션인지, 관심분야 어떤건지 / 백엔드, 프론트엔드 등등
    APP_DEVELOPMENT("앱개발"),
    WEB_DEVELOPMENT("웹개발"),
    PROGRAMMING_LANGUAGE("컴퓨터언어"),
    DEEP_LEARNING("딥러닝"),
    STATISTICS("통계"),
    DATA_ANALYSIS("데이터분석"),
    UI_UX("UX/UI"),
    PLANNING("기획"),
    BUSINESS_PRODUCTIVITY("업무생산성"),
    FOREIGN_LANGUAGE("외국어"),
    CAREER("취업");


    private final String description;

    InterestField(String description) {
        this.description = description;
    }
}
