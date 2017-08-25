package translateit2.validator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import translateit2.exception.TranslateIt2Exception;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dto.ProjectDto;
import translateit2.service.ProjectService;
import translateit2.service.WorkService;
import translateit2.util.Messages;
import translateit2.util.OrderedProperties;

@Component
public class Iso8859ValidatorImpl implements LanguageFileValidator {
    
    private Messages messages;

    private ProjectService projectService;

    private WorkService workService;

    @Override
    public void checkEmptyFile(Path uploadedLngFile, long workId) {
        Charset charset = getCharSet(workId);
        LinkedHashMap<String, String> segments = null;
        try {
            segments = (LinkedHashMap<String, String>) getPropSegments(uploadedLngFile, charset);
        } catch (IOException e) { //
            throw new TranslateIt2Exception((messages.get("FileStorageService.not_read_properties_file")) + " "
                    + uploadedLngFile.getFileName());
        }
        if (segments.isEmpty())
            // Errors instanssi
            throw new TranslateIt2Exception(
                    (messages.get("FileStorageService.empty_properties_file")) + " " + uploadedLngFile.getFileName());

    }

    @Override
    public void checkFileCharSet(Path uploadedLngFile, long workId) {
        LanguageFileType typeExpected = getExpectedFiletype(workId);

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
            throw new TranslateIt2Exception(messages.get("FileStorageService.false_ISO8859_encoding"));
        // ("The encoding is not same as defined for the version. It should be
        // ISO8859.");

        // if typeExpected == UTF-8 and uploaded is ISO8859 => reject
        if (typeExpected.equals(LanguageFileType.UTF_8) && isUploadedISO8859)
            throw new TranslateIt2Exception(messages.get("FileStorageService.false_UTF8_encoding"));
        // ("The encoding is not same as defined for the version. It should be
        // UTF-8.");
    }

    @Override
    public void checkFileExtension(Path uploadedLngFile) {
        // check extension
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.properties");

        if (!(matcher.matches(uploadedLngFile.getFileName())))
            throw new TranslateIt2Exception(
                    (messages.get("FileStorageService.not_properties_file")) + " " + uploadedLngFile.getFileName());
    }

    @Override
    public String checkFileNameFormat(Path uploadedLngFile) {
        String appName = null;
        // check file name format i.e. appName_region_language*.properties
        // or just appName_language*.properties => reject
        appName = sanityCheck(uploadedLngFile.getFileName().toString());
        if (appName == null)
            throw new TranslateIt2Exception(
                    (messages.get("FileStorageService.code_missing")) + " " + uploadedLngFile.getFileName());

        Locale locale = getLocaleFromString(uploadedLngFile.getFileName().toString(), ext -> ext.equals("properties"));
        if (locale == null)
            throw new TranslateIt2Exception(
                    (messages.get("FileStorageService.code_missing")) + " " + uploadedLngFile.getFileName());

        return appName;
    }

    @Override
    public Charset getCharSet(long workId) {
        LanguageFileType typeExpected = null;
        final long projectId = workService.getWorkDtoById(workId).getProjectId();
        ProjectDto projectDto = projectService.getProjectDtoById(projectId);
        typeExpected = projectDto.getType();

        Charset charset = StandardCharsets.UTF_8;
        if (typeExpected.equals(LanguageFileType.ISO8859_1))
            charset = StandardCharsets.ISO_8859_1;
        return charset;
    }

    @Autowired
    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setWorkService(WorkService workService) {
        this.workService = workService;
    }

    private LanguageFileType getExpectedFiletype(final long workId) {
        final long projectId = workService.getWorkDtoById(workId).getProjectId();
        return projectService.getProjectDtoById(projectId).getType();
    }

