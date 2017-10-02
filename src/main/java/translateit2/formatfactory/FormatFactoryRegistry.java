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
	
    Map<LanguageFileFormat, FormatFactory> factoryMap;
    
    @Autowired
    List<FormatFactory> factories;
    
    @PostConstruct
    void setFactories() {
        factoryMap = factories.stream().collect(Collectors.toMap(
        		factory -> factory.getFormat(), Function.identity()));
    }
    
    public FormatFactory getFactory(LanguageFileFormat format) {
        return factoryMap.get(format);
    }
    
}
