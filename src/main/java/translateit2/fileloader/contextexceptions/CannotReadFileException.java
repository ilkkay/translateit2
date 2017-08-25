package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class CannotReadFileException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public CannotReadFileException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public CannotReadFileException(Throwable throwable) {
        super(throwable);
        // TODO Auto-generated constructor stub
    }

}
