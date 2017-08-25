package translateit2.service;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
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
import translateit2.persistence.model.FileInfo;
import translateit2.persistence.model.Project;
import translateit2.persistence.model.Source;
import translateit2.persistence.model.State;
import translateit2.persistence.model.Status;
import translateit2.persistence.model.Target;
import translateit2.persistence.model.Unit;
import translateit2.persistence.model.Work;

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
    public Stream <Path> downloadTarget(long workId) {
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
    public void uploadTarget(MultipartFile multipartFile, long workId) {
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
    public void uploadSource(MultipartFile multipartFile, long workId) {        
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

        // commit that file processing has finished  
    }

    @Transactional
    private void loadTargetSegmentsToDatabase(HashMap<String, String> segments, final long workId) {
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
    private boolean loadSourceSegmentsToDatabase(String originalFileName, String appName, Locale appLocale,
            Path uploadedFilePath, HashMap<String, String> segments, long workId) {


        // upload to database 
        loadSourceSegmentsToDatabase( segments, workId);

        // update file info
        long fileInfoId = updateFileInfo(uploadedFilePath,originalFileName, workId);

        // once you've loaded source file, the work status will be NEw
        updateWork(appName, Status.NEW, workId);    

        return true;
    }

    @Transactional
    private void removeUnitDtos(final long workId) {        
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        unitRepo.delete(units);        
    }

    @Transactional
    private Locale getExpectedSourceLocale(long workId) {
        Project project = workRepo.findOne(workId).getProject();
        return workRepo.findOne(workId).getProject().getSourceLocale();
    }

    @Transactional
    private Locale getExpectedTargetLocale(long workId) {
        Work work = workRepo.findOne(workId);
        return workRepo.findOne(workId).getLocale();
    }

    @Transactional
    private String getExpectedApplicationName(long workId) {
        Work work = workRepo.findOne(workId);
        return  work.getOriginalFile();
    }

    @Transactional
    private LanguageFileType getExpectedType(long workId) {
        Work work = workRepo.findOne(workId);
        return  projectRepo.findOne(work.getProject().getId()).getType();
    }

    @Transactional
    private Charset getCharSet(long workId) {
        return charsetResolver.getProjectCharSet(
                workRepo.findOne(workId).getProject().getId());
    }
    
    @Transactional
    private Map<String, String> getSegmentsMap(long workId) {
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        Map<String, String> map = new HashMap<String, String>();
        units.stream().forEach(dto -> map.put(dto.getSegmentKey(), dto.getTarget().getText()));

        return map;
    }

    @Transactional
    private LanguageFileFormat getFormat(long workId) {
        Work work = workRepo.findOne(workId);
        return projectRepo.findOne(work.getProject().getId()).getFormat();        
    }

    @Transactional
    private void updateStatus(Status newStatus, long workId) {
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
    private long updateFileInfo(Path uploadedFilePath, String OriginalFile, long workId) {
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
    private void updateWork(String appName, Status status, long workId) {

        Work work = workRepo.findOne(workId);

        work.setOriginalFile(appName);

        work.setStatus(status);

        workRepo.save(work);

        logger.debug( "Leaving updateWork with {}", work.toString());
    }

    @Transactional
    private int loadSourceSegmentsToDatabase( HashMap<String, String> segments, long workId){

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

    private void checkServiceAvailability(long workId) {
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
    public void removeUploadedSource(long workId) {
        
        if (fileInfoRepo.findByWorkId(workId).isPresent()){
            FileInfo fileInfo = fileInfoRepo.findByWorkId(workId).get();
            Path fileToDeletePath = Paths.get(fileInfo.getBackup_file());
            filelocator.deleteFileFromPermanentFileSystem(fileToDeletePath);
            fileInfoRepo.delete(fileInfo);
        }
        
        // else branch does not exist because source file may have not been uploaded yet
                       
    }
}
