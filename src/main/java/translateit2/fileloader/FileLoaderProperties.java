package translateit2.fileloader;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "translateit2.fileloader")
public class FileLoaderProperties {

    private String uploadLocation;
    private String downloadLocation;
    private String permanentLocation; 
    private String rootPermanentDirectory;
    private String rootTemporaryDirectory;

/* 
translateit2.fileloader.rootPermanentDirectory=D:\\sw-tools\\STS\\TranslateIT2v4
translateit2.fileloader.rootTemporaryDirectory=D:\\sw-tools\\STS\\TranslateIT2v4
translateit2.fileloader.uploadLocation=upload-dir
translateit2.fileloader.downloadLocation=download-dir
translateit2.fileloader.permanentLocation=permanent-dir
*/
    
    public String getUploadLocation() {
        return uploadLocation;
    }

    public void setUploadLocation(String location) {
        this.uploadLocation = location;
    }

    public String getDownloadLocation() {
        return downloadLocation;
    }

    public void setDownloadLocation(String location) {
        this.downloadLocation = location;
    }

    public void setPermanentLocation(String permanentLocation) {
        this.permanentLocation = permanentLocation;
    }

    public String getPermanentLocation() {
        return this.permanentLocation ;
    }

    public String getRootPermanentDirectory() {
        return rootPermanentDirectory;
    }

    public void setRootPermanentDirectory(String rootPermanentDirectory) {
        this.rootPermanentDirectory = rootPermanentDirectory;
    }

    public String getRootTemporaryDirectory() {
        return rootTemporaryDirectory;
    }

    public void setRootTemporaryDirectory(String rootTemporaryDirectory) {
        this.rootTemporaryDirectory = rootTemporaryDirectory;
    }
}