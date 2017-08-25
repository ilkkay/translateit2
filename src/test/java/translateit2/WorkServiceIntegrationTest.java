package translateit2;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import translateit2.exception.TranslateIt2Exception;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dto.PersonDto;
import translateit2.persistence.dto.ProjectDto;
import translateit2.persistence.dto.TranslatorGroupDto;
import translateit2.persistence.dto.UnitDto;
import translateit2.persistence.dto.WorkDto;
import translateit2.persistence.model.Priority;
import translateit2.persistence.model.Project;
import translateit2.persistence.model.Source;
import translateit2.persistence.model.Status;
import translateit2.persistence.model.Target;
import translateit2.service.ProjectService;
import translateit2.service.WorkService;
import translateit2.util.Messages;

@ConfigurationProperties(prefix = "test.translateit2")
@TestPropertySource("test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
public class WorkServiceIntegrationTest {
    static final Logger logger = LogManager.getLogger(WorkServiceIntegrationTest.class.getName());

    private long testPersonId;

    private long testGroupId;

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
        prj.setSourceLocale(new Locale("fi_FI"));
        prj.setFormat(LanguageFileFormat.PROPERTIES);
        prj.setType(LanguageFileType.UTF_8);
        prj = projectService.createProjectDto(prj,"James Bond");
    }

    @After
    public void reset() {
        // remove all for a person
        List<ProjectDto> personPrjs = projectService.getProjectDtos(testPersonId);
        projectService.removeProjectDtos(personPrjs);

        projectService.removePersonDto(testPersonId);

        projectService.removeGroupDto(testGroupId);
    }

    @Test
    public void createWork_assertFields_and_WorkCount() {

        // GIVEN a project
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");

        // WHEN create a work
        WorkDto work = new WorkDto();
        work.setProjectId(prj.getId());
        work.setLocale(new Locale("en_EN"));
        work.setVersion("0.071");
        work.setOriginalFile("dotcms");
        work.setSkeletonFile("skeleton file");
        work.setStatus(Status.NEW);
        work.setPriority(Priority.HIGH);
        work.setStarted(LocalDate.now());
        work.setDeadLine(LocalDate.parse("2017-10-10"));
        work.setProgress(66);

        work = workService.createWorkDto(work,"Group name 2");
        
        // THEN assert that all the fields are what we've set
        WorkDto wrk1 = workService.getWorkDtoById(work.getId());
        assertEquals("en_EN",wrk1.getLocale().toString());
        assertEquals("0.071",wrk1.getVersion());
        assertEquals("dotcms",wrk1.getOriginalFile());
        assertEquals("skeleton file",wrk1.getSkeletonFile());
        assertEquals(Status.NEW.toString(),wrk1.getStatus().toString());
        assertEquals(Priority.HIGH,wrk1.getPriority());
        assertEquals(LocalDate.now(),wrk1.getStarted());
        assertEquals(LocalDate.parse("2017-10-10"),wrk1.getDeadLine());
        assertThat(66,equalTo(wrk1.getProgress()));

        // assert that work count is one
        assertThat(1L, equalTo(workService.getWorkDtoCount(testGroupId)));
        List<WorkDto> works = workService.getWorkDtos(testGroupId);
        assertThat(1, equalTo(works.size()));

        // assert that the work count within Translate IT 22 project is one
        Map<Long, Integer> workMap = projectService.getWorkCountPerProject("James Bond");
        List <ProjectDto> dtos = projectService.getProjectDtos(testPersonId);
        assertThat(workMap.get(dtos.get(0).getId()), equalTo(1));

    }

    @Test
    public void Get_RemovedWork_assertTranslateIt2Exception() {
        // GIVEN a project and work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");

        WorkDto work = new WorkDto();
        work.setProjectId(prj.getId());
        work.setLocale(new Locale("en_EN"));
        work.setVersion("0.071");
        work.setOriginalFile("dotcms");
        work.setSkeletonFile("skeleton file");
        work.setStatus(Status.NEW);
        work.setPriority(Priority.HIGH);
        work.setStarted(LocalDate.now());
        work.setDeadLine(LocalDate.parse("2017-10-10"));
        work.setProgress(66);

        work = workService.createWorkDto(work,"Group name 2");
        
        // WHEN remove work   
        long workId = work.getId();
        workService.removeWorkDto(workId);

        // THEN assert Exception
        assertThatCode(() -> workService.getWorkDtoById(workId))
        .isExactlyInstanceOf(TranslateIt2Exception.class);        
    }
    
    @Test
    public void CreateEmptyWork_assertViolation_Name_SourceLocale_Format_and_Type() {
        // GIVEN a project
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");

        // WHEN create work with null values
        try {
            WorkDto work = new WorkDto();
            work.setProjectId(prj.getId());
            work = workService.createWorkDto(work,"Group name 2");

            fail("No Constraint Violation Exception thrown");
        } catch (ConstraintViolationException e) {

            // THEN assert fields with violations
            List <String> returnedFields = getViolatedFields(e);
            List <String> expectedFields = Arrays.asList("priority", "locale", "version", "deadLine");

            Collections.sort(returnedFields);
            Collections.sort(expectedFields);
            assertThat(returnedFields, 
                    IsIterableContainingInOrder.contains(expectedFields.toArray()));
        }
    }

    @Test
    public void AddWorkWithExistingVersionNumber_assertViolation() {
        // GIVEN a project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        
        WorkDto work = new WorkDto();
        work.setProjectId(prj.getId());
        work.setLocale(new Locale("en_EN"));
        work.setVersion("0.071");
        work.setPriority(Priority.HIGH);
        work.setDeadLine(LocalDate.parse("2017-10-10"));
        work = workService.createWorkDto(work,"Group name 2");
        
        try {
            // WHEN create a new work with the same version number
            WorkDto newWork = new WorkDto();
            newWork.setProjectId(prj.getId());
            newWork.setLocale(new Locale("fi_FI"));
            newWork.setVersion("0.071");
            newWork.setPriority(Priority.LOW);
            newWork.setDeadLine(LocalDate.parse("2017-12-12"));
            newWork = workService.createWorkDto(newWork,"Group name 2");
            fail("No Constraint Violation Exception thrown");
        } catch (ConstraintViolationException e) {
            
            // THEN assert violation
            ConstraintViolation<?> constraintViolation = 
                    e.getConstraintViolations().stream().findFirst().get();
            String messageTemplate = constraintViolation.getMessageTemplate();
            assert("WorkDto.work_version_exists_already".equals(messageTemplate));                        
        }
    }
    
    @Test
    public void upateWork_assertFields_and_WorkCount() {        
        // GIVEN a project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");

        WorkDto work = new WorkDto();
        work.setProjectId(prj.getId());
        work.setLocale(new Locale("en_EN"));
        work.setVersion("0.071");
        work.setOriginalFile("dotcms");
        work.setSkeletonFile("skeleton file");
        work.setStatus(Status.NEW);
        work.setPriority(Priority.HIGH);
        work.setStarted(LocalDate.now());
        work.setDeadLine(LocalDate.parse("2017-10-10"));
        work.setProgress(66);
        work = workService.createWorkDto(work,"Group name 2");
        long allWorksCount = workService.getWorkDtoCount(testGroupId);

        // WHEN update all the fields are what we've set 
        try {
            WorkDto wrk = workService.getWorkDtoById(work.getId());
            wrk.setLocale(new Locale("fi_FI"));
            wrk.setVersion("0.0711");
            wrk.setOriginalFile("dotcms2");
            wrk.setSkeletonFile("skeleton file2");
            wrk.setStatus(Status.OPEN);
            wrk.setPriority(Priority.LOW);
            wrk.setStarted(LocalDate.parse("2017-10-10"));
            wrk.setDeadLine(LocalDate.parse("2017-11-11"));
            wrk.setProgress(99);
            wrk = workService.updateWorkDto(wrk);
        } catch (ConstraintViolationException e) {                        
            fail("Constraint Violation Exception was thrown");
        }
        
        // THEN retrieve it and assert all the fields
        WorkDto wrk1 = workService.getWorkDtoById(work.getId());

        assertEquals("fi_FI",wrk1.getLocale().toString());
        assertEquals("0.0711",wrk1.getVersion());
        assertEquals("dotcms2",wrk1.getOriginalFile());
        assertEquals("skeleton file2",wrk1.getSkeletonFile());
        assertEquals(Status.OPEN.toString(),wrk1.getStatus().toString());
        assertEquals(Priority.LOW,wrk1.getPriority());
        assertEquals(LocalDate.parse("2017-10-10"),wrk1.getStarted());
        assertEquals(LocalDate.parse("2017-11-11"),wrk1.getDeadLine());
        assertThat(99,equalTo(wrk1.getProgress()));

        // and assert work count has remained the same
        assertThat(allWorksCount, equalTo(workService.getWorkDtoCount(testGroupId)));
    }

    private List <String> getViolatedFields(ConstraintViolationException e) {
        List <Path> propertyPaths = new ArrayList<Path>();

        e.getConstraintViolations().stream()
        .forEach(v ->propertyPaths.add(v.getPropertyPath()));

        List <String> fields = new ArrayList<String>();
        for(Path p : propertyPaths) {
            Iterator<Path.Node> nodeIterator = p.iterator();
            String lastNode = "";
            while (nodeIterator.hasNext()) {
                Path.Node node = nodeIterator.next();
                lastNode = node.toString();
            }
            fields.add(lastNode);
        }

        return fields;
    }

}
