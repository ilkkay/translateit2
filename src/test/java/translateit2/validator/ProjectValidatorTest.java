package translateit2.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dao.ProjectRepository;
import translateit2.persistence.dto.ProjectDto;
import translateit2.persistence.model.Project;
import translateit2.util.Messages;

// JUnit 4 Rule to run individual tests with a different default locale
// https://gist.github.com/digulla/5884162
@RunWith(MockitoJUnitRunner.class)
public class ProjectValidatorTest implements ConstraintValidatorFactory {
    private static Validator validator;

    private Integer testProjectNameMinSize = 5;

    private Integer testProjectNameMaxSize = 35;

    private Messages messages;

    @Mock
    private ProjectRepository mockRepo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Configuration<?> config = Validation.byDefaultProvider().configure();
        config.constraintValidatorFactory(this);

        ValidatorFactory factory = config.buildValidatorFactory();
        validator = factory.getValidator();

        // TODO: move this part to somewhere else
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("ISO-8859-1");
        messageSource.setFallbackToSystemLocale(false);

        messages = new Messages(messageSource);
        messages.resetLocale(Locale.ENGLISH);
    }

    @Test
    public void UpdateExistingProject_assertNoViolation() {
        // GIVEN: an existing projectId l
        long receivedProjectId = 1l;
        Project prj = new Project();
        prj.setName("Translate IT 2");
        prj.setId(receivedProjectId);

        // WHEN: get project with an existing project name
        // validator will check if there is other project with same name
        when(mockRepo.findByName("Translate IT 2")).thenReturn(Optional.of(prj));

        // set new values
        long dtoProjectId = 1L;
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Translate IT 2");
        projectDto.setId(dtoProjectId);
        projectDto.setFormat(LanguageFileFormat.PROPERTIES);
        projectDto.setType(LanguageFileType.UTF_8);
        projectDto.setSourceLocale(new Locale("en_EN"));

        // and validate
        Set<ConstraintViolation<ProjectDto>> constraintViolations = 
                validator.validate(projectDto);

        // THEN: assert that violation is NOT found
        assertThat(constraintViolations.size(),equalTo(0));
    }

    @Test
    public void UpdateEntity_FailIfSourceLocale_Name_Format_And_Charset_Null() {

        // WHEN: create a new project
        ProjectDto projectDto = new ProjectDto();
        
        // validate an entity
        Set<ConstraintViolation<ProjectDto>> constraintViolations = validator.validate(projectDto);
        List <String> returnedPropertyPaths = new ArrayList<String>();
        List <String> expectedPropertyPaths = Arrays.asList("format","name","sourceLocale","charset");

        for (ConstraintViolation<ProjectDto> constraintViolation : constraintViolations)
            returnedPropertyPaths.add(constraintViolation.getPropertyPath().toString());

        Collections.sort(returnedPropertyPaths);
        Collections.sort(expectedPropertyPaths);
        assertThat(returnedPropertyPaths, 
                IsIterableContainingInOrder.contains(expectedPropertyPaths.toArray()));

    }

    @Test
    public void CreateEntity_FailIfProjectNameShort() {

        long dtoProjectId = 0L;
        ProjectDto projectDto = new ProjectDto();
        
        // WHEN update with too short project name
        when(mockRepo.findByName("Proj")).thenReturn(Optional.empty());
        projectDto.setName("Proj");
        projectDto.setId(dtoProjectId);
        projectDto.setFormat(LanguageFileFormat.PROPERTIES);
        projectDto.setType(LanguageFileType.UTF_8);
        projectDto.setSourceLocale(new Locale("en_EN"));
        Set<ConstraintViolation<ProjectDto>> constraintViolations = validator.validate(projectDto);

        // THEN assert that there is one one violation
        assertThat(constraintViolations.size(),equalTo(1));

        // AND assert it is the same we have set in the validator
        ConstraintViolation<ProjectDto> constraintViolation = constraintViolations.stream().findFirst().get();
        String messageTemplate = constraintViolation.getMessageTemplate();
        assert("ProjectDto.projectName.size".equals(messageTemplate));

    }

    @Test
    public void CreateProject_FailIfProjectNameExists() {
        // GIVEN: an existing project id 0
        long dtoProjectId = 0L;
        long receivedProjectId = 1l;
        Project prj = new Project();
        prj.setName("Translate IT 2");
        prj.setId(receivedProjectId);

        // WHEN: get it with an existing project name
        when(mockRepo.findByName("Translate IT 2")).thenReturn(Optional.of(prj));

        // validate an existing entity
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Translate IT 2");
        projectDto.setId(dtoProjectId);
        projectDto.setFormat(LanguageFileFormat.PROPERTIES);
        projectDto.setType(LanguageFileType.UTF_8);
        projectDto.setSourceLocale(new Locale("en_EN"));
        Set<ConstraintViolation<ProjectDto>> constraintViolations = validator.validate(projectDto);

        // THEN assert that there is one one violation
        assertThat(constraintViolations.size(),equalTo(1));

        // AND it is the same we have set in the validator
        ConstraintViolation<ProjectDto> constraintViolation = constraintViolations.stream().findFirst().get();
        String messageTemplate = constraintViolation.getMessageTemplate();
        assert("ProjectValidator.project_exists_already".equals(messageTemplate));
        
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        try {
            if (key == ProjectValidator.class) {
                ProjectValidator validator = new ProjectValidator(mockRepo);
                validator.setProjectNameMinSize(testProjectNameMinSize);
                validator.setProjectNameMaxSize(testProjectNameMaxSize);
                return (T)validator;
            }
            else
                return key.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("getInstance() gave InstantiationException");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("getInstance() gave IllegalAccessException");
        }
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> arg0) {

    }

    @Test
    public void test_messages() throws NoSuchFieldException, SecurityException {
        String s = null;

        s = messages.get("ProjectValidator.name_exists");
        System.out.println(s);
        ;

        s = messages.get("ProjectValidator.no_create_permission");
        System.out.println(s);
        ;

        s = messages.get("ProjectValidator.project_exists_already");
        System.out.println(s);
        ;

        s = messages.get("ProjectValidator.entity_missing");
        System.out.println(s);
        ;

        s = messages.get("ProjectValidator.name_exists");
        System.out.println(s);
        ;

        s = messages.get("ProjectValidator.test_name");
        System.out.println(s);
        ;

        String[] args = { "Translate IT 2", "5", "35" };
        s = messages.get("ProjectDto.projectName.size", args);
        System.out.println(s);
        ;

        s = messages.get("Source.segment_not_null");
        System.out.println(s);
        ;
        s = messages.get("Target.segment_not_empty");
        System.out.println(s);
        String[] args2 = { "5", "666" };
        s = messages.get("Unit.segment_size", args2);
        System.out.println(s);

        s = messages.get("javax.validation.constraints.NotNull.message");
        System.out.println(s);

        s = messages.get("org.hibernate.validator.constraints.NotBlank.message");
        System.out.println(s);

        s = messages.get("org.hibernate.validator.constraints.NotEmpty.message");
        System.out.println(s);

    }

}