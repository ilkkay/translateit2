package translateit2.filenameresolver;

import java.util.Locale;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.languagefile.LanguageFileFormat;

@Component
public class FileNameResolverImpl implements FileNameResolver{
    @Override
    public String getApplicationName(String filename) {
        if (filename == null)
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_READ_LANGUAGE_FROM_FILE_NAME);

        // Extract application name
        int appIndex = filename.indexOf('_');
        if (appIndex == -1) {
            // No further "_" so this is "{application}" only file
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_READ_LANGUAGE_FROM_FILE_NAME);
        } else
            return filename.substring(0, appIndex);
    }

    /**
     * Get Locale from file name. Accepts following file naming conventions: 
     * "appname_{language}.extension"
     * "appname_{language}_{country}.extension".
     * "appname_{language}_{country}_{variant}.extension"
     * 
     */
    @Override
    public Locale getLocaleFromFilename(String fileName, Predicate<String> p) {

        // check extension
        int extPos = fileName.lastIndexOf('.');
        if ((extPos > 0) && (!p.test(fileName.substring(extPos + 1).toLowerCase()))) 
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_EXTENSION_IN_FILE_NAME);

        // get application name end position
        int appIndex = fileName.indexOf('_');
        if (appIndex == -1)
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_READ_APPLICATION_NAME_FROM_FILE_NAME);            

        // language can be found between two underscores (i.e. appname_fi_FI.extension)
        // OR between language code and extension (i.e. appname_fi.extension)
        String language;
        int languageIndex = fileName.indexOf('_', appIndex + 1);
        if (languageIndex == -1) {
            // No further "_" so is "{language}" only
            int extensionIndex = fileName.indexOf('.', appIndex + 1);

            // Extract language which is exactly two characters long
            if (extensionIndex - appIndex != 3){
                // file name can be also like this: dotcms_fi-UTF.properties
                int dashIndex = fileName.indexOf('-', appIndex + 1);
                if (dashIndex - appIndex == 3) {
                    language = fileName.substring(appIndex + 1, appIndex + 3);
                    return new Locale(language.toLowerCase(), language.toUpperCase());
                }
                else
                    throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_READ_LANGUAGE_FROM_FILE_NAME);
            }
            else {
                language = fileName.substring(appIndex + 1, appIndex + 3);
                return new Locale(language.toLowerCase(), language.toUpperCase());
            }
        } else {
            language = fileName.substring(appIndex + 1, languageIndex);
        }

        // Extract language which is exactly two characters long
        if (languageIndex - appIndex != 3) 
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_READ_LANGUAGE_FROM_FILE_NAME);

        // Extract country
        int countryIndex = fileName.indexOf('_', languageIndex + 1);
        if (countryIndex == -1)
            countryIndex = fileName.indexOf('.', languageIndex + 1);

        // Extract country which is exactly two characters long
        // end if not return only language
        if (countryIndex - languageIndex != 3) {
            return new Locale(language.toLowerCase(), language.toUpperCase());
        }

        if (countryIndex == -1) {
            // No further "_" so is "{language}_{country}"
            String country = fileName.substring(languageIndex + 1);
            country = country.substring(0, 2);
            return new Locale(language.toLowerCase(), country.toUpperCase());
        } else {
            // Assume all remaining is the variant so is
            // "{language}_{country}_{variant}"
            String country = fileName.substring(languageIndex + 1, countryIndex);
            // String variant = localeString.substring(countryIndex+1);
            return new Locale(language.toLowerCase(), country.toUpperCase());
        }
    }

    @Override
    public String getDownloadFilename(String originalFileName, Locale locale, LanguageFileFormat format) {
        return originalFileName + "_" + locale.toString() + "." + format.toString();
    }
}
