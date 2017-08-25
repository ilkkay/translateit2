package translateit2.languagebeancache.writer;

import org.springframework.stereotype.Component;

import translateit2.languagebeancache.LanguageFileImpl;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class LanguageFileWriterFactoryImpl extends LanguageFileImpl<LanguageFileFormat, LanguageFileWriter> {

}
