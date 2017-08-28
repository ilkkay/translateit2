package translateit2.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.exception.TranslateIt2ServiceException;
import translateit2.restapi.CustomErrorType;
import translateit2.util.MessageLogic;

@RestControllerAdvice
@ConfigurationProperties(prefix = "translateit2.validator")
public class RestErrorHandler {

    @Autowired
    MessageLogic messages;

    // Constraint violation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType processValidationError(MethodArgumentNotValidException ex) {

        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        return new CustomErrorType(ex.getLocalizedMessage(), errors);
    }

    // Constraint violation exceptions
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType handleOtherViolationExceptions(HttpServletRequest request,ConstraintViolationException ex){

        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + 
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }
        
        return new CustomErrorType(ex.getLocalizedMessage(), errors);
    }

    // custom error messages Client may have done somtinh wrong
    @ExceptionHandler(TranslateIt2Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType handleLoadingClientExceptions(TranslateIt2Exception ex) {

        CustomErrorType customError = new CustomErrorType(
                messages.get(ex.getErrorCode().getDescription()),
                messages.get(ex.getErrorCode().getDescription(),Locale.ENGLISH),
                ex.getErrorCode());

        return customError;
    }

    // custom error messages. Something went wrong on the server. Client should informa adminstrator
    @ExceptionHandler(TranslateIt2ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CustomErrorType handleLoadingServerExceptions(TranslateIt2Exception ex) {

        CustomErrorType customError = new CustomErrorType(
                messages.get(ex.getErrorCode().getDescription()),
                messages.get(ex.getErrorCode().getDescription(),Locale.ENGLISH),
                ex.getErrorCode());

        return customError;
    }

    // all the other exceptions
    @ExceptionHandler(value = { RuntimeException.class, IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CustomErrorType handleOtherExceptions(HttpServletRequest request, Exception ex){
        return new CustomErrorType(ex.getLocalizedMessage(),TranslateIt2ErrorCode.UNDEFINED_ERROR);        
    }
}
