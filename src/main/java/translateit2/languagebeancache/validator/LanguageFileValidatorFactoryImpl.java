package translateit2.languagebeancache.validator;

import org.springframework.stereotype.Component;

import translateit2.languagebeancache.LanguageFileImpl;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class LanguageFileValidatorFactoryImpl extends LanguageFileImpl<LanguageFileFormat, LanguageFileValidator > {

}
