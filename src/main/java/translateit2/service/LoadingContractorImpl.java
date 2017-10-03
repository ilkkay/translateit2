package translateit2.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartFile;
import translateit2.configuration.CharSetResolver;
import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.fileloader.FileLoader;
import translateit2.filelocator.FileLocator;
import translateit2.filenameresolver.FileNameResolver;
import translateit2.languagebeancache.LanguageBeanCache;
import translateit2.languagebeancache.reader.LanguageFileReader;
import translateit2.languagebeancache.validator.LanguageFileValidator;
import translateit2.languagebeancache.writer.LanguageFileWriter;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dao.FileInfoRepository;
import translateit2.persistence.dao.ProjectRepository;
import translateit2.persistence.dao.UnitRepository;
import translateit2.persistence.dao.WorkRepository;
import translateit2.persistence.model.*;

import javax.transaction.Transactional;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// note [MD] (3) @EnableTransactionManagement - why here?
@EnableTransactionManagement
@Service
public class LoadingContractorImpl implements LoadingContractor {
    static final Logger logger = LogManager.getLogger(LoadingContractorImpl.class);

    @Autowired
    private CharSetResolver charsetResolver;
    
    @Autowired
    private FileLoader fileloader;

    @Autowired
    private FileLocator filelocator;

    @Autowired
    private FileNameResolver fileNameResolver;

    @Autowired
    private LanguageBeanCache<LanguageFileFormat, LanguageFileReader> fileReaderCache;

    @Autowired
    private LanguageBeanCache<LanguageFileFormat, LanguageFileWriter> fileWriterCache;

    @Autowired
    private LanguageBeanCache<LanguageFileFormat, LanguageFileValidator> fileValidatorCache;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private WorkRepository workRepo;

    @Autowired
    private FileInfoRepository fileInfoRepo;

    @Autowired
    private UnitRepository unitRepo;

