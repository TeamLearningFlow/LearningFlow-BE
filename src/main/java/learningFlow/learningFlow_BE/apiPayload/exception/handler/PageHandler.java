package learningFlow.learningFlow_BE.apiPayload.exception.handler;

import learningFlow.learningFlow_BE.apiPayload.code.BaseErrorCode;
import learningFlow.learningFlow_BE.apiPayload.code.status.ErrorStatus;
import learningFlow.learningFlow_BE.apiPayload.exception.GeneralException;

public class PageHandler extends GeneralException {
    public PageHandler(ErrorStatus code) {
        super(code);
    }
}
