package learningFlow.learningFlow_BE.apiPayload.exception.handler;

import learningFlow.learningFlow_BE.apiPayload.code.BaseErrorCode;
import learningFlow.learningFlow_BE.apiPayload.exception.GeneralException;

public class LoginHandler extends GeneralException {
    public LoginHandler(BaseErrorCode code) {
        super(code);
    }
}
