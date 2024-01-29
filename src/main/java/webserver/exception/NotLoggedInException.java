package webserver.exception;

import webserver.status.ErrorCode;

public class NotLoggedInException extends GeneralException{
    public NotLoggedInException(ErrorCode errorCode) {
        super(errorCode);
    }
}
