package translateit2.fileloader;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import translateit2.exception.TranslateIt2Exception;

public interface FileLoader {

    void deleteUploadedFile(Path filePath);

    void deleteUploadedFiles();

    Path getUploadPath(String filename);
    
    Path getDownloadPath(String filename);

    Stream<Path> getPathsOfDownloadableFiles();

    Resource loadAsResource(String filename);
    
    Path storeToUploadDirectory(MultipartFile file);

    Stream <Path> storeToDownloadDirectory(Path temporaryFilePath, String downloadFilename);
    
}
