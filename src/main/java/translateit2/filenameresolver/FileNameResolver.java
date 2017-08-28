package translateit2.filenameresolver;

import java.util.Locale;
import java.util.function.Predicate;

import translateit2.languagefile.LanguageFileFormat;

public interface FileNameResolver {
    
    public String getApplicationName(final String filename);
        
    Locale getLocaleFromFilename(final String fileName, Predicate<String> p);
    
    String getDownloadFilename(final String originalFileName,
    		final Locale locale, final LanguageFileFormat format);

}
