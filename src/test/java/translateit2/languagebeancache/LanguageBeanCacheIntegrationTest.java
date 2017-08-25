package translateit2.languagebeancache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import translateit2.TranslateIt2v4Application;
import translateit2.languagebeancache.reader.LanguageFileReader;
import translateit2.languagebeancache.validator.LanguageFileValidator;
import translateit2.languagebeancache.writer.LanguageFileWriter;
import translateit2.languagefile.LanguageFileFormat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
@WebAppConfiguration
public class LanguageBeanCacheIntegrationTest {
    
    @Autowired
    private LanguageBeanCache<LanguageFileFormat, LanguageFileReader> fileReaderCache;

    @Autowired
    private LanguageBeanCache<LanguageFileFormat, LanguageFileWriter> fileWriterCache;
    
    @Autowired
    private LanguageBeanCache<LanguageFileFormat, LanguageFileValidator> fileValidatorCache;

    // https://blog.goyello.com/2015/10/01/different-ways-of-testing-exceptions-in-java-and-junit/
    @Test
    public void failToGetService_ifFormatNotSupported() {

        try {
            fileReaderCache.getService(LanguageFileFormat.PO).get();
            fail("No exception was thrown");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }

    }
    
    @Test
    public void testBeanCache() {
        LanguageFileWriter writerBean = fileWriterCache.getService(LanguageFileFormat.PROPERTIES).get();
        assertThat(writerBean.getFileFormat(), is(equalTo(LanguageFileFormat.PROPERTIES)));

        LanguageFileReader readerBean = fileReaderCache.getService(LanguageFileFormat.PROPERTIES).get();
        assertThat(readerBean.getFileFormat(), is(equalTo(LanguageFileFormat.PROPERTIES)));
        
        LanguageFileValidator validatorBean = fileValidatorCache.getService(LanguageFileFormat.PROPERTIES).get();
        assertThat(validatorBean.getFileFormat(), is(equalTo(LanguageFileFormat.PROPERTIES)));
    }

}
