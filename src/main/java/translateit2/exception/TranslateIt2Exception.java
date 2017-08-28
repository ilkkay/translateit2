package translateit2.exception;

public class TranslateIt2Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private  final  TranslateIt2ErrorCode errorCode;

    public TranslateIt2ErrorCode getErrorCode(){
        return this.errorCode;
    }

    public TranslateIt2Exception(TranslateIt2ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    
    public TranslateIt2Exception(TranslateIt2ErrorCode errorCode, String message){
        super(message);
        this.errorCode=errorCode;
    }

    public TranslateIt2Exception(TranslateIt2ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

}
