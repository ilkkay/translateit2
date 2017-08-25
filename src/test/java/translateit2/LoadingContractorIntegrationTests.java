package translateit2;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import translateit2.exception.TranslateIt2Exception;
import translateit2.fileloader.FileLoader;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dao.FileInfoRepository;
import translateit2.persistence.dao.UnitRepository;
import translateit2.persistence.dto.PersonDto;
import translateit2.persistence.dto.ProjectDto;
import translateit2.persistence.dto.TranslatorGroupDto;
import translateit2.persistence.dto.WorkDto;
import translateit2.persistence.model.FileInfo;
import translateit2.persistence.model.Priority;
import translateit2.persistence.model.State;
import translateit2.persistence.model.Status;
import translateit2.persistence.model.Unit;
import translateit2.service.LoadingContractor;
import translateit2.service.ProjectService;
import translateit2.service.WorkService;;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
public class LoadingContractorIntegrationTests {
    static final Logger logger = LogManager.getLogger(ProjectServiceIntegrationTestOld.class.getName());

    private long testPersonId;

    private long testGroupId;

    @Autowired
    private FileInfoRepository fileInfoRepo;

    @Autowired
    private FileLoader fileloader;

    @Autowired
    private UnitRepository unitRepo;

    @Autowired
    private LoadingContractor loadingContractor;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private WorkService workService;

    @Before
    public void setup() {
        PersonDto personDto = new PersonDto();
        personDto.setFullName("James Bond");
        personDto = projectService.createPersonDto(personDto);
        testPersonId = personDto.getId();

        TranslatorGroupDto groupDto = new TranslatorGroupDto();
        groupDto.setName("Group name 2");
        groupDto = projectService.createGroupDto(groupDto);
        testGroupId = groupDto.getId();

        ProjectDto prj = new ProjectDto();
        prj.setName("Translate IT 22");
        prj.setSourceLocale(new Locale("en_EN"));
        prj.setFormat(LanguageFileFormat.PROPERTIES);
        prj.setType(LanguageFileType.UTF_8);
        prj = projectService.createProjectDto(prj,"James Bond");

        WorkDto work = new WorkDto();
        work.setProjectId(prj.getId());
        work.setLocale(new Locale("fi_FI"));
        work.setVersion("0.071");
        work.setOriginalFile("dotcms");
        work.setSkeletonFile("skeleton file");
        work.setStatus(Status.NEW);
        work.setPriority(Priority.HIGH);
        work.setStarted(LocalDate.now());
        work.setDeadLine(LocalDate.parse("2017-10-10"));
        work.setProgress(66);
        work = workService.createWorkDto(work,"Group name 2");        
    }

    @After
    public void reset() {
        // remove all for a person
        List<ProjectDto> personPrjs = projectService.getProjectDtos(testPersonId);
        projectService.removeProjectDtos(personPrjs);

        // remove person
        projectService.removePersonDto(testPersonId);

        // remove group
        projectService.removeGroupDto(testGroupId);
    }

