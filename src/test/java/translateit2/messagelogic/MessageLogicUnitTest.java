package translateit2.messagelogic;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import translateit2.util.MessageLogic;

import java.util.Locale;

public class MessageLogicUnitTest {

	private MessageLogic messages;
	
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("ISO-8859-1");
        messageSource.setFallbackToSystemLocale(false);

        messages = new MessageLogic(messageSource);
    }

    @Test
    public void getTestMessage_assertMessageFromPropertiesFile() {
    	messages.resetLocale(Locale.ENGLISH);
    	
    	String retrievedMsg = messages.get("MessageLogic.test_message");
    	assert("Test message".equals(retrievedMsg));
    	
    	messages.resetLocale(new Locale("fi"));
    	
    	retrievedMsg = messages.get("MessageLogic.test_message");
    	assert("Testi-ilmoitus".equals(retrievedMsg));
    	
    }
    
    @Test
    public void getTestMessage_assertMessageFromFallbackPropertiesFile() {
    	messages.resetLocale(Locale.FRENCH);
    	
    	String retrievedMsg = messages.get("MessageLogic.test_message");
    	assert("Test message".equals(retrievedMsg));
    }

    @Test
    public void getTranslatedValidationMessage_assertMessageFromPropertiesFile() {
    	messages.resetLocale(new Locale("fi"));
    	
        String retrievedMsg = messages.get("javax.validation.constraints.NotNull.message");
    	assert("ei voi olla null".equals(retrievedMsg));

    	retrievedMsg = messages.get("org.hibernate.validator.constraints.NotBlank.message");
        // note [MD] uses JVM-assertion, probably not what was intended
        assert("ei voi olla tyhjä".equals(retrievedMsg));


    }

}
