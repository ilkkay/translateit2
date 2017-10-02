package translateit2.directorcreator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;

import translateit2.directorycreator.DirectoryCreator;
import translateit2.directorycreator.DirectoryCreatorImpl;
import translateit2.fileloader.FileLoader;
import translateit2.fileloader.FileLoaderImpl;
import translateit2.fileloader.FileLoaderProperties;
import translateit2.filelocator.FileLocator;
import translateit2.filelocator.FileLocatorImpl;

public class DirectorCreatorUnitTest {

	private String testUploadDir="upload-dir";
	private String testDownloadDir="download-dir";        
	private String testPermanentDir = "permanentDir"; 
	private String testRootTemporaryDirectory ="testRoot";
	private String testRootPermanentDir ="testRoot"; 


	@After
	public void TearDown() throws IOException{
		FileSystemUtils.deleteRecursively(directoryCreator().getDownloadDirectory().toFile());
		FileSystemUtils.deleteRecursively(directoryCreator().getUploadDirectory().toFile());
		FileSystemUtils.deleteRecursively(directoryCreator().getPermanentDirectory().toFile());
	}
	
	@Test
	public void createDownloadDirectory_assertDirectoryExists() {
		List<Path> paths = new ArrayList<Path>();
		assertThatCode(() -> { paths.add(directoryCreator().getDownloadDirectory()); } )
		.doesNotThrowAnyException(); 

        Path expectedDir = Paths.get("").toAbsolutePath().getParent()
        		.resolve(testRootTemporaryDirectory).resolve(testDownloadDir);

        // assert that the directory exists
        assertThat(expectedDir, equalTo(paths.get(0)));
		assertTrue(Files.exists(paths.get(0)));
	}

	@Test
	public void createUploadDirectory_assertDirectoryExists() {
		List<Path> paths = new ArrayList<Path>();
		assertThatCode(() -> { paths.add(directoryCreator().getUploadDirectory()); } )
		.doesNotThrowAnyException(); 

		// assert that the directory exists
		assertTrue(Files.exists(paths.get(0)));
	}
	
	@Test
	public void createPermanentDirectory_assertDirectoryExists() {
		List<Path> paths = new ArrayList<Path>();
		assertThatCode(() -> { paths.add(directoryCreator().getPermanentDirectory()); } )
		.doesNotThrowAnyException(); 

		// assert that the directory exists
		assertTrue(Files.exists(paths.get(0)));
	}
		
	private DirectoryCreator directoryCreator() {
		FileLoaderProperties props = new FileLoaderProperties();
		props.setUploadLocation(testUploadDir);
		props.setDownloadLocation(testDownloadDir);
		props.setPermanentLocation(testPermanentDir);

		Path tempDir = Paths.get("").toAbsolutePath().getParent().resolve(testRootTemporaryDirectory);
		props.setRootTemporaryDirectory(tempDir.toString());
		Path permanentDir = Paths.get("").toAbsolutePath().getParent().resolve(testRootPermanentDir);        
		props.setRootPermanentDirectory(permanentDir.toString());

		return new DirectoryCreatorImpl(props); 
	}

}
