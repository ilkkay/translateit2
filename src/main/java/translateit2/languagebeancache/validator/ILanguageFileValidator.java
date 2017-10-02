package translateit2.languagebeancache.validator;

import java.nio.file.Path;
import java.util.Locale;

import translateit2.languagefile.LanguageFile;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;

public interface ILanguageFileValidator {   
    void validateCharacterSet(final Path uploadedFile, final LanguageFileType expectedType);

    void validateApplicationName(final String appName, final String expectedApplicationName);

    void validateLocale(final Locale appLocale, final Locale expectedLocale);

}