    @Override
    public Stream <Path> downloadTarget(final long workId) {
        if (!(workRepo.exists(workId))) {
            logger.error("Work with id {} not found.", workId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }

        Work work = workRepo.findOne(workId);
                
        // get a map of translated units (i.e source segment and its translation)
        Map<String, String> map = getSegmentsMap(workId);
        
        // get original language file (i.e backup file) into string list
        LanguageFileFormat format = work.getProject().getFormat();
        LanguageFileReader reader = fileReaderCache.getService(format).get();
        String backupFile = getBackupFileName(workId);
        // we don't validate backup file since we did it during the upload phase  
        List<String> originalFileAsList = reader.getOriginalFileAsList(Paths.get(backupFile),getCharSet(workId));
        
        // merge the map of translated units with the original language file
        LanguageFileWriter writer = fileWriterCache.getService(format).get();
        List<String> downloadFileAsList = writer.mergeWithOriginalFile(map, originalFileAsList);
        
        // create filename for the download file
        String originalFileName = work.getOriginalFile();
        Locale locale = work.getLocale();
        Charset charset = getCharSet(workId);
        String downloadFilename = fileNameResolver.getDownloadFilename(originalFileName,locale,format);

        // store into a temporary file in permanent location
        Path tmpFilePath = filelocator.createFileIntoPermanentFileSystem(downloadFileAsList, format, charset);
        
        // move file from permanent location to download directory
        Stream <Path> downloadStreamPath = fileloader.storeToDownloadDirectory(tmpFilePath,downloadFilename);
        
        // delete temporary file
        filelocator.deleteFileFromPermanentFileSystem(tmpFilePath);
        
        return downloadStreamPath;
    }

    @Override
    public void uploadTarget(final MultipartFile multipartFile, final long workId) {
        if (!(workRepo.exists(workId))) {
            logger.error("Work with id {} not found.", workId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }

        // move file from server to temporary location i.e. to upload directory
        Path uploadedFile = fileloader.storeToUploadDirectory(multipartFile);

        // get application name from filename
        String originalFileName = uploadedFile.getFileName().toString();
        String appName = fileNameResolver.getApplicationName(uploadedFile.getFileName().toString());

        // check extension and get locale from filename
        LanguageFileFormat format = getFormat(workId);        
        Locale appLocale = fileNameResolver.getLocaleFromFilename(uploadedFile.getFileName().toString(), 
                ext -> ext.equals(format.toString().toLowerCase()));

        // validate appName, locale and character set used in file 
        LanguageFileValidator validator = fileValidatorCache.getService(format).get();
        validator.validateCharacterSet(uploadedFile, getExpectedType(workId));
        validator.validateApplicationName(appName, getExpectedApplicationName(workId));
        validator.validateLocale(appLocale, getExpectedTargetLocale(workId));

        // read key/values pairs from the language file
        LanguageFileReader reader = fileReaderCache.getService(format).get();
        LinkedHashMap<String, String> segments = (LinkedHashMap<String, String>)reader.
                getSegments(uploadedFile, getCharSet(workId));

        // upload segments to data base
        loadTargetSegmentsToDatabase(segments, workId);

        // the uploaded (target) language file will be removed silently
        fileloader.deleteUploadedFile(uploadedFile);        
    }

    @Override
    public void uploadSource(final MultipartFile multipartFile, final long workId) {        
        if (!(workRepo.exists(workId))) {
            logger.error("Work with id {} not found.", workId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }

        if (isSourceFileReload(workId)){
            logger.error("Trying to reload source file.", workId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }

        // check that availability of validator, reader and writer service for this format
        checkServiceAvailability(workId);

        // commit that file processing starts  

        // move file from server to temporary location i.e. to upload directory
        Path uploadedFile = fileloader.storeToUploadDirectory(multipartFile);

        // get application name from filename
        String originalFileName = uploadedFile.getFileName().toString();
        String appName = fileNameResolver.getApplicationName(uploadedFile.getFileName().toString());

        // check extension and get locale from filename
        LanguageFileFormat format = getFormat(workId);        
        Locale appLocale = fileNameResolver.getLocaleFromFilename(uploadedFile.getFileName().toString(), 
                ext -> ext.equals(format.toString().toLowerCase()));

        // validate character set used in file 
        LanguageFileValidator validator = fileValidatorCache.getService(format).get();
        validator.validateCharacterSet(uploadedFile, getExpectedType(workId));   
        validator.validateLocale(appLocale, getExpectedSourceLocale(workId));

        // move file to a permanent location       
        Path uploadedFilePath = filelocator.moveUploadedFileIntoPermanentFileSystem(uploadedFile, format);

        // read key/values pairs from the language file
        LanguageFileReader reader = fileReaderCache.getService(format).get();
        LinkedHashMap<String, String> segments = (LinkedHashMap<String, String>)reader.
                getSegments(uploadedFilePath, getCharSet(workId));

        // we move all the relevant into to the database, BUT
        // if there has been a rollback, we need to remove the uploaded file
        // and notify what has happened
        // test something somewhere (javax.persistence.RollbackException)
        loadSourceSegmentsToDatabase(originalFileName, appName, appLocale, uploadedFilePath, segments, workId);

        // note [MD] (3) example of transaction helper usage (bundleHelper should be a bean & autowired into this class)
//        final TransactionBundleHelper bundleHelper = new TransactionBundleHelper();
//        bundleHelper.inReadOnlyTransaction(() -> loadSourceSegmentsToDatabase(originalFileName, appName, appLocale, uploadedFilePath, segments, workId));

        // commit that file processing has finished  
    }

    // note [MD] (3) helper itself
/*
    @Component
    public static class TransactionBundleHelper {

        @org.springframework.transaction.annotation.Transactional(readOnly = true)
        public void inReadOnlyTransaction(Runnable task) {
            task.run();
        }

        @org.springframework.transaction.annotation.Transactional
        public void inTransaction(Runnable task) {
            task.run();
        }

        @org.springframework.transaction.annotation.Transactional(readOnly = true)
        public <T> T inReadOnlyTransaction(Supplier<T> task) {
            return task.get();
        }

        @org.springframework.transaction.annotation.Transactional
        public <T> T inTransaction(Supplier<T> task) {
            return task.get();
        }

    }
*/

    @Transactional
    private void loadTargetSegmentsToDatabase(final HashMap<String, String> segments, final long workId) {
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        for (Unit unit : units) {
            unit.getTarget().setText(segments.get(unit.getSegmentKey()));
            unit.getTarget().setState(State.TRANSLATED);
        }
        unitRepo.save(units);
    }


    private boolean isSourceFileReload(final long workId) {
        return (unitRepo.countByWorkId(workId) > 0);
    }

    // Only unchecked exceptions (that is, subclasses of java.lang.RuntimeException)
    // are rollbacked by default
    // we can safely assume you are doing your database operations through Spring, Hibernate, 
    // or other JDBC wrappers. These JDBC wrappers don't typically throw checked exceptions, 
    // they throw runtime exceptions that wrap the JDBC SQLException types.
    @Transactional //(rollbackOn = Exception.class)
    private boolean loadSourceSegmentsToDatabase(final String originalFileName, final String appName, 
    		final Locale appLocale, final Path uploadedFilePath, final HashMap<String, String> segments, 
    		long workId) {


        // upload to database 
        loadSourceSegmentsToDatabase( segments, workId);

        // update file info
        long fileInfoId = updateFileInfo(uploadedFilePath,originalFileName, workId);

        // once you've loaded source file, the work status will be NEw
        updateWork(appName, Status.NEW, workId);    

        return true;
    }

    // note [MD] (3) suspicious @Transactional private methods
    @Transactional
    private void removeUnitDtos(final long workId) {
        // note [MD] (3) pretty un-SQLish way to do this
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        unitRepo.delete(units);        
    }

    @Transactional
    private Locale getExpectedSourceLocale(final long workId) {
        // note [MD] (3) repeated find, unused variable (same repeats below)
        Project project = workRepo.findOne(workId).getProject();
        return workRepo.findOne(workId).getProject().getSourceLocale();
    }

    @Transactional
    private Locale getExpectedTargetLocale(final long workId) {
        Work work = workRepo.findOne(workId);
        return workRepo.findOne(workId).getLocale();
    }

    @Transactional
    private String getExpectedApplicationName(final long workId) {
        Work work = workRepo.findOne(workId);
        return  work.getOriginalFile();
    }

    @Transactional
    private LanguageFileType getExpectedType(final long workId) {
        Work work = workRepo.findOne(workId);
        return  projectRepo.findOne(work.getProject().getId()).getType();
    }

    @Transactional
    private Charset getCharSet(final long workId) {
        return charsetResolver.getProjectCharSet(
                workRepo.findOne(workId).getProject().getId());
    }
    
    @Transactional
    private Map<String, String> getSegmentsMap(final long workId) {
        // note [MD] (3) using a toMap collector here would be a more streamish way to do the whole thing here
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        Map<String, String> map = new HashMap<String, String>();
        units.stream().forEach(dto -> map.put(dto.getSegmentKey(), dto.getTarget().getText()));

        return map;
    }

    @Transactional
    private LanguageFileFormat getFormat(final long workId) {
        Work work = workRepo.findOne(workId);
        return projectRepo.findOne(work.getProject().getId()).getFormat();        
    }

    @Transactional
    private void updateStatus(final Status newStatus, final long workId) {
        Work work = workRepo.findOne(workId);
        work.setStatus(newStatus);
        workRepo.save(work);

        logger.debug( "Leaving updateWork with {}", work.toString());
    }

    @Transactional
    private String getBackupFileName(final long workId) {
        if (fileInfoRepo.findByWorkId(workId).isPresent())
            return fileInfoRepo.findByWorkId(workId).get().getBackup_file();
        else
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
    }
    
    @Transactional
    private long updateFileInfo(final Path uploadedFilePath, final String OriginalFile, long workId) {
        FileInfo fileInfo;
        if (fileInfoRepo.findByWorkId(workId).isPresent())
            fileInfo = fileInfoRepo.findByWorkId(workId).get();
        else
            fileInfo = new FileInfo();
        
        fileInfo.setBackup_file(uploadedFilePath.toString());
        fileInfo.setOriginal_file(OriginalFile);
        fileInfo.setWork(workRepo.findOne(workId));

        fileInfo = fileInfoRepo.save(fileInfo);

        return fileInfo.getId();
    }

    @Transactional
    private void updateWork(final String appName, final Status status, final long workId) {

        Work work = workRepo.findOne(workId);

        work.setOriginalFile(appName);

        work.setStatus(status);

        workRepo.save(work);

        logger.debug( "Leaving updateWork with {}", work.toString());
    }

    @Transactional
    private int loadSourceSegmentsToDatabase(final HashMap<String, String> segments, final long workId){

        List<Unit> units = new ArrayList<Unit>();
        int serialNum = 0;

        for (Map.Entry<String, String> entry : segments.entrySet()) {
            serialNum++;

            Source s = new Source();
            s.setText(entry.getValue());

            Target t = new Target();
            t.setText("");
            t.setSkeletonTag("TARGET_TAG_" + serialNum);
            t.setState(State.NEEDS_TRANSLATION);

            Unit unit = new Unit();
            unit.setSerialNumber(serialNum);
            unit.setSegmentKey(entry.getKey());
            unit.setSource(s);
            unit.setTarget(t);

            units.add(unit);            
        }

        Work work = workRepo.findOne(workId);
        units.stream().forEach(unit -> unit.setWork(work));

        List<Unit> savedUnits =  (List<Unit>) unitRepo.save(units);        
        return savedUnits.size();
    }

    private void checkServiceAvailability(final long workId) {
        if (!(fileValidatorCache.getService(getFormat(workId)).isPresent())) {
            logger.error("Language file validator for format {} was missing", getFormat(workId));
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }      

        if (!(fileReaderCache.getService(getFormat(workId)).isPresent())) {
            logger.error("Language file reader for format {} was missing", getFormat(workId));
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }

        if (!(fileWriterCache.getService(getFormat(workId)).isPresent())) {
            logger.error("Language file writer for format {} was missing", getFormat(workId));
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.CANNOT_UPLOAD_FILE); // or something
        }
    }

    @Override
    public void removeUploadedSource(final long workId) {
        
        if (fileInfoRepo.findByWorkId(workId).isPresent()){
            FileInfo fileInfo = fileInfoRepo.findByWorkId(workId).get();
            Path fileToDeletePath = Paths.get(fileInfo.getBackup_file());
            filelocator.deleteFileFromPermanentFileSystem(fileToDeletePath);
            fileInfoRepo.delete(fileInfo);
        }
        
        // else branch does not exist because source file may have not been uploaded yet
                       
    }
}
