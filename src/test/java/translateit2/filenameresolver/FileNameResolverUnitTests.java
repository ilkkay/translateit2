package translateit2.filenameresolver;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import translateit2.exception.TranslateIt2Exception;

@RunWith(MockitoJUnitRunner.class)
public class FileNameResolverUnitTests {

    @Test
    public void resolveFileName_assertAppName() {
        // when
        String returnAppName = fileNameResolver().getApplicationName("dotCMS_fi.properties");      
        // then
        assertThat(returnAppName, is(equalTo("dotCMS")));        

        // when
        returnAppName = fileNameResolver().getApplicationName("dotCMS-fi_FI_var.po");      
        // then
        assertThat(returnAppName, is(equalTo("dotCMS-fi")));
    }

    @Test
    public void resolveFileName_assertLocale() {
        
        // initialize
        String expected = "fi_FI";

        // when 
        Locale returnLocale = fileNameResolver().getLocaleFromFilename("dotCMS_FI.properties",
                ext -> ext.equals("properties"));      
        // then
        String returned = returnLocale.toString(); 
        assertThat(expected, is(equalTo(returned)));

        returnLocale = fileNameResolver().getLocaleFromFilename("dotcms_fi-utf8.properties",
                ext -> ext.equals("properties"));      
        // then
        returned = returnLocale.toString(); 
        assertThat(expected, is(equalTo(returned)));
        
        // when
        returnLocale = fileNameResolver().getLocaleFromFilename("dotCMS_fi_fi.properties",
                ext -> ext.equals("properties"));      
        // then
        returned = returnLocale.toString(); 
        assertThat(expected, is(equalTo(returned)));

        // when
        returnLocale = fileNameResolver().getLocaleFromFilename("dotCMS_FI_FI_var.properties",
                ext -> ext.equals("properties"));      
        // then
        returned = returnLocale.toString(); 
        assertThat(expected, is(equalTo(returned)));

    }

    // https://www.petrikainulainen.net/programming/testing/writing-clean-tests-java-8-to-the-rescue/
    @Test
    public void resolveFileName_failIfAppNameOnlyFile() {

        assertThatThrownBy(() -> fileNameResolver().getApplicationName("dotCMS.properties"))
        .isExactlyInstanceOf(TranslateIt2Exception.class);

        assertThatThrownBy(() -> fileNameResolver().getLocaleFromFilename("XXX.properties",
                ext -> ext.equals("properties")))
        .isExactlyInstanceOf(TranslateIt2Exception.class);
    }

    @Test
    public void resolveFileName_failIfExtensionIncorrect() {

        assertThatThrownBy(() -> fileNameResolver().getLocaleFromFilename("XXX.xxx",
                ext -> ext.equals("properties")))
        .isExactlyInstanceOf(TranslateIt2Exception.class);
    }

    @Test
    public void resolveFileName_failIfLocaleMissingOrIncorrect() {


        assertThatThrownBy(() -> fileNameResolver().getLocaleFromFilename("dotCMS_XXX.properties",
                ext -> ext.equals("properties")))
        .isExactlyInstanceOf(TranslateIt2Exception.class);

        assertThatThrownBy(() -> fileNameResolver().getLocaleFromFilename("dotCMS-xx-XX.properties",
                ext -> ext.equals("properties")))
        .isExactlyInstanceOf(TranslateIt2Exception.class);
    }

    private FileNameResolver fileNameResolver() {
        return new FileNameResolverImpl();
    }

}
