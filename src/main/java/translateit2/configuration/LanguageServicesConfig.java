package translateit2.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.languagebeancache.LanguageBeanCacheImpl;
import translateit2.languagebeancache.reader.LanguageFileReader;
import translateit2.languagebeancache.validator.LanguageFileValidator;
import translateit2.languagebeancache.writer.LanguageFileWriter;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.model.Priority;

@Component
public class LanguageServicesConfig {
    static final Logger logger = LogManager.getLogger(LanguageServicesConfig.class);

    @Autowired
    private LanguageBeanCacheImpl<LanguageFileFormat, LanguageFileReader> fileReaderCache;

    @Autowired
    private LanguageBeanCacheImpl<LanguageFileFormat, LanguageFileWriter> fileWriterCache;

    @Autowired
    private LanguageBeanCacheImpl<LanguageFileFormat, LanguageFileValidator> fileValidatorCache;

    public List<LanguageFileFormat> getSupportedFormats() {
        List<LanguageFileFormat> readerFormats = fileReaderCache.listFormatsSupported();
        List<LanguageFileFormat> writerFormats = fileWriterCache.listFormatsSupported();
        List<LanguageFileFormat> validatorFormats = fileValidatorCache.listFormatsSupported();

        if (readerFormats.size() == writerFormats.size() && 
                readerFormats.size() == validatorFormats.size() &&
                readerFormats.size() > 0)
            return readerFormats;
        else
            return Collections.emptyList();
    }

    public List<LanguageFileType> getSupportedCharacterSets() {
        List<LanguageFileType> csets = new ArrayList<LanguageFileType>();
        csets.add(LanguageFileType.ISO8859_1);
        csets.add(LanguageFileType.UTF_8);
        return csets;
    }

    
    // list => get...
    public List<Priority> getSupportedPriorities() {
        List<Priority> priorities = new ArrayList<Priority>();

        priorities.add(Priority.LOW);
        priorities.add(Priority.MEDIUM);
        priorities.add(Priority.HIGH);
        
        return priorities;
    }
    
    private void checkServiceAvailability(LanguageFileFormat format) {
        if (!(fileValidatorCache.getService(format).isPresent())) {
            logger.error("Language file validator for format {} was missing", format);
        }      

        if (!(fileReaderCache.getService(format).isPresent())) {
            logger.error("Language file reader for format {} was missing", format);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }

        if (!(fileWriterCache.getService(format).isPresent())) {
            logger.error("Language file writer for format {} was missing", format);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }
    }
}
