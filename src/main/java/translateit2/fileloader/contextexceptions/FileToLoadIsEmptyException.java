package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class FileToLoadIsEmptyException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public FileToLoadIsEmptyException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public FileToLoadIsEmptyException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
