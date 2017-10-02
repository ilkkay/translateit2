package translateit2.formatfactory;

import translateit2.languagebeancache.reader.ILanguageFileReader;
import translateit2.languagebeancache.reader.LanguageFileReader;
import translateit2.languagebeancache.validator.ILanguageFileValidator;
import translateit2.languagebeancache.validator.LanguageFileValidator;
import translateit2.languagebeancache.writer.ILanguageFileWriter;
import translateit2.languagebeancache.writer.LanguageFileWriter;
import translateit2.languagefile.LanguageFileFormat;

public interface FormatFactory {
    LanguageFileFormat getFormat();
    ILanguageFileReader getReader();
    ILanguageFileWriter getWriter();
    ILanguageFileValidator getValidator();    
}
