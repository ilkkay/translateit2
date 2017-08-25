package translateit2.util;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 *
 * Used widely in this project for localizing messages and in unit testing for
 * asserting that the exception returned was a correct one.
 * 
 * @author Ilkka
 *
 */
@ConfigurationProperties(prefix = "translateit2.localeresolver")
@Component
public class Messages {

    private String locale;

    private MessageSourceAccessor accessor;
    
    private MessageSource messageSource;
    
    public void setLocale(String locale) {
        this.locale = locale;
    }

    // used in tests that will be using english
    public void resetLocale(Locale locale) {
        accessor = new MessageSourceAccessor(messageSource, locale);
    }

    @PostConstruct
    private void init() { 
        accessor = new MessageSourceAccessor(messageSource, new Locale(locale));
    }

    @Autowired
    public Messages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String code) {
        String msg = "";
        try {
            msg = accessor.getMessage(code);
        } catch (NoSuchMessageException e) {
            //return "Text not implemented for " + code;
        }

        return msg;
    }

    public String get(String code, Locale locale) {
        String msg = "";
        try {
            msg = accessor.getMessage(code,locale);
        } catch (NoSuchMessageException e) {
        }

        return msg;
    }
    
    public String get(String code, String[] args) {
        String msg = null;
        try {
            msg = accessor.getMessage(code, args);
        } catch (NoSuchMessageException e) {
            return "Text not implemented for " + code;
        }

        return msg;
    }

    // Used in unit testing to check that the exception message is the one
    // we are expecting. The code returns only part of the message because
    // of message parameters we cannot compare the whole message
    //
    // Segment size must be between {0} and {1} characters
    //
    public String getPart(String code) {
        String msg = null;
        try {
            msg = accessor.getMessage(code);
        } catch (NoSuchMessageException e) {
            return "Text not implemented for " + code;
        }

        String parts[] = msg.split("[{]");
        if (parts.length == 1)
            return msg;

        int maxLen = -1;
        String maxStr = "";
        for (String s : parts) {
            if (s.length() > maxLen) {
                maxStr = s;
                maxLen = s.length();
            }
        }

        parts = maxStr.split("[}]");
        if (parts.length == 1)
            return maxStr;
        else
            return parts[1];
    }



}
