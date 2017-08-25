package translateit2.languagebeancache.reader;

import org.springframework.stereotype.Component;

import translateit2.languagebeancache.LanguageBeanCacheImpl;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class LanguageReaderBeanCacheImpl extends LanguageBeanCacheImpl<LanguageFileFormat, LanguageFileReader> {

}