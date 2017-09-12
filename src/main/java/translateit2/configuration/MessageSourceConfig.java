package translateit2.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

// note [MD] (1) mixing on @Configuration and @ConfigurationProperties
@ConfigurationProperties(prefix = "translateit2.messages")
@Configuration
public class MessageSourceConfig {
    private String encoding;
    private String propertiesFile;

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
        bean.setBasename(propertiesFile);
        bean.setDefaultEncoding(encoding);

        // if this is turned false, the only fallback will be the default file 
        // (e.g. "messages.properties" for basename "messages").
        bean.setFallbackToSystemLocale(false);
        return bean;
    }

    // note [MD] (1) logically not part of message source configuration
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    // note [MD] (1) logically not part of message source configuration
    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
