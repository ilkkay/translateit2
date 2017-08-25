package translateit2.fileLocator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;

import translateit2.exception.TranslateIt2Exception;
import translateit2.fileloader.FileLoaderProperties;
import translateit2.filelocator.FileLocator;
import translateit2.filelocator.FileLocatorImpl;
import translateit2.languagefile.LanguageFileFormat;

public class FileLocatorUnitTests {
    List<Path> newLocation = new ArrayList<Path>();

    private String testPermanentDir = "permanentDir"; 
    private String testRootPermanentDir = "D:\\sw-tools\\STS\\TranslateIT2v4\\TranslateIT2v4";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        newLocation = new ArrayList<Path> ();

        if (!(Files.isDirectory(Paths.get(testPermanentDir)))) 
            //if (!(Files.exists(Paths.get(testPermanentDir)))) 
            Files.createDirectory(Paths.get(testPermanentDir));  
        else
            FileSystemUtils.deleteRecursively(Paths.get(testPermanentDir).toFile()); 
    }

    @After
    public void tearDown() throws Exception {
        FileSystemUtils.deleteRecursively(Paths.get(testPermanentDir).toFile()); 
    }

    @Test
    public void moveFile_failIfFileMissing() {

        // initialize
        Path permanentPath = Paths.get(testPermanentDir);
        Path permanentFilePath = permanentPath.resolve("messages_fi.properties");

        // WHEN upload file has no path

        // THEN
        assertThatCode(() -> filelocator().moveUploadedFileIntoPermanentFileSystem(
                permanentFilePath, LanguageFileFormat.PROPERTIES))
        .isExactlyInstanceOf(TranslateIt2Exception.class);                
    }

    @Test
    public void moveFile_assertFileExists() {

        // WHEN
        Path permanentPath = Paths.get(testPermanentDir);
        if (Files.notExists(permanentPath))
            try {
                Files.createDirectory(permanentPath);
            } catch (IOException e1) {
                fail ("Could not copy test directory.");
            }

        Path srcFilePath = Paths.get("D:\\messages_fi.properties");
        Path permanentFilePath = permanentPath.resolve("messages_fi.properties");        
        try {
            Files.copy(srcFilePath, permanentFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            fail ("Could not copy test file.");
        }

        // THEN
        List<Path> newLocation = new ArrayList<Path> ();
        assertThatCode(() -> { newLocation.add(filelocator().moveUploadedFileIntoPermanentFileSystem(
                permanentFilePath, LanguageFileFormat.PROPERTIES)); } )
        .doesNotThrowAnyException();        

        // the permanent file exists
        assertThat(Files.exists(newLocation.get(0)), equalTo(true));

        // and the size is expected size
        try {
            int expectedSize = (int) Files.size(srcFilePath);
            assertTrue(Files.size(newLocation.get(0)) ==  expectedSize);
        } catch (IOException e) {
            fail ("Could not read file sizes.");
        }

        // remove the new moved file
        FileSystemUtils.deleteRecursively(newLocation.get(0).getParent().toFile());               
    }

    @Test
    public void createTemporaryFile_assertFileExists() {

        List <String>  downloadFileAsList = new ArrayList<String>();

        downloadFileAsList.add("##");
        downloadFileAsList.add("## Messages");
        downloadFileAsList.add("##");
        downloadFileAsList.add("");
        downloadFileAsList.add("## DOTCMS-3022");
        downloadFileAsList.add("alert-file-too-large-takes-lot-of-time=Tiedoston tallennus voi kestää pidempään, jos sen koko on suuri.");
        downloadFileAsList.add("");
        downloadFileAsList.add("a-new-password-can-only-be-sent-to-an-external-email-address=Uusi salasana voidaan lähettää vain ulkoiseen sähköpostiosoitteeseen.");
        downloadFileAsList.add("a-new-password-has-been-sent-to-x=Sähköposti ohjeineen on lähetetty osoitteeseen {0}.");
        downloadFileAsList.add("an-email-with-instructions-will-be-sent=Sähköposti ohjeineen lähetetään osoitteeseesi. Jatketaanko?");
        downloadFileAsList.add("reset-password-success=Salasanasi vaihto onnistui.");

        // THEN
        List<Path> newLocation = new ArrayList<Path> ();
        assertThatCode(() -> { newLocation.add(filelocator().createFileIntoPermanentFileSystem(downloadFileAsList, 
                LanguageFileFormat.PROPERTIES, StandardCharsets.UTF_8)); } )
        .doesNotThrowAnyException();        

        // the permanent file exists
        assertThat(Files.exists(newLocation.get(0)), equalTo(true));

        // assert directory name
        String expectedDownloadDirectory = LocalDate.now().toString();
        String returnedDownloadDirectory = newLocation.get(0).getParent().getFileName().toString();
        assertThat(expectedDownloadDirectory ,equalTo(returnedDownloadDirectory ));

        // remove the new file
        FileSystemUtils.deleteRecursively(newLocation.get(0).getParent().toFile()); 
    }

    private FileLocator filelocator() {
        FileLoaderProperties props = new FileLoaderProperties();
        props.setPermanentLocation(testPermanentDir);
        props.setRootPermanentDirectory(testRootPermanentDir);

        return new FileLocatorImpl(props); 
    }
}
