package webserver.exception;

import webserver.status.ErrorCode;

public class GeneralException extends RuntimeException {
    private final ErrorCode errorCode;

    public GeneralException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}