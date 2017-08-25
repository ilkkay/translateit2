package translateit2.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import translateit2.exception.TranslateIt2Exception;

@Validated
public interface LoadingContractor {

    Stream <Path> downloadTarget(long workId);
    
    void uploadSource(MultipartFile file, long workId);
    
    void removeUploadedSource(long workId);
    
    void uploadTarget(MultipartFile file, long workId);
}
