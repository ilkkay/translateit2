package translateit2.fileloader.contextexceptions;

import java.io.IOException;

public class LoadedFileNotFoundException extends IOException {
    private static final long serialVersionUID = 1L;

    public LoadedFileNotFoundException(String message) {
        super(message);
    }

    public LoadedFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
