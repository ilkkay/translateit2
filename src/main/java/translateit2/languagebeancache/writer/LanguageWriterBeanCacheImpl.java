package translateit2.languagebeancache.writer;

import org.springframework.stereotype.Component;

import translateit2.languagebeancache.LanguageBeanCacheImpl;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class LanguageWriterBeanCacheImpl extends LanguageBeanCacheImpl<LanguageFileFormat, LanguageFileWriter> {

}