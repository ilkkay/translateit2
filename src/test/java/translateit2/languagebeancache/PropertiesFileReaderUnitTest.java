package translateit2.languagebeancache;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import translateit2.exception.TranslateIt2Exception;
import translateit2.languagebeancache.reader.PropertiesFileReaderImpl;

public class PropertiesFileReaderUnitTest {

    @Test
    public void readImproperFile_assertFileLoaderException() {
        // WHEN permanent properties is not a proper properties file
        Path permanentFilePath = Paths.get("d:\\kirje2fi.txt");

        // THEN
        assertThatCode(() -> reader().getSegments(permanentFilePath, StandardCharsets.ISO_8859_1))
        .isExactlyInstanceOf(TranslateIt2Exception.class); 

    }

    @Test
    public void readPropertiesFile_assertSegmentCount() {
        // WHEN file is UTF8-properties file
        Path permanentFilePath = Paths.get("d:\\dotcms_fi_FI-UTF8.properties");

        // THEN
        List <LinkedHashMap<String, String>> segmentsList = new ArrayList <LinkedHashMap <String, String>> ();        
        assertThatCode(() ->{ segmentsList.add((LinkedHashMap<String, String>) reader().getSegments(permanentFilePath, 
                StandardCharsets.UTF_8)); })
        .doesNotThrowAnyException();

        int expectedSegmentCount = 4140;
        int returnedSegmentCount = segmentsList.get(0).size();
        assertThat(expectedSegmentCount,equalTo(returnedSegmentCount));

    }

    @Test
    public void getOriginalFileAsList_assertStringCount() {
        // WHEN backup file is UTF8-properties file
        String backupFile = "d:\\dotcms_fi_FI-UTF8.properties";

        // THEN
        List <List<String>> stringsList = new ArrayList <List<String>> (); 
        assertThatCode(() ->{ stringsList.add((List<String>) reader().getOriginalFileAsList(Paths.get(backupFile),
                StandardCharsets.UTF_8)); })
        .doesNotThrowAnyException();

        int expectedStringCount = 4561;
        int returnedStringCount = stringsList.get(0).size();
        assertThat(expectedStringCount,equalTo(returnedStringCount));
    }

    
    private PropertiesFileReaderImpl reader() {
        return new PropertiesFileReaderImpl();
    }

}
