package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class CannotReadApplicationNameFromFileNameException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public CannotReadApplicationNameFromFileNameException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public CannotReadApplicationNameFromFileNameException(Throwable throwable) {
        super(throwable);
        // TODO Auto-generated constructor stub
    }

}
