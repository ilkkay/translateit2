package translateit2.languagebeancache.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class PropertiesFileWriterImpl2 implements ILanguageFileWriter {
    static final Logger logger = LogManager.getLogger(PropertiesFileWriterImpl.class.getName());

    @Override
    public void write() { }
    
    @Override
    public void createDownloadFile(final Path tmpFilePath, final List<String> downloadFileAsList) {

            try {
                Files.write(tmpFilePath, downloadFileAsList);
            } catch (IOException e) {
                throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_CREATE_FILE,
                		"Could not create file for download");
            }        
    }
    

    @Override
    public  List<String> mergeWithOriginalFile(final Map<String, String> map, final List<String> inLines) {
    	logger.debug("Entering mergeWithOriginalFile() with map size {} and list size {}", 
    			map.size(), inLines.size());

        List<String> outLines = new ArrayList<String>();
        boolean isFirstLine = true; // <= optional byte order mark (BOM)
        for (String line : inLines) {
            if ((isEmptyLine(line)) || (isCommentLine(line)) || (isFirstLine)) {
                outLines.add(line);
                isFirstLine = false;
            } 
            
            else if (isKeyValuePair(line)) {
                String key = getKey(line);
                logger.debug("Key: {} => value: {} in line: {}",key, map.get(key), line);
                outLines.add(getKey(line) + "=" + map.get(key));
            } 
            
            else
                throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_CREATE_FILE,
                		"Could not create file for download");
        }
        
    	logger.debug("Leaving mergeWithOriginalFile() with list size {}", outLines.size());
        return outLines;
    }

    private String getKey(final String line) {
        String parts[] = line.split("=");
        if (parts.length < 2)
            return null;
        else
            return parts[0].trim();
    }
    
    private boolean isKeyValuePair(final String line) {
        if (getKey(line) != null)
            return true;
        else
            return false;
    }
    
    private boolean isCommentLine(final String line) {
        return (line.trim().startsWith("#") || line.trim().startsWith("<"));
    }

    private boolean isEmptyLine(final String line) {
        return line.isEmpty();
    }
}
