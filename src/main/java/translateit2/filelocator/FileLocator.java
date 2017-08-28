package translateit2.filelocator;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import translateit2.languagefile.LanguageFileFormat;

public interface FileLocator {
    
    Path createFileIntoPermanentFileSystem(final List<String> downloadFileAsList, 
    		final LanguageFileFormat format,  final Charset charset);
    
    Path moveUploadedFileIntoPermanentFileSystem(final Path uploadedFile,
            final LanguageFileFormat format);
    
    void deleteFileFromPermanentFileSystem(final Path fileToDeletePath);
    
}
