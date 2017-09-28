package translateit2.languagebeancache;

import org.springframework.beans.factory.annotation.Autowired;
import translateit2.languagefile.LanguageFile;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageBeanCacheImpl <F, T extends LanguageFile <F>>
    implements LanguageBeanCache<F, T>{

    private final Map<F, T> serviceCache = new HashMap<>();
    
    @Autowired
    private List<T> services;

    @Override
    public Optional<T> getService(F type) {
        // return Optional.ofNullable(serviceCache.get(type));

        // note [MD] (2) odd, ask for an explanation
        Map<F, T> services = serviceCache.entrySet().stream()
                .filter(p -> p.getValue().getFileFormat().equals(type))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        if (services.size() == 1)
            return Optional.of(services.get(type));
        else
            return Optional.<T>empty();
    }
    
    @PostConstruct
    public void initLngServiceCache() {
        services.forEach(s -> serviceCache.put(s.getFileFormat(), s));
    }
    
    @Override
    public List<F> listFormatsSupported() {
        // note [MD] (2) .keySet() ? new ArrayList(...) ? random order?
        List<F> formats = serviceCache.entrySet().stream().map(x -> x.getKey())
                .collect(Collectors.toList());
        return formats;
    }

}
