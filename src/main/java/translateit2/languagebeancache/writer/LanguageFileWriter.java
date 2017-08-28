package translateit2.languagebeancache.writer;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import translateit2.languagefile.LanguageFile;
import translateit2.languagefile.LanguageFileFormat;

public interface LanguageFileWriter extends LanguageFile <LanguageFileFormat> {
    
    List<String> mergeWithOriginalFile(final Map<String, String> map, final List<String> inLines);
    
    void createDownloadFile(final Path tmpFilePath,List<String> downloadFileAsList);
    
    void write();
}
