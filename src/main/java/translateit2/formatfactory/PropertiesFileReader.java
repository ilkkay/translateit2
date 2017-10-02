package translateit2.formatfactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.languagebeancache.reader.PropertiesFileReaderImpl;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.util.OrderedProperties;

@Component
public class PropertiesFileReader implements ILanguageFileReader {
    static final Logger logger = LogManager.getLogger(PropertiesFileReaderImpl.class.getName());

    @Override
    public HashMap<String, String> getSegments(final Path inputPath, final Charset charset) {
    	logger.debug("Entering getSegments() with file {} and charset {}", 
    			inputPath.toAbsolutePath().toString(), charset.toString());
    	
        HashMap<String, String> map = new LinkedHashMap<String, String>();
        OrderedProperties srcProp = new OrderedProperties();

        try (InputStream stream = new FileInputStream(inputPath.toString());
                InputStreamReader isr = new InputStreamReader(stream, charset)) {
            
        	srcProp.load(isr);

            Set<String> keys = srcProp.stringPropertyNames();
            // checks for at least one (ASCII) alphanumeric character.
            map = keys.stream().filter(k -> k.toString().matches(".*\\w.*")).collect(Collectors.toMap(k -> k.toString(),
                    k -> srcProp.getProperty(k), (v1, v2) -> v1, LinkedHashMap::new));

        } catch (IOException e) {
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_READ_FILE,e.getCause());
        }

    	logger.debug("Leaving getSegments() with map size:{} ", map.size());
    	
        return map;
    }

    @Override
    public List<String> getOriginalFileAsList(final Path storedOriginalFile, final Charset charSet) {

        try {
            return Files.readAllLines(storedOriginalFile, charSet);
        } catch (IOException e) {
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_READ_FILE,e.getCause());
        }
    }
}
