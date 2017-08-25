package translateit2;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import translateit2.persistence.model.Source;
import translateit2.persistence.model.State;
import translateit2.persistence.model.Status;
import translateit2.persistence.model.Target;
import translateit2.service.ProjectService;
import translateit2.service.WorkService;

@ConfigurationProperties(prefix = "test.translateit2")
@TestPropertySource("test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
public class UnitServiceIntegrationTest {
    static final Logger logger = LogManager.getLogger(UnitServiceIntegrationTest.class.getName());

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
    public void createUnit_assertAllFields() {
        // GIVEN project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> works = workService.getProjectWorkDtos(prj.getId());
        WorkDto work = works.get(0);

        // and GIVEN an unit
        final UnitDto unit = new UnitDto();
        unit.setSegmentKey("segmentKey");
        unit.setSerialNumber(666);
        final Source s = new Source();
        s.setText("source text");
        s.setPlural("source texts");
        final Target t = new Target();
        t.setText("target text");
        t.setState(State.NEW);
        unit.setSource(s);
        unit.setTarget(t);

        List<UnitDto> unitDtos = new ArrayList<UnitDto>();
        unitDtos.add(unit);

        // WHEN create Unit
        workService.createUnitDtos(unitDtos, work.getId());

        // THEN assert unit count
        long unitCount = workService.getUnitDtoCount(work.getId());
        assertThat(1L, is(equalTo(unitCount)));

        // and assert values
        List<UnitDto> returnedUnitDtos = workService.getUnitDtos(work.getId());
        assertThat(1, equalTo(returnedUnitDtos.size()));
        UnitDto returnedUnitDto = returnedUnitDtos.get(0);
        assertThat("segmentKey",equalTo(unit.getSegmentKey()));
        assertThat(666,equalTo(unit.getSerialNumber()));

        Source returnedSource = returnedUnitDto.getSource();
        assertThat("source text",equalTo(returnedSource.getText()));
        assertThat("source texts",equalTo(returnedSource.getPlural()));

        Target returnedTarget = returnedUnitDto.getTarget();
        assertThat("target text",equalTo(returnedTarget.getText()));
        assertThat(State.NEW,equalTo(returnedTarget.getState()));

    }


    @Test
    public void CreateEmptyUnit_assertViolation() {

        // GIVEN a project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> works = workService.getProjectWorkDtos(prj.getId());
        WorkDto work = works.get(0);

        // and GIVEN an empty unit
        final UnitDto unit = new UnitDto();
        final Source s = new Source();
        final Target t = new Target();
        unit.setSource(s);
        unit.setTarget(t);

        List<UnitDto> unitDtos = new ArrayList<UnitDto>();
        unitDtos.add(unit);

        // WHEN create it
        try {
            workService.createUnitDtos(unitDtos, work.getId());
            fail ("No ConstraintViolation exceptions thrown ");
        } catch (ConstraintViolationException e) {

            // ASSERT violation exceptions
            List <String> returnedFields = getViolatedFields(e);
            List <String> expectedFields = Arrays.asList("serialNumber", "segment_key");
            Collections.sort(expectedFields);
            Collections.sort(returnedFields);

            assertThat(returnedFields, 
                    IsIterableContainingInOrder.contains(expectedFields.toArray()));
        }
    }


    @Test
    public void updatateUnit_assertAllFields() {
        // GIVEN a project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> wrks = workService.getProjectWorkDtos(prj.getId());
        WorkDto work = wrks.get(0);

        // and GIVEN an existing unit
        final UnitDto unit = new UnitDto();
        unit.setSegmentKey("segmentKey");
        unit.setSerialNumber(666);
        final Source s = new Source();
        s.setText("source text");
        s.setPlural("source texts");
        final Target t = new Target();
        t.setText("target text");
        unit.setSource(s);
        unit.setTarget(t);
        List<UnitDto> unitDtos = new ArrayList<UnitDto>();
        unitDtos.add(unit);

        workService.createUnitDtos(unitDtos, work.getId());

        // WHEN update unit to new values
        List<UnitDto> newUnitDtos = workService.getUnitDtos(work.getId());
        newUnitDtos.forEach(dto -> dto.setSegmentKey("new " + dto.getSegmentKey()));
        newUnitDtos.forEach(dto -> dto.getSource().setText("new " + dto.getSource().getText()));
        newUnitDtos.forEach(dto -> {
            dto.getTarget().setText("new " + dto.getTarget().getText());   
            dto.getTarget().setState(State.TRANSLATED);   
        });
        
        workService.updateUnitDtos(newUnitDtos, work.getId());

        // THEN assert unit count
        List<UnitDto> returnedUnitDtos = workService.getUnitDtos(work.getId());
        assertThat(1, equalTo(returnedUnitDtos.size()));

        // and assert updated key and texts
        UnitDto returnedUnitDto = newUnitDtos.get(0);
        assertThat("new segmentKey",equalTo(returnedUnitDto.getSegmentKey()));
        assertThat(666,equalTo(returnedUnitDto.getSerialNumber()));
        assertThat("new source text",equalTo(returnedUnitDto.getSource().getText())); 
        assertThat("new target text",equalTo(returnedUnitDto.getTarget().getText()));  
        assertThat(State.TRANSLATED,equalTo(returnedUnitDto.getTarget().getState()));
    }

    @Test
    public void loadUnits_assertPagePagination() {

        // GIVEN a project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> wrks = workService.getProjectWorkDtos(prj.getId());
        WorkDto work = wrks.get(0);

        // and GIVEN a number of units
        List<UnitDto> unitDtos = new ArrayList<UnitDto>();
        for (int i = 1; i <= 50; i++ ) {
            final UnitDto unit = new UnitDto();
            unit.setSegmentKey("segmentKey");
            unit.setSerialNumber(i);
            final Source s = new Source();
            s.setText("source text");
            final Target t = new Target();
            unit.setSource(s);
            unit.setTarget(t);

            unitDtos.add(unit);
        }

        // WHEN create Unit
        workService.createUnitDtos(unitDtos, work.getId());

        // THEN assert serial number of every 10th unit
        int pageSize = 10;
        for (int pageNumber = 1; pageNumber < 5; pageNumber++ ) {
            List<UnitDto> unitPage = workService.getPage(work.getId(), pageNumber, pageSize);
            assertThat(pageSize, is(equalTo(unitPage.size())));

            final UnitDto unit = unitPage.get(0);
            assertThat((pageNumber - 1) * pageSize + 1, equalTo(unit.getSerialNumber()));
        }


        workService.removeUnitDtos(work.getId());
    }

    @Test
    public void removeWorkWithUnits_assertException_Units() {

        // GIVEN a project and a work
        ProjectDto prj = projectService.getProjectDtoByProjectName("Translate IT 22");
        List<WorkDto> wrks = workService.getProjectWorkDtos(prj.getId());
        WorkDto work = wrks.get(0);

        // and GIVEN a number of units
        List<UnitDto> unitDtos = new ArrayList<UnitDto>();
        for (int i = 1; i <= 50; i++ ) {
            final UnitDto unit = new UnitDto();
            unit.setSegmentKey("segmentKey");
            unit.setSerialNumber(i);
            final Source s = new Source();
            s.setText("source text");
            final Target t = new Target();
            unit.setSource(s);
            unit.setTarget(t);

            unitDtos.add(unit);
        }
        long workId = work.getId();
        workService.createUnitDtos(unitDtos, work.getId());

        List<Long> ids = new ArrayList<>();
        workService.getUnitDtos(workId).stream().forEach(u -> ids.add((u.getId())));

        // WHEN remove work
        workService.removeWorkDto(workId);
        
        // THEN assert exception is thrown for every unit removed
        for (long unitId : ids) {
            assertThatCode(() -> workService.getUnitDtoById(unitId))
            .isExactlyInstanceOf(TranslateIt2Exception.class); 
        }

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
