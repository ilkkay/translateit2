package translateit2.languagebeancache;

import java.util.List;
import java.util.Optional;

public interface LanguageFileService <F, T> {
    Optional<T> getService(F format);
    
    List<F> listFormatsSupported();
}
