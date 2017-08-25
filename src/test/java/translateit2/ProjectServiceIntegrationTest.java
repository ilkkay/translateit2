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
public class ProjectServiceIntegrationTest {
    static final Logger logger = LogManager.getLogger(ProjectServiceIntegrationTest.class.getName());

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
    public void AddProject_assertAllFields() {
        // GIVEN a new project
        ProjectDto prj = new ProjectDto();
        prj.setName("Translate IT 333");
        prj.setSourceLocale(new Locale("fi_FI"));
        prj.setFormat(LanguageFileFormat.PROPERTIES);
        prj.setType(LanguageFileType.UTF_8);

        // WHEN create it
        try {
            prj = projectService.createProjectDto(prj,"James Bond");
        } catch (ConstraintViolationException e) { 
            fail("A Constraint Violation Exception was thrown");            
        }

        // THEN assert properties
        assertThat("Translate IT 333",equalTo(prj.getName()));
        assertThat("fi_FI".toLowerCase(),equalTo(prj.getSourceLocale().toString()));
        assertThat(LanguageFileFormat.PROPERTIES,equalTo(prj.getFormat()));
        assertThat(LanguageFileType.UTF_8,equalTo(prj.getType()));

        projectService.removeProjectDto(prj.getId());
    }

    @Test
    public void AddProjectHavingShortProjectName_assertViolation__ProjectNameSize() {
        // GIVEN a new project
        ProjectDto prj = new ProjectDto();
        prj.setSourceLocale(new Locale("fi_FI"));
        prj.setFormat(LanguageFileFormat.PROPERTIES);
        prj.setType(LanguageFileType.UTF_8);

        // WHEN create project with short name
        try {
            prj.setName("Tr.");
            prj = projectService.createProjectDto(prj,"James Bond");
            projectService.removeProjectDto(prj.getId());
            fail("No Constraint Violation Exception thrown");
        } catch (ConstraintViolationException e) {

            // THEN assert size violation
            ConstraintViolation<?> constraintViolation =  e.getConstraintViolations().stream().findFirst().get();
            String messageTemplate = constraintViolation.getMessageTemplate();
            assert("ProjectDto.projectName.size".equals(messageTemplate));                        
        }
    }

    @Test
    public void AddProjectLongProjectName_assertViolation_ProjectNameSize() {
        // GIVEN a new project
        ProjectDto prj = new ProjectDto();
        prj.setSourceLocale(new Locale("fi_FI"));
        prj.setFormat(LanguageFileFormat.PROPERTIES);
        prj.setType(LanguageFileType.UTF_8);

        // WHEN create project with short name
        try {
            prj.setName("Tr..........................................................");
            prj = projectService.createProjectDto(prj,"James Bond");
            projectService.removeProjectDto(prj.getId());
            fail("No Constraint Violation Exception thrown");
        } catch (ConstraintViolationException e) {          

            // THEN assert size violation
            ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().get();
            String messageTemplate = constraintViolation.getMessageTemplate();
            assert("ProjectDto.projectName.size".equals(messageTemplate));                        
        }
    }

    @Test
    public void AddEmptyProject_assertViolation_Name_SourceLocale_Format_and_Type() {

        // WHEN create a new project with null values
        try {
            ProjectDto prj = new ProjectDto();
            prj = projectService.createProjectDto(prj,"James Bond");
            projectService.removeProjectDto(prj.getId());
            fail("No Constraint Violation Exception thrown");
        } catch (ConstraintViolationException e) {

            // THEN assert violations for null properties
            List <String> expectedFields = Arrays.asList("format","name","sourceLocale","charset");
            List <String> returnedFields = getViolatedFields( e);

            Collections.sort(returnedFields);
            Collections.sort(expectedFields);
            assertThat(returnedFields, 
                    IsIterableContainingInOrder.contains(expectedFields.toArray()));
        }

    }

    @Test
    public void RemoveProject_assertTranslateIt2Exception() {
        // GIVEN an created project
        ProjectDto prj = new ProjectDto();
        prj.setName("Translate IT 333");
        prj.setSourceLocale(new Locale("fi_FI"));
        prj.setFormat(LanguageFileFormat.PROPERTIES);
        prj.setType(LanguageFileType.UTF_8);
        prj = projectService.createProjectDto(prj,"James Bond");      

        // WHEN remove it
        projectService.removeProjectDto(prj.getId());

        // THEN assert exception if reread
        assertThatCode(() -> projectService.getProjectDtoByProjectName("Translate IT 333"))
        .isExactlyInstanceOf(TranslateIt2Exception.class);        

    }

    @Test
    public void RemoveProjectHavingWorks_assertWorkCount() {
        // GIVEN a project with a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        WorkDto work = new WorkDto();
        work.setProjectId(prj.getId());
        work.setLocale(new Locale("en_EN"));
        work.setVersion("0.073");
        work.setPriority(Priority.HIGH);
        LocalDate currentDate = LocalDate.now();
        LocalDate deadLine = currentDate.plusMonths(2L);
        deadLine = deadLine.plusDays(5L);
        work.setDeadLine(deadLine);
        work = workService.createWorkDto(work,"Group name 2");   
        long workId = work.getId();

        // WHEN remove the project
        projectService.removeProjectDto(prj.getId());

        // THEN assert that no works left for the that projectId
        assertThat(0, equalTo(workService.getProjectWorkDtos(prj.getId()).size()));

        // and assert exception if work reread
        assertThatCode(() -> workService.getWorkDtoById(workId) )
        .isExactlyInstanceOf(TranslateIt2Exception.class);  
    }

    @Test
    public void UpdateProject_assertAllFields() {
        // GIVEN a existing project
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");

        // WHEN update it
        prj.setName("Translate IT 333");
        prj.setSourceLocale(new Locale("en_EN"));
        prj.setFormat(LanguageFileFormat.XLIFF);
        prj.setType(LanguageFileType.ISO8859_1);
        try {
            prj = projectService.updateProjectDto(prj);
        } catch (ConstraintViolationException e) { 
            fail("A Constraint Violation Exception was thrown");            
        }

        // THEN assert new property values
        assertThat("Translate IT 333",equalTo(prj.getName()));
        assertThat("en_EN".toLowerCase(),equalTo(prj.getSourceLocale().toString()));
        assertThat(LanguageFileFormat.XLIFF,equalTo(prj.getFormat()));
        assertThat(LanguageFileType.ISO8859_1,equalTo(prj.getType()));

        projectService.removeProjectDto(prj.getId());

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
