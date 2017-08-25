package translateit2.exception;

import java.io.IOException;

public class TranslateIt2ServiceException extends IOException  /* RuntimeException */  {
    private static final long serialVersionUID = 1L;

    private TranslateIt2ErrorCode errorCode;

    public TranslateIt2ErrorCode getErrorCode(){
        return this.errorCode;
    }

    public TranslateIt2ServiceException(TranslateIt2ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    
    public TranslateIt2ServiceException(TranslateIt2ErrorCode errorCode, String message){
        super(message);
        this.errorCode=errorCode;
    }

    public TranslateIt2ServiceException(TranslateIt2ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

}
