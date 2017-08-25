package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class CannotMoveFileException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public CannotMoveFileException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public CannotMoveFileException(Throwable throwable) {
        super(throwable);
        // TODO Auto-generated constructor stub
    }

}
