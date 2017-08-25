package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class CannotReadLanguageFromFileNameException extends IOException {
    private static final long serialVersionUID = 1L;
    
    public CannotReadLanguageFromFileNameException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public CannotReadLanguageFromFileNameException(Throwable throwable) {
        super(throwable);
        // TODO Auto-generated constructor stub
    }

}
