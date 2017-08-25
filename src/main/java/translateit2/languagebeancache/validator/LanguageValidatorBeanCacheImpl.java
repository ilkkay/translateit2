package translateit2.languagebeancache.validator;

import org.springframework.stereotype.Component;

import translateit2.languagebeancache.LanguageBeanCacheImpl;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class LanguageValidatorBeanCacheImpl extends LanguageBeanCacheImpl<LanguageFileFormat, LanguageFileValidator> {

}