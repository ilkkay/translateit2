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
import translateit2.restapi.ViewStatistics;
import translateit2.service.ProjectService;
import translateit2.service.WorkService;
import translateit2.util.WorkStatisticsLogic;

@ConfigurationProperties(prefix = "test.translateit2")
@TestPropertySource("test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
public class WorkStatisticsLogicIntegrationTest {
    static final Logger logger = LogManager.getLogger(WorkStatisticsLogicIntegrationTest.class.getName());

    private long testPersonId;

    private long testGroupId;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private WorkService workService;

    @Autowired
    private WorkStatisticsLogic workStatisticsLogic;

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
    public void translateUnits_assertTranslatedCount_Progress() {

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
            t.setState(State.NEW);
            unit.setSource(s);
            unit.setTarget(t);

            unitDtos.add(unit);
        }
        long workId = work.getId();
        workService.createUnitDtos(unitDtos, work.getId());

        // WHEN all the units are translated
        List<UnitDto> dtos = workService.getUnitDtos(workId);
        for (UnitDto unit : dtos) {
            unit.getTarget().setText("target text");
            UnitDto updatedUnit = workService.updateTranslatedUnitDto(unit, unit.getWorkId());        
            workService.updateProgress(updatedUnit.getWorkId());
        }

        // THEN assert their count
        ViewStatistics stats = workStatisticsLogic.getStatistics(workId);
        assertThat(50L,equalTo(stats.getTranslated()));
        
        // and assert progress
        int progress = workService.getWorkDtoById(workId).getProgress();
        assertThat(100,equalTo(progress));
        
        // WHEN set one target text to empty string
        UnitDto dto = dtos.get(0);
        dto.getTarget().setText("");
        UnitDto updatedUnit = workService.updateTranslatedUnitDto(dto, dto.getWorkId());        
        workService.updateProgress(updatedUnit.getWorkId());
        
        // THEN assert translated count to decrease by one
        stats = workStatisticsLogic.getStatistics(workId);
        assertThat(49L,equalTo(stats.getTranslated()));
        
        // and assert progress decreased
        progress = workService.getWorkDtoById(workId).getProgress();
        assertThat(98,equalTo(progress));
        
    }

}
