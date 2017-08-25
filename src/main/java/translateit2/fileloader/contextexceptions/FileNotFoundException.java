package translateit2.fileloader.contextexceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="File Not Found")
public class FileNotFoundException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public FileNotFoundException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
