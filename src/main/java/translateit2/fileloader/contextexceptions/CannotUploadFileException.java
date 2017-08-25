package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class CannotUploadFileException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public CannotUploadFileException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public CannotUploadFileException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
