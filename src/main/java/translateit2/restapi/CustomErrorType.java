package translateit2.restapi;

import java.util.Arrays;
import java.util.List;

import translateit2.exception.TranslateIt2ErrorCode;

public class CustomErrorType {

    final private String localizedErrorMessage;

    final List<String> errorMessages;

    final private TranslateIt2ErrorCode errorCode;

    public CustomErrorType(String localizedErrorMessage, List<String> errorMessages) {
        this.localizedErrorMessage = localizedErrorMessage;
        this.errorMessages = errorMessages;
        this.errorCode = TranslateIt2ErrorCode.UNDEFINED_ERROR;
    }
 
    public CustomErrorType(String errorMessage, TranslateIt2ErrorCode errorCode) {
        this.localizedErrorMessage = errorMessage;
        this.errorMessages = Arrays.asList(errorMessage);
        this.errorCode = errorCode;
    }
    
    public CustomErrorType(String localizedErrorMessage, String errorMessage, TranslateIt2ErrorCode errorCode) {
        this.localizedErrorMessage = localizedErrorMessage;
        this.errorMessages = Arrays.asList(errorMessage);
        this.errorCode = errorCode;
    }

    public String getLocalizedErrorMessage() {
        return localizedErrorMessage;
    }
    
    public List<String> getErrorMessage() {
        return errorMessages;
    }

    public TranslateIt2ErrorCode getErrorCode() {
        return errorCode;
    }
}
