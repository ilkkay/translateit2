package translateit2.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import translateit2.exception.TranslateIt2Exception;

@Validated
public interface LoadingContractor {

    Stream <Path> downloadTarget(final long workId);
    
    void uploadSource(final MultipartFile file, final long workId);
    
    void removeUploadedSource(final long workId);
    
    void uploadTarget(final MultipartFile file, final long workId);
}
