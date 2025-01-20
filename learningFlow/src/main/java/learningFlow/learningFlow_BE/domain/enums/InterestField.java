package learningFlow.learningFlow_BE.domain.enums;

import lombok.Getter;

@Getter
public enum InterestField { // 어떤 주제의 컬렉션인지, 관심분야 어떤건지 / 백엔드, 프론트엔드 등등
    BACKEND("백엔드 개발"),
    FRONTEND("프론트엔드 개발"),
    MOBILE("모바일 개발"),
    AI_ML("AI/머신러닝"),
    DEVOPS("데브옵스/인프라"),
    SECURITY("보안"),
    DATA_SCIENCE("데이터 사이언스"),
    UI_UX("UI/UX 디자인"),
    PROJECT_MANAGEMENT("프로젝트 관리"),
    GAME_DEV("게임 개발"),
    BLOCKCHAIN("블록체인"),
    CLOUD("클라우드 컴퓨팅");
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
