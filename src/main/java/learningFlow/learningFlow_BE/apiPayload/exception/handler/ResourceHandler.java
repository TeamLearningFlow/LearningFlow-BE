package learningFlow.learningFlow_BE.apiPayload.exception.handler;

import learningFlow.learningFlow_BE.apiPayload.code.BaseErrorCode;
import learningFlow.learningFlow_BE.apiPayload.exception.GeneralException;

public class ResourceHandler extends GeneralException {
    public ResourceHandler(BaseErrorCode code) { super(code);}
}
