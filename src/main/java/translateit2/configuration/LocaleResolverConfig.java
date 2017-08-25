package translateit2.configuration;

import java.util.Locale;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@ConfigurationProperties(prefix = "translateit2.localeresolver")
@Configuration
public class LocaleResolverConfig {
    /* Implementation of LocaleResolver that uses a locale attribute in
    * the user’s session in case of a custom setting, with a fallback to the
    * specified default locale or the request’s accept-header locale
    */
    
    private String locale;

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    // should detect language based on browser. TODO: test it
    // Does not support setLocale, since the accept header can
    // only be changed through changing the client's locale settings
    // => AcceptHeaderLocaleResolver will resolve the Locale from
    // the request (using the accept header <= from the client's OS)
    @Bean
    public AcceptHeaderLocaleResolver browserLocaleResolver() {
        AcceptHeaderLocaleResolver bean = new AcceptHeaderLocaleResolver();
        return bean;
    }
    
    @Bean
    public CharSetResolver charSetResolver() {
        CharSetResolver bean = new CharSetResolver();
        return bean;
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale(locale));
        return slr;
    }

}