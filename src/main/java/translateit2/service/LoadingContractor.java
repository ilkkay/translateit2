package translateit2.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Validated
public interface LoadingContractor {

    Stream <Path> downloadTarget(final long workId);
    
    void uploadSource(final MultipartFile file, final long workId);
    
    void removeUploadedSource(final long workId);
    
    void uploadTarget(final MultipartFile file, final long workId);
}
