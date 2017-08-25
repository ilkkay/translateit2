package translateit2.filelocator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.fileloader.FileLoaderProperties;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class FileLocatorImpl implements FileLocator {
    
    private final Path rootPermanentDirectory; 
    
    private final Path permanentLocation;

    @Autowired
    public FileLocatorImpl(FileLoaderProperties properties) {
        this.permanentLocation = Paths.get(properties.getPermanentLocation());
        this.rootPermanentDirectory = Paths.get(properties.getRootPermanentDirectory());
    }
    
    @Override
    public Path moveUploadedFileIntoPermanentFileSystem(Path uploadedFile, 
            LanguageFileFormat format) {
        
        // create new path for permanent file storage
        Path outFilePath = getUniquePath(format, getFullPath(rootPermanentDirectory));
        Path dir = outFilePath.getParent();
        if (Files.notExists(dir))
            try {
                Files.createDirectory(dir);
            } catch (IOException e1) {
                throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_CREATE_PERMANENT_DIRECTORY);
            }
        
        // and move the file from upload directory   
        try {
            Files.move(uploadedFile, outFilePath);
        } catch (IOException e) {
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_MOVE_FILE);
        }       

        return outFilePath;
    }

    private Path getUniquePath(LanguageFileFormat format, Path rootPath) {
        //Path test = fileLoaderService
                
        Path fnamePath = Paths.get(java.util.UUID.randomUUID().toString());
        Path dirPath = Paths.get(LocalDate.now().toString());
        Path path = dirPath.resolve(fnamePath);
        path = path.resolveSibling(path.getFileName() + "." + format.toString());
        
        Path uniquePath = rootPath.resolve(path);
        
        return uniquePath;
    }

    @Override
    public void deleteFileFromPermanentFileSystem(Path fileToDeletePath) {
        try {
            Files.deleteIfExists(fileToDeletePath);
        } catch (IOException e) {
            //logger.warn("Could not remove file: {}", fileToDeletePath.toAbsolutePath().toString());
        }
    }
    
    @Override
    public Path createFileIntoPermanentFileSystem(List<String> downloadFileAsList, 
            LanguageFileFormat format, Charset charset) {
        
        // create new path for temporary file in permanent storage
        Path outFilePath = getUniquePath(format, getFullPath(rootPermanentDirectory));
        Path dir = outFilePath.getParent();
        if (Files.notExists(dir))
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_CREATE_PERMANENT_DIRECTORY);
            }
        
        // and write contents to file   
        try {
            return Files.write(outFilePath,downloadFileAsList, charset);
        } catch (IOException e) {
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_CREATE_FILE);
        }       

    }
    
    @PostConstruct
    private void init() {        
        try {
            if (Files.notExists(permanentLocation)) Files.createDirectory(permanentLocation);
        } catch (IOException e) {
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_CREATE_ROOT_DIRECTORY, e.getCause());
        }
    }
    
    private Path getFullPath(Path p) {
        return permanentLocation.resolve(p);
    }
}
