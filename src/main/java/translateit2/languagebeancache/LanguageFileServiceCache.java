package translateit2.languagebeancache;

import java.util.List;
import java.util.Optional;

import translateit2.languagefile.LanguageFileFormat;

public interface LanguageFileServiceCache <F, T> {
    Optional<T> getService(F format);

    List<LanguageFileFormat> listFormatsSupported();
}
