package translateit2.fileloader;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import translateit2.exception.TranslateIt2Exception;

public interface FileLoader {

    void deleteUploadedFile(final Path filePath);

    void deleteUploadedFiles();

    Path getUploadPath(final String filename);
    
    Path getDownloadPath(final String filename);

    Stream<Path> getPathsOfDownloadableFiles();

    Resource loadAsResource(final String filename);
    
    Path storeToUploadDirectory(final MultipartFile file);

    Stream <Path> storeToDownloadDirectory(final Path temporaryFilePath, 
    		final String downloadFilename);
    
}
