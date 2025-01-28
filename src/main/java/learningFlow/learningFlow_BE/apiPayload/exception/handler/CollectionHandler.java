package learningFlow.learningFlow_BE.apiPayload.exception.handler;

import learningFlow.learningFlow_BE.apiPayload.code.BaseErrorCode;
import learningFlow.learningFlow_BE.apiPayload.exception.GeneralException;

public class CollectionHandler extends GeneralException {
    public CollectionHandler(BaseErrorCode code) {
        super(code);
    }
}
