package translateit2.directorycreator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.fileloader.FileLoaderProperties;

@Component
public class DirectoryCreatorImpl implements DirectoryCreator{
    private final Path rootPermanentDirectory;     
    private final Path rootTemporaryDirectory;
    private final Path permanentLocation;    
    private final Path uploadLocation;
    private final Path downloadLocation;    
    
    public DirectoryCreatorImpl (FileLoaderProperties properties) {
        this.rootTemporaryDirectory = Paths.get(properties.getRootTemporaryDirectory());
        this.uploadLocation = Paths.get(properties.getUploadLocation());
        this.downloadLocation = Paths.get(properties.getDownloadLocation());
        this.permanentLocation = Paths.get(properties.getPermanentLocation());
        this.rootPermanentDirectory = Paths.get(properties.getRootPermanentDirectory());
    }

    public Path getPermanentDirectory() {
    	Path permanentDirectoryPath = rootPermanentDirectory.resolve(permanentLocation);
    	
        try {
            if (Files.notExists(permanentDirectoryPath)) 
            	Files.createDirectories(permanentDirectoryPath);
        } catch (IOException e) {
            throw new TranslateIt2Exception(
            		TranslateIt2ErrorCode.CANNOT_CREATE_PERMANENT_DIRECTORY, e.getCause());
        }
        return permanentDirectoryPath;
    }

    public Path getDownloadDirectory() {
    	Path downloadDirectoryPath = rootTemporaryDirectory.resolve(downloadLocation);
    	
        try {
            if (Files.notExists(downloadDirectoryPath)) 
            	Files.createDirectories(downloadDirectoryPath);
        } catch (IOException e) {
            throw new TranslateIt2Exception(
            		TranslateIt2ErrorCode.CANNOT_CREATE_DOWNLOAD_DIRECTORY, e.getCause());
        }
        return downloadDirectoryPath;
    }

    public Path getUploadDirectory() {
    	Path uploadDirectoryPath = rootTemporaryDirectory.resolve(uploadLocation);
    	
        try {
            if (Files.notExists(uploadDirectoryPath)) 
            	Files.createDirectories(uploadDirectoryPath);
        } catch (IOException e) {
            throw new TranslateIt2Exception(
            		TranslateIt2ErrorCode.CANNOT_CREATE_UPLOAD_DIRECTORY, e.getCause());
        }
        return uploadDirectoryPath;
    }
    
}
