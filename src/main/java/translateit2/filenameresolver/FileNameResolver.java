package translateit2.filenameresolver;

import java.util.Locale;
import java.util.function.Predicate;

import translateit2.exception.TranslateIt2Exception;
import translateit2.languagefile.LanguageFileFormat;

public interface FileNameResolver {
    
    public String getApplicationName(String filename);
        
    Locale getLocaleFromFilename(String fileName, Predicate<String> p);
    
    String getDownloadFilename(String originalFileName,Locale locale,LanguageFileFormat format);

}
