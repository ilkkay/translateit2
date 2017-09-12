package translateit2.languagebeancache;

import translateit2.languagefile.LanguageFile;

// note [MD] let's go over this idea once more

public interface LanguageBeanCache <F, T extends LanguageFile <F>> 
    extends LanguageFileService <F, T> {
    
}
