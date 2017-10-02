package translateit2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import translateit2.formatfactory.FormatFactory;
import translateit2.formatfactory.FormatFactoryRegistry;
import translateit2.languagebeancache.LanguageBeanCache;
import translateit2.languagebeancache.reader.ILanguageFileReader;
import translateit2.languagebeancache.reader.LanguageFileReader;
import translateit2.languagebeancache.validator.ILanguageFileValidator;
import translateit2.languagebeancache.validator.LanguageFileValidator;
import translateit2.languagebeancache.writer.ILanguageFileWriter;
import translateit2.languagebeancache.writer.LanguageFileWriter;
import translateit2.languagefile.LanguageFileFormat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
@WebAppConfiguration
public class FormatFactoryIT {
	@Autowired 
	FormatFactoryRegistry formatFactoryRegistry;	
	
    @Test
    public void getPropertiesReader() {

    	FormatFactory propFormatFactory = 
    			formatFactoryRegistry.getFactory(LanguageFileFormat.PROPERTIES);

    	ILanguageFileWriter writerBean = propFormatFactory.getWriter();
        assertThat(writerBean, notNullValue());
        
        ILanguageFileReader readerBean = propFormatFactory.getReader();
        assertThat(readerBean, notNullValue());

        ILanguageFileValidator validatorBean = propFormatFactory.getValidator();
        assertThat(validatorBean, notNullValue());

        assertThat(propFormatFactory.getFormat(),equalTo(LanguageFileFormat.PROPERTIES));	
    	
    }
   
    @Test
    public void failIf_notFoundFactory() {
        try {
        	formatFactoryRegistry.getFactory(LanguageFileFormat.PO);            
        	fail("No exception was thrown");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }

    }


}