    /**
     * Convert a string based locale into a Locale Object. Accepts following
     * file naming forms "appname_{language}.extension"
     * "appname_{language}_{country}.extension".
     * "appname_{language}_{country}_{variant}.extension"
     * "appname_{language}_{country}.extension"
     * 
     * @param localeString
     *            The String
     * @return the Locale
     */
    private Locale getLocaleFromString(String localeString, Predicate<String> p) {
        if (localeString == null)
            return null;

        localeString = localeString.trim();

        String extension = "";
        int i = localeString.lastIndexOf('.');
        if (i > 0)
            extension = localeString.substring(i + 1);

        if (!p.test(extension))
            return null;

        // get application name end position
        int appIndex = localeString.indexOf('_');

        int languageIndex = localeString.indexOf('_', appIndex + 1);

        String language = null;
        if (languageIndex == -1) {
            // No further "_" so is "{language}" only
            language = localeString.substring(appIndex + 1, appIndex + 3);
            return new Locale(language, language.toUpperCase());
        } else {
            language = localeString.substring(appIndex + 1, languageIndex);
        }

        // Extract language which is exactly two characters long
        if (languageIndex - appIndex != 3)
            return null;

        // Extract country
        int countryIndex = localeString.indexOf('_', languageIndex + 1);
        if (countryIndex == -1)
            countryIndex = localeString.indexOf('.', languageIndex + 1);

        // Extract country which is exactly two characters long
        // end if not return only language
        if (countryIndex - languageIndex != 3) {
            return new Locale(language, language.toUpperCase());
        }

        String country = null;
        if (countryIndex == -1) {
            // No further "_" so is "{language}_{country}"
            country = localeString.substring(languageIndex + 1);
            country = country.substring(0, 2);
            return new Locale(language, country.toUpperCase());
        } else {
            // Assume all remaining is the variant so is
            // "{language}_{country}_{variant}"
            country = localeString.substring(languageIndex + 1, countryIndex);
            // String variant = localeString.substring(countryIndex+1);
            return new Locale(language, country.toUpperCase());
        }
    }

    // TODO: test this
    private HashMap<String, String> getPropSegments(Path inputPath, Charset charset) throws IOException {
        HashMap<String, String> map = new LinkedHashMap<String, String>();
        OrderedProperties srcProp = new OrderedProperties();

        try (InputStream stream = new FileInputStream(inputPath.toString());
             InputStreamReader isr = new InputStreamReader(stream, charset)) {
            srcProp.load(isr);
            Set<String> keys = srcProp.stringPropertyNames();
            // checks for at least one (ASCII) alphanumeric character.
            map = keys.stream().filter(k -> k.toString().matches(".*\\w.*")).collect(Collectors.toMap(k -> k.toString(),
                    k -> srcProp.getProperty(k), (v1, v2) -> v1, LinkedHashMap::new));

            map.forEach((k, v) -> System.out.println(k + "\n" + v));

        } catch (FileNotFoundException e) {
            throw new IOException("Error loading reading property file.", e);
        }

        return map;
    }

    private HashMap<String, String> getPropSegmentsToBeRemoved(Path inputPath, Charset charset) throws IOException {

        InputStreamReader isr = null;
        InputStream stream = null;
        HashMap<String, String> map = new LinkedHashMap<String, String>();
        OrderedProperties srcProp = new OrderedProperties();

        try {
            stream = new FileInputStream(inputPath.toString());
            isr = new InputStreamReader(stream, charset);
            srcProp.load(isr);
            Set<String> keys = srcProp.stringPropertyNames();
            // checks for at least one (ASCII) alphanumeric character.
            map = keys.stream().filter(k -> k.toString().matches(".*\\w.*")).collect(Collectors.toMap(k -> k.toString(),
                    k -> srcProp.getProperty(k), (v1, v2) -> v1, LinkedHashMap::new));

            map.forEach((k, v) -> System.out.println(k + "\n" + v));

        } catch (FileNotFoundException e) {
            throw new IOException("Error loading reading property file.", e);
        } finally {
            try { if (stream != null) stream.close(); } catch(IOException e) {/* closing quietly */ } 
            try { if (isr != null) isr.close(); } catch(IOException e) {/* closing quietly */ } 
        }

        return map;
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
        // encoded
    }

    // return application name otherwise null
    private String sanityCheck(String localeString) {
        if (localeString == null)
            return null;
        localeString = localeString.trim();

        // Extract application name
        int appIndex = localeString.indexOf('_');
        if (appIndex == -1) {
            // No further "_" so this is "{application}" only file
            return null;
        } else
            return localeString.substring(0, appIndex);
    }
}
