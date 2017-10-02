package translateit2.formatfactory;

import translateit2.languagefile.LanguageFileFormat;

public interface FormatFactory {
    LanguageFileFormat getFormat();
    ILanguageFileReader getReader();
    ILanguageFileWriter getWriter();
    ILanguageFileValidator getValidator();    
}
