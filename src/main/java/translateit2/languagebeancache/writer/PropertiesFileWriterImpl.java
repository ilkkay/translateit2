package translateit2.languagebeancache.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2Exception;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class PropertiesFileWriterImpl implements LanguageFileWriter {


    @Override
    public LanguageFileFormat getFileFormat() {
        return LanguageFileFormat.PROPERTIES;
    }

    @Override
    public void write() { }
    
    @Override
    public void createDownloadFile(Path tmpFilePath, List<String> downloadFileAsList) {

            try {
                Files.write(tmpFilePath, downloadFileAsList);
            } catch (IOException e) {
                throw new TranslateIt2Exception("Could not create file for download");
            }        
    }
    

    @Override
    public  List<String> mergeWithOriginalFile(Map<String, String> map, List<String> inLines) {
        
        List<String> outLines = new ArrayList<String>();
        boolean isFirstLine = true; // <= optional byte order mark (BOM)
        for (String line : inLines) {
            if ((isEmptyLine(line)) || (isCommentLine(line)) || (isFirstLine)) {
                outLines.add(line);
                isFirstLine = false;
            } else if (isKeyValuePair(line)) {
                String key = getKey(line);
                System.out.println(getKey(line) + "=" + map.get(key));
                outLines.add(getKey(line) + "=" + map.get(key));
            } else
                throw new TranslateIt2Exception("Could not create file for download");
        }
        
        return outLines;
    }

    private String getKey(String line) {
        String parts[] = line.split("=");
        if (parts.length < 2)
            return null;
        else
            return parts[0].trim();
    }
    
    private boolean isKeyValuePair(String line) {
        if (getKey(line) != null)
            return true;
        else
            return false;
    }
    
    private boolean isCommentLine(String line) {
        return (line.trim().startsWith("#") || line.trim().startsWith("<"));
    }

    private boolean isEmptyLine(String line) {
        return line.isEmpty();
    }


    

}
