package translateit2.languagebeancache.validator;

import java.nio.file.Path;
import java.util.Locale;

import translateit2.exception.TranslateIt2Exception;
import translateit2.languagefile.LanguageFile;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;

public interface LanguageFileValidator extends LanguageFile <LanguageFileFormat> {   
    void validateCharacterSet(Path uploadedFile, LanguageFileType expectedType);

    void validateApplicationName(String appName, String expectedApplicationName);

    void validateLocale(Locale appLocale, Locale expectedLocale);

}
