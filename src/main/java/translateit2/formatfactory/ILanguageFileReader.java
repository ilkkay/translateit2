package translateit2.formatfactory;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public interface ILanguageFileReader {   
    
    HashMap<String, String> getSegments(final Path inputPath, final Charset charset);
    
    List<String> getOriginalFileAsList(final Path storedOriginalFile, final Charset charSet);

}
