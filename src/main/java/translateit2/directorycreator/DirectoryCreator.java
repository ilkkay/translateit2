package translateit2.directorycreator;

import java.nio.file.Path;

public interface DirectoryCreator {
	Path getPermanentDirectory();
	Path getDownloadDirectory();
	Path getUploadDirectory();
}
