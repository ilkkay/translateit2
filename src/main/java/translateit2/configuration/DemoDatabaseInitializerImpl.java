package translateit2.configuration;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dto.PersonDto;
import translateit2.persistence.dto.ProjectDto;
import translateit2.persistence.dto.TranslatorGroupDto;
import translateit2.persistence.dto.WorkDto;
import translateit2.persistence.model.Priority;
import translateit2.service.ProjectService;
import translateit2.service.WorkService;

@Component
public class DemoDatabaseInitializerImpl implements DatabaseInitializer {

    @Autowired
    ProjectService projectService;
    
    @Autowired
    WorkService workService;

    @Override
    public void loadDemo() {

        // create Person entity; not used in demo
        PersonDto personDto = new PersonDto();
        personDto.setFullName("Ilkka");
        personDto = projectService.createPersonDto(personDto);

        // create translator group; not used in demo
        TranslatorGroupDto groupDto = new TranslatorGroupDto();
        groupDto.setName("Group name 1");
        groupDto = projectService.createGroupDto(groupDto);

        // create demo project
        ProjectDto prj = new ProjectDto();
        prj.setName("Translate IT 2");
        prj.setSourceLocale(new Locale("en_EN"));
        prj.setFormat(LanguageFileFormat.PROPERTIES);
        prj.setType(LanguageFileType.UTF_8);
        prj.setPersonId(personDto.getId());
        prj = projectService.createProjectDto(prj,"Ilkka");

        // create work for demo project
        WorkDto work = new WorkDto();
        work.setProjectId(prj.getId());
        work.setGroupId(666L);
        work.setLocale(new Locale("fi_FI"));
        work.setVersion("0.07");
        // work.setOriginalFile("dotcms");
        work.setSkeletonFile("skeleton file");
        // work.setStatus(Status.NEW); not yet!!
        work.setPriority(Priority.HIGH);
        work.setStarted(LocalDate.now());
        // LocalDate finishedDate = LocalDate.parse("2017-05-22");
        // work.setFinished(finishedDate); not yet!!
        LocalDate currentDate = LocalDate.now();
        LocalDate deadLine = currentDate.plusMonths(2L);
        deadLine = LocalDate.parse("2017-05-22");
        work.setDeadLine(deadLine);
        work.setProgress(0);
        work = workService.createWorkDto(work,"Group name 1");
    }

}
