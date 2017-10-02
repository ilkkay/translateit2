package translateit2.formatfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import translateit2.languagebeancache.reader.ILanguageFileReader;
import translateit2.languagebeancache.reader.LanguageFileReader;
import translateit2.languagebeancache.reader.PropertiesFileReaderImpl;
import translateit2.languagebeancache.reader.PropertiesFileReaderImpl2;
import translateit2.languagebeancache.validator.ILanguageFileValidator;
import translateit2.languagebeancache.validator.LanguageFileValidator;
import translateit2.languagebeancache.validator.PropertiesFileValidatorImpl;
import translateit2.languagebeancache.validator.PropertiesFileValidatorImpl2;
import translateit2.languagebeancache.writer.ILanguageFileWriter;
import translateit2.languagebeancache.writer.LanguageFileWriter;
import translateit2.languagebeancache.writer.PropertiesFileWriterImpl;
import translateit2.languagebeancache.writer.PropertiesFileWriterImpl2;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class PropertiesFormatFactory implements FormatFactory {
    @Autowired
    private PropertiesFileReaderImpl2 proLFR;
    
    @Autowired
    private PropertiesFileWriterImpl2 proLFW;
    
    @Autowired
    private PropertiesFileValidatorImpl2 proLFV;
    
    @Override
    public ILanguageFileReader getReader() {
        return proLFR;
    }
    
    @Override
    public ILanguageFileWriter getWriter() {
        return proLFW;
    }
    
    @Override
    public ILanguageFileValidator getValidator() {
        return proLFV;
    }

	@Override
	public LanguageFileFormat getFormat() {
		return LanguageFileFormat.PROPERTIES;
	}
     
}
