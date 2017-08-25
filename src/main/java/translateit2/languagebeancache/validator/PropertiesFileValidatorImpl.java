package translateit2.languagebeancache.validator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;

@Component
public class PropertiesFileValidatorImpl implements LanguageFileValidator {

    public PropertiesFileValidatorImpl() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public LanguageFileFormat getFileFormat() {
        return LanguageFileFormat.PROPERTIES;
    }

    @Override
    public void validateApplicationName(String appName, String expectedApplicationName) {
        if (!(appName.equalsIgnoreCase(expectedApplicationName)))
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_APPLICATION_NAME_IN_FILE_NAME);
    }
    
    @Override
    public void validateLocale(Locale appLocale, Locale expectedLocale) {
        if (!(appLocale.equals(expectedLocale)))
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_LOCALE_IN_FILE_NAME);
    }    
    
    @Override
    public void validateCharacterSet(Path uploadedLngFile, LanguageFileType typeExpected) {

        boolean isUploadedUTF_8 = true;
        try {
            isUploadedUTF_8 = isCorrectCharset(uploadedLngFile, StandardCharsets.UTF_8);
        } catch (TranslateIt2Exception e) {
            throw e;
        }

        boolean isUploadedISO8859 = false;
        if (!isUploadedUTF_8)
            try {
                isUploadedISO8859 = isCorrectCharset(uploadedLngFile, StandardCharsets.ISO_8859_1);
            } catch (TranslateIt2Exception e) {
                throw e;
            }

        // UTF-8 is identical to ISO8859 for the first 128 ASCII characters
        // which
        // include all the standard keyboard characters. After that, characters
        // are encoded as a multi-byte sequence.
        // if written in english it is both UTF-8 and ISO8859 encoded

        // if typeExpected == ISO8859 and uploaded is UTF-8 => reject
        if (typeExpected.equals(LanguageFileType.ISO8859_1) && isUploadedUTF_8)
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_CHARACTERSET_IN_FILE);
        // ("The encoding is not same as defined for the version. It should be
        // ISO8859.");

        // if typeExpected == UTF-8 and uploaded is ISO8859 => reject
        if (typeExpected.equals(LanguageFileType.UTF_8) && isUploadedISO8859)
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_CHARACTERSET_IN_FILE);
        // ("The encoding is not same as defined for the version. It should be
        // UTF-8.");
    }
    
    private boolean isCorrectCharset(Path uploadedLngFile, Charset charset) {
        try {
            Files.readAllLines(uploadedLngFile, charset);
        } catch (MalformedInputException e) {
            return false; // do nothing is OK
        } catch (IOException e) {
            throw new TranslateIt2Exception("Unexpected exception thrown while testing charset of a properties file");
        }
        return true; // if charset == UTF8 and no exceptions => file is UTF8
    }


}
