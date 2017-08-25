package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class CannotCreateUploadDirectoryException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public CannotCreateUploadDirectoryException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public CannotCreateUploadDirectoryException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
