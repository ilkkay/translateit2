package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class CannotCreatePermanentDirectoryException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public CannotCreatePermanentDirectoryException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public CannotCreatePermanentDirectoryException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
