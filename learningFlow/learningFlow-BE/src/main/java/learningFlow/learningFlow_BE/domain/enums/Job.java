package learningFlow.learningFlow_BE.domain.enums;

import lombok.Getter;

@Getter
public enum Job {
    STUDENT("대학생(휴학생)"),
    ADULT("성인"),
    EMPLOYEE("직장인"),
    JOB_SEEKER("이직/취업 준비생"),
    OTHER("기타");

    private final String description;

    Job(String description) {
        this.description = description;
    }
}
