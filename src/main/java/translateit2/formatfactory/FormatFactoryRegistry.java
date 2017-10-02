package translateit2.formatfactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import translateit2.languagefile.LanguageFileFormat;

@Component
public class FormatFactoryRegistry {
	
    //@Autowired
    //List<FormatFactory> factories;
    
    Map<LanguageFileFormat, FormatFactory> factoryMap;
    
    //@PostConstruct
    @Autowired
    void setFactories(List<FormatFactory> factories  ) {
        factoryMap = factories.stream().collect(Collectors.toMap(
        		factory -> factory.getFormat(), Function.identity()));
    }
    
    public FormatFactory getFactory(LanguageFileFormat format) {
    	FormatFactory fak = factoryMap.get(format);
    	if (fak != null)
    		return fak;
    	else
    		throw new RuntimeException("Not available factory for format: " + format);
    }
    
}
