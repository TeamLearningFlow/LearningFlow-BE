package learningFlow.learningFlow_BE.apiPayload.code.status;

import learningFlow.learningFlow_BE.apiPayload.code.BaseErrorCode;
import learningFlow.learningFlow_BE.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 멤버 관려 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "사용자를 찾을 수 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4002", "닉네임은 필수 입니다."),

    //Resources 관련 에어
    RESOURCES_NOT_FOUND(HttpStatus.NOT_FOUND,"RESOURCE4001","강의 에피소드를 찾을 수 없습니다."),
    QUANTITY_IS_NULL(HttpStatus.BAD_REQUEST, "RESOURCE4002", "분량이 존재하지 않습니다"),
    //페이징 시 페이지 범위를 벗어났을 때
    PAGE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "PAGE4001","페이지 범위에 맞지 않는 페이지 값 입니다."),

    //컬렉션 관련 에러
    COLLECTION_NOT_FOUND(HttpStatus.NOT_FOUND,"COLLECTION4001","해당 컬렉션을 찾을 수 없습니다."),

    NO_MORE_COLLECTION(HttpStatus.NOT_FOUND,"COLLECTION4002","더 이상 컬렉션이 존재하지 않습니다."),

    //계정 로그인 관련 에러
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH4001", "로그인이 필요한 서비스입니다."),

    // 예시,,,
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다."),


    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"EMAIL4001" ,"이미 동일한 이메일로 생성된 계정이 존재합니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "PASSWORD4001", "유효하지 않은 비밀번호입니다."),

    // 컬렉션 에피소드 에러
    EPISODE_NOT_FOUND(HttpStatus.NOT_FOUND, "EPISODE4001", "존재하지 않는 에피소드 입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