    @Test
    public void uploadSourceFile_assertFileInfoHasOriginalFilename_and_BackupDirectory() throws IOException {
        // GIVEN project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> works = workService.getProjectWorkDtos(prj.getId());
        long workId=works.get(0).getId(); //1;

        // GIVEN a multipart file
        File file = new File("d:\\dotcms_en-utf8.properties");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multiPartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));

        // WHEN load it to permanent system and to database
        try {
            loadingContractor.uploadSource(multiPartFile, workId);
        }
        catch (Exception ex) {
            fail("Unexcepted exception");
        }

        // THEN assert file info containing  backup directory
        FileInfo info = fileInfoRepo.findByWorkId(workId).get();
        // directory name is same as current date
        String dateStr = Paths.get(info.getBackup_file()).getParent().getFileName().toString();
        String dateNowStr = LocalDate.now().toString();
        assertThat(dateStr, equalTo(dateNowStr));      

        // and assert file info containing original filename 
        assertThat("dotcms_en-utf8.properties",equalTo(info.getOriginal_file()));

        // remove file from disk and units from database
        FileSystemUtils.deleteRecursively(Paths.get(info.getBackup_file()).getParent().toFile());
        fileInfoRepo.delete(info);

        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        unitRepo.delete(units);        
    }

    @Test
    public void reloadingSourceFile_assertCannotUploadException() throws IOException {
        // GIVEN project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> works = workService.getProjectWorkDtos(prj.getId());
        long workId=works.get(0).getId(); //1;

        // GIVEN a multipart file 
        File file = new File("d:\\dotcms_en-utf8.properties");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multiPartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));

        // GIVEN we have loaded the file
        try {
            loadingContractor.uploadSource(multiPartFile, workId);
        }
        catch (Exception ex) {
            fail("Unexcepted exception");
        }

        // WHEN reload to the same work ID
        assertThatCode(() -> { loadingContractor.uploadSource(multiPartFile, workId); } )
        // THEN assert TranslateIt2 exception
        .isExactlyInstanceOf(TranslateIt2Exception.class);

        // remove file from disk, units and file info from database
        FileInfo info = fileInfoRepo.findByWorkId(workId).get();
        fileInfoRepo.delete(info);
        FileSystemUtils.deleteRecursively(Paths.get(info.getBackup_file()).getParent().toFile());

        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        unitRepo.delete(units);
    }

    @Test
    public void uploadTargetFile_assertUnitTargetTextLength_and_TranslatedCount() throws IOException {
        // GIVEN project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> works = workService.getProjectWorkDtos(prj.getId());
        long workId=works.get(0).getId(); //1;

        // GIVEN an uploaded source file
        try {
            File fileSource = new File("d:\\dotcms_en-utf8.properties");
            FileInputStream input1 = new FileInputStream(fileSource);
            MultipartFile multiPartFile = new MockMultipartFile("file1",
                    fileSource.getName(), "text/plain", IOUtils.toByteArray(input1));
            loadingContractor.uploadSource(multiPartFile, workId);
        }
        catch (Exception ex) {
            fail("Unexcepted exception");
        }

        // WHEN load the target file
        try {
            File fileTarget = new File("d:\\dotcms_fi-utf8.properties");
            FileInputStream input2 = new FileInputStream(fileTarget);
            MultipartFile multiPartFile = new MockMultipartFile("file2",
                    fileTarget.getName(), "text/plain", IOUtils.toByteArray(input2));
            loadingContractor.uploadTarget(multiPartFile, workId);
        }
        catch (Exception ex) {
            fail("Unexcepted exception");
        }

        // THEN assert first line of target file
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        String receivedTargetText = units.get(0).getTarget().getText();
        assert("Tiedoston tallennus voi kestää pidempään, jos sen koko on suuri.".equals(receivedTargetText));

        // assert count of translated units
        long translated = unitRepo.countByWorkIdAndTargetState(workId, State.TRANSLATED);
        long needsReview = unitRepo.countByWorkIdAndTargetState(workId, State.NEEDS_REVIEW);
        assertThat(4140L, equalTo(translated + needsReview));

        // remove file from disk and units from database
        FileInfo info = fileInfoRepo.findByWorkId(workId).get();
        fileInfoRepo.delete(info);
        FileSystemUtils.deleteRecursively(Paths.get(info.getBackup_file()).getParent().toFile());

        unitRepo.delete(units);
    }

    @Test
    public void downloadTarget_assertDownloadDirectoryName_and_AllHaveBeenTranslated() {
        // GIVEN project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> works = workService.getProjectWorkDtos(prj.getId());
        long workId=works.get(0).getId(); //1;

        // GIVEN uploaded source and target files
        try {
            File fileSource = new File("d:\\dotcms_en-utf8.properties");
            FileInputStream input1 = new FileInputStream(fileSource);
            MultipartFile multiPartFile1 = new MockMultipartFile("file1",
                    fileSource.getName(), "text/plain", IOUtils.toByteArray(input1));
            loadingContractor.uploadSource(multiPartFile1, workId);

            File fileTarget = new File("d:\\dotcms_fi-utf8.properties");
            FileInputStream input2 = new FileInputStream(fileTarget);
            MultipartFile multiPartFile2 = new MockMultipartFile("file2",
                    fileTarget.getName(), "text/plain", IOUtils.toByteArray(input2));
            loadingContractor.uploadTarget(multiPartFile2, workId);
        }
        catch (Exception ex) {
            fail("Unexcepted exception");
        }

        // WHEN download target file as stream
        List<Stream<Path>> paths = new ArrayList<Stream<Path>>();        
        assertThatCode(() -> { paths.add(loadingContractor.downloadTarget(workId)); } )
        .doesNotThrowAnyException(); 

        // THEN assert stream path count
        Stream<Path> streamPath = paths.get(0);        
        List<Path> streamPaths = streamPath.map(path -> path.toAbsolutePath()).collect(Collectors.toList());        
        assertThat(streamPaths.size(), equalTo(1));

        // and assert download directory name
        String expectedDownloadDir = fileloader.getDownloadPath("test.txt").getParent().getFileName().toString();
        String returnedDownloadDir = streamPaths.get(0).getParent().getFileName().toString();
        assertThat(expectedDownloadDir,equalTo(returnedDownloadDir));

        // remove file from disk and units from database
        FileInfo info = fileInfoRepo.findByWorkId(workId).get();
        fileInfoRepo.delete(info);
        FileSystemUtils.deleteRecursively(Paths.get(info.getBackup_file()).getParent().toFile());

        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        unitRepo.delete(units);
    }

    @Test   
    public void removeUploadedSource_assertFileInfo_and_File_Removed()  {
        // GIVEN project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> works = workService.getProjectWorkDtos(prj.getId());
        long workId=works.get(0).getId(); //1;

        // and WHEN we have loaded a multipart file
        try {
            File file = new File("d:\\dotcms_en-utf8.properties");
            FileInputStream input = new FileInputStream(file);
            MultipartFile multiPartFile = new MockMultipartFile("file",
                    file.getName(), "text/plain", IOUtils.toByteArray(input));
            loadingContractor.uploadSource(multiPartFile, workId);
        }
        catch (Exception ex) {
            fail("Unexcepted exception");
        }

        FileInfo info = fileInfoRepo.findByWorkId(workId).get();
        String backupFile = info.getBackup_file();

        // THEN we remove it
        assertThatCode(() -> { loadingContractor.removeUploadedSource(workId); } )
        .doesNotThrowAnyException(); 

        // assert that entity exists more
        assertThat(fileInfoRepo.findByWorkId(workId).isPresent(),equalTo(false));

        // and neither does file
        assertThat(Files.exists(Paths.get(backupFile)),equalTo(false));

        // remove units from database
        FileSystemUtils.deleteRecursively(Paths.get(info.getBackup_file()).getParent().toFile());

        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        unitRepo.delete(units);
    }
}
