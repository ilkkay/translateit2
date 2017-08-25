package translateit2.validator;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import translateit2.exception.TranslateIt2Exception;
import translateit2.languagebeancache.validator.LanguageFileValidator;
import translateit2.languagebeancache.validator.PropertiesFileValidatorImpl;
import translateit2.languagefile.LanguageFileType;

@RunWith(MockitoJUnitRunner.class)
public class LanguageFileValidatorUnitTest {

    @Test
    public void readIso8859File_notFailIfExpectIso8859() {

        // WHEN expect ISO8859-1 and uploaded is ISO8859-1
        LanguageFileType expectedType = LanguageFileType.ISO8859_1;

        // THEN no exceptions if the upload file is ISO8859-1
        Path uploadedLngFile = Paths.get("d:\\messages_fi.properties");

        assertThatCode(() -> validator().validateCharacterSet(uploadedLngFile,  
                expectedType)).doesNotThrowAnyException();
     
    }
    
    @Test
    public void readUTF8File_notFailIfExpectUTF8() {

        // WHEN expect UTF-8 and uploaded is UTF-8
        LanguageFileType expectedType = LanguageFileType.UTF_8;

        // THEN no exceptions if the upload file is UTF-8
        Path uploadedLngFile = Paths.get("d:\\dotcms_fi-UTF8.properties");

        assertThatCode(() -> validator().validateCharacterSet(uploadedLngFile,  
                expectedType)).doesNotThrowAnyException();
     
    }
    
    @Test
    public void readUTF8File_failIfExpectIso8859() {

        // WHEN expect ISO8859 and uploaded is UTF-8
        LanguageFileType expectedType = LanguageFileType.ISO8859_1;

        // THEN throw exception if the upload file is UTF-8
        Path uploadedLngFile = Paths.get("d:\\messages_fi-UTF8.properties");

        assertThatCode(() -> validator().validateCharacterSet(uploadedLngFile,  
                expectedType))
        .isExactlyInstanceOf(TranslateIt2Exception.class);        

    }


    @Test
    public void readIso8859File_failIfExpectIso8859() {

        // WHEN expect UTF-8 and uploaded is ISO8859
        LanguageFileType expectedType = LanguageFileType.UTF_8;
        Path uploadedLngFile = Paths.get("D:\\dotcms_fi.properties");

        // THEN throw exception if the upload file is ISO8859
        assertThatCode(() -> validator().validateCharacterSet(uploadedLngFile,  
                expectedType))
        .isExactlyInstanceOf(TranslateIt2Exception.class);         

    }

    private LanguageFileValidator validator() {
        return new PropertiesFileValidatorImpl();
    }

}
