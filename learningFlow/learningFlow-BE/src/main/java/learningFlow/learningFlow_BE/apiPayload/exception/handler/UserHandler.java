package learningFlow.learningFlow_BE.apiPayload.exception.handler;

import learningFlow.learningFlow_BE.apiPayload.code.BaseErrorCode;
import learningFlow.learningFlow_BE.apiPayload.exception.GeneralException;

public class UserHandler extends GeneralException {
    public UserHandler(BaseErrorCode code) {
        super(code);
    }
}
