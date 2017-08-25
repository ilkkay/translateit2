package translateit2.languagebeancache;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

import translateit2.languagebeancache.writer.PropertiesFileWriterImpl;

public class PropertiesFileWriterUnitTest {

    @Test
    public void readCommentLine_EmptyLine_And_ProperSegmentLine_assertLineCount() {
        HashMap <String, String> map = new LinkedHashMap<String, String>();
        List <String> originalFileAsList = new ArrayList<String>();

        // WHEN         
        originalFileAsList.add("## DOTCMS-3022");
        originalFileAsList.add("");
        originalFileAsList.add("alert-file-too-large-takes-lot-of-time=Saving the file may take a longer time because of its size");
        map.put("alert-file-too-large-takes-lot-of-time", "Tiedoston tallennus voi kestää pidempään, jos sen koko on suuri.");

        List <List<String>> stringsList = new ArrayList <List<String>> (); 
        assertThatCode(() ->{ stringsList.add((List<String>) writer().mergeWithOriginalFile(map, originalFileAsList)); })
        .doesNotThrowAnyException();
        
        int returnedSegmentCount = stringsList.get(0).size();
        int expectedSegmentCount = originalFileAsList.size();
        assertThat(expectedSegmentCount,equalTo(returnedSegmentCount));
        
    }
    
    private PropertiesFileWriterImpl writer() {
        return new PropertiesFileWriterImpl();
    }

}
