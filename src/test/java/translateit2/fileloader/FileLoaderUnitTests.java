package translateit2.fileloader;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileLoaderUnitTests {

    private String testUploadDir="upload-dir";

    private String testDownloadDir="download-dir";
    
    private String testRootTemporaryDirectory ="D:\\sw-tools\\STS\\TranslateIT2v4";

    @Before
    public void setUp() throws Exception {

        if (!(Files.exists(Paths.get(testUploadDir)))) 
            Files.createDirectory(Paths.get(testUploadDir));  
        else
            FileSystemUtils.deleteRecursively(Paths.get(testUploadDir).toFile()); 
        
        if (!(Files.exists(Paths.get(testDownloadDir)))) 
            Files.createDirectory(Paths.get(testDownloadDir));  
        else
            FileSystemUtils.deleteRecursively(Paths.get(testDownloadDir).toFile()); 
    }

    @After
    public void tearDown() throws Exception {
        FileSystemUtils.deleteRecursively(Paths.get(testUploadDir).toFile()); 
        FileSystemUtils.deleteRecursively(Paths.get(testDownloadDir).toFile()); 
    }

    @Test
    public void storeMultipartFile_assertUploadedFile() throws IOException {

        // when MultipartFile exists
        Path filePath = Paths.get("d:\\dotcms_fi-utf8.properties");
        File file = new File("d:\\dotcms_fi-utf8.properties");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));

        // then store it upload directory
        List<Path> paths = new ArrayList<Path>();
        assertThatCode(() -> { paths.add(fileloader().storeToUploadDirectory(multipartFile)); } )
        .doesNotThrowAnyException(); 

        // assert that the file exists
        assertThat(Files.exists(paths.get(0)), equalTo(true));

        // and assert that the size is expected size
        try {
            int expectedSize = (int) Files.size(filePath);
            assertTrue(Files.size(paths.get(0)) ==  expectedSize);
        } catch (IOException e) {
            fail ("Could not read file sizes.");
        }
    }

    @Test
    public void deleteUploadDirectory_assertDirectoryNotExists() throws IOException {

        // when upload exists
        Path uploadDir = Paths.get(testUploadDir);
        if (Files.notExists(uploadDir)) Files.createDirectory(uploadDir);

        // then remove it with contents
        assertThatCode(() -> { fileloader().deleteUploadedFiles();}  )
        .doesNotThrowAnyException();

        // assert it exists no more
        assertThat(Files.exists(uploadDir), equalTo(false));

    }

    @Test
    public void deleteUploadedFile_assertFileNotExists() throws IOException {

        // when file exists
        Path uploadDir = Paths.get(testUploadDir);
        if (Files.notExists(uploadDir)) Files.createDirectory(uploadDir);
        Path filePath = uploadDir.resolve("testfile.txt"); 
        if (Files.notExists(filePath)) Files.createFile(filePath);

        // then remove it with contents
        assertThatCode(() -> { fileloader().deleteUploadedFile(filePath);}  )
        .doesNotThrowAnyException();

        // assert it exists no more
        assertThat(Files.notExists(filePath), equalTo(true));

    }

    @Test
    public void getFilesInDownloadDirectory_assertFileCount() throws IOException {
        // when two files exists
        Path downDir = Paths.get(testDownloadDir);
        FileSystemUtils.deleteRecursively(downDir.toFile());
        if (Files.notExists(downDir)) Files.createDirectory(downDir);
        Path filePath1 = downDir.resolve("first.txt"); 
        if (Files.notExists(filePath1)) Files.createFile(filePath1);
        Path filePath2 = downDir.resolve("second.txt"); 
        if (Files.notExists(filePath2)) Files.createFile(filePath2);

        // then get paths of files in upload directory
        List<Stream<Path>> paths = new ArrayList<Stream<Path>>();        
        assertThatCode(() -> { paths.add(fileloader().getPathsOfDownloadableFiles()); } )
        .doesNotThrowAnyException(); 

        // assert file name
        Stream<Path> streamPath = paths.get(0);        
        List<String> fileNames = streamPath.map(path -> path.getFileName().toString()).collect(Collectors.toList());        
        assertThat(fileNames.size(), equalTo(2));
    }

    @Test
    public void storeFileToDownloadDirectory_assertParentDirectoryy() {
        Path tmpFilePath = Paths.get("d:\\dotcms_temp.properties");

        try {
            Files.copy(Paths.get("d:\\dotcms_fi-utf8.properties"), tmpFilePath,StandardCopyOption.REPLACE_EXISTING );
        } catch (IOException e) {
            fail("Unexpected exception");
        }
        String downloadFilename = "dotcms_download.properties";

        List<Stream<Path>> paths = new ArrayList<Stream<Path>>();        
        assertThatCode(() -> { paths.add(fileloader().storeToDownloadDirectory(tmpFilePath,downloadFilename)); } )
        .doesNotThrowAnyException(); 

        // assert stream name count
        Stream<Path> streamPath = paths.get(0);        
        List<Path> streamPaths = streamPath.map(path -> path.toAbsolutePath()).collect(Collectors.toList());        

        assertThat(streamPaths.size(), equalTo(1));

        String expectedDownloadDir = fileloader().getDownloadPath("test.txt").getParent().getFileName().toString();
        String returnedDownloadDir = streamPaths.get(0).getParent().getFileName().toString();
        assertThat(expectedDownloadDir,equalTo(returnedDownloadDir));
    }

    private FileLoader fileloader() {
        FileLoaderProperties props = new FileLoaderProperties();
        props.setUploadLocation(testUploadDir);
        props.setDownloadLocation(testDownloadDir);
        props.setRootTemporaryDirectory(testRootTemporaryDirectory);
        return new FileLoaderImpl(props); 
    }
}
