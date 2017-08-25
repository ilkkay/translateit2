package translateit2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.persistence.dao.FileInfoRepository;
import translateit2.persistence.dao.InfoRepository;
import translateit2.persistence.dao.PersonRepository;
import translateit2.persistence.dao.ProjectRepository;
import translateit2.persistence.dao.TranslatorGroupRepository;
import translateit2.persistence.dao.UnitRepository;
import translateit2.persistence.dao.WorkRepository;
import translateit2.persistence.dto.FileInfoDto;
import translateit2.persistence.dto.InfoDto;
import translateit2.persistence.dto.PersonDto;
import translateit2.persistence.dto.ProjectDto;
import translateit2.persistence.dto.ProjectMapper;
import translateit2.persistence.dto.TranslatorGroupDto;
import translateit2.persistence.model.FileInfo;
import translateit2.persistence.model.Info;
import translateit2.persistence.model.Person;
import translateit2.persistence.model.Project;
import translateit2.persistence.model.TranslatorGroup;
import translateit2.persistence.model.Unit;
import translateit2.persistence.model.Work;

@Validated
@EnableTransactionManagement
@Service
public class ProjectServiceImpl implements ProjectService {
    static final Logger logger = LogManager.getLogger(ProjectServiceImpl.class);

    @Autowired
    private FileInfoRepository fileInfoRepo;

    @Autowired
    private TranslatorGroupRepository groupRepo;

    @Autowired
    private InfoRepository infoRepo;

    @Autowired
    private ProjectMapper modelMapper;

    @Autowired
    private PersonRepository personRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UnitRepository unitRepo;

    @Autowired
    private WorkRepository workRepo;

    /*
     * FILE INFO services start
     */
    @Transactional
    @Override
    public FileInfoDto createFileInfoDto(@Valid final FileInfoDto entity) {
        logger.log(getLoggerLevel(), "Entering createFileInfoDto with {}", entity.toString());
        FileInfo perInfo = fileInfoRepo.save(convertToEntity(entity));
        FileInfoDto infoDto = convertToDto(perInfo);
        logger.log(getLoggerLevel(), "Leaving createInfoDto with {}", infoDto.toString());
        return infoDto;
    }

    /*
     * INFO services start
     */
    @Transactional
    @Override
    public InfoDto createInfoDto(@Valid final InfoDto entity) {
        logger.log(getLoggerLevel(), "Entering createInfoDto with {}", entity.toString());
        Info perInfo = infoRepo.save(convertToEntity(entity));
        InfoDto infoDto = convertToDto(perInfo);
        logger.log(getLoggerLevel(), "Leaving createInfoDto with {}", infoDto.toString());
        return infoDto;
    }

    /*
     * GROUP services start
     */
    @Transactional
    @Override
    public TranslatorGroupDto createGroupDto(@Valid final TranslatorGroupDto entity) {
        logger.log(getLoggerLevel(), "Entering createGroupDto with {}", entity.toString());
        TranslatorGroup perGroup = groupRepo.save(convertToEntity(entity));
        TranslatorGroupDto groupDto = convertToDto(perGroup);
        logger.log(getLoggerLevel(), "Leaving createGroupDto with {}", groupDto.toString());
        return groupDto;
    }

    @Override
    public TranslatorGroupDto getGroupDtoByName(String name) {
        logger.log(getLoggerLevel(), "Entering getGroupDtoByName with {}", name.toString());
        Optional<TranslatorGroup> perGroup = groupRepo.findByName(name);
        TranslatorGroupDto groupDto = convertToDto(perGroup.get());
        logger.log(getLoggerLevel(), "Leaving getGroupDtoByName with {}", groupDto.toString());
        return groupDto;
    }

    @Override
    public void removeGroupDto(final long groupId) {
        if (groupRepo.exists(groupId)) {
            groupRepo.delete(groupId); 
            logger.log(getLoggerLevel(), "Removing GroupDto with id{}", groupId);
        }
        else
            logger.log(getLoggerLevel(), "Did not find Person with id {}", groupId);
    }
    /*
     * TRANSLATOR GROUP services end
     */

    /*
     * PERSON services start
     */
    @Transactional
    @Override
    public PersonDto createPersonDto(@Valid final PersonDto entity) {
        logger.log(getLoggerLevel(), "Entering createPersonDto with {}", entity.toString());
        Person perPerson = personRepo.save(convertToEntity(entity));
        PersonDto perPersonDto = convertToDto(perPerson);
        logger.log(getLoggerLevel(), "Leaving createPersonDto with {}", perPersonDto.toString());
        return perPersonDto;
    }

    @Transactional
    @Override
    public void removePersonDto(final long personId) {
        if (personRepo.exists(personId)) {
            personRepo.delete(personId); 
            logger.log(getLoggerLevel(), "Removing PersonDto with id{}", personId);
        }
        else
            logger.log(getLoggerLevel(), "Did not find Person with id {}", personId);
    }

    @Override
    public PersonDto getPersonDtoByPersonName(final String name) {
        logger.log(getLoggerLevel(), "Entering getPersonDtoByPersonName with {}", name.toString());
        Optional<Person> perPerson = personRepo.findByFullName(name);
        PersonDto personDto = convertToDto(perPerson.get());
        logger.log(getLoggerLevel(), "Leaving getPersonDtoByPersonName with {}", personDto.toString());
        return personDto;
    }
    /*
     * PERSON services end
     */


    /*
     * PROJECT services start
     */
    @Transactional
    @Override
    public ProjectDto createProjectDto(final ProjectDto entity, final String personName) {
        logger.log(getLoggerLevel(), "Entering createProjectDto with {}", 
                entity.toString(),personName);

        Project perProject = convertToEntity(entity);

        perProject.setPerson(personRepo.findByFullName(personName).get());
        perProject = projectRepo.save(perProject);

        ProjectDto projectDto = convertToDto(perProject);

        logger.log(getLoggerLevel(), "Leaving createProjectDto with {}", projectDto.toString());
        return projectDto;
    }

    @Override
    public ProjectDto getProjectDtoById(final long projectId) {
        logger.log(getLoggerLevel(), "Entering getProjectDtoById with id: {}", projectId);

        if (projectRepo.exists(projectId)) {
            ProjectDto projectDto = convertToDto(projectRepo.findOne(projectId));
            logger.log(getLoggerLevel(), "Leaving getProjectDtoById with {}", projectDto);
            return projectDto;
        }
        else{
            logger.log(getLoggerLevel(), "Failure in getProjectDtoById with {}", projectId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_IDENTIFIER_IN_DATA_OBJECT); 
        }
    }

    @Override
    public ProjectDto getProjectDtoByProjectName(final String projectName) {
        logger.log(getLoggerLevel(), "Entering getProjectDtoByProjectName with {}", projectName);
        Optional<Project> project = projectRepo.findByName(projectName);
        if (project.isPresent()) {
            ProjectDto projectDto = convertToDto(project.get());
            logger.log(getLoggerLevel(), "Leaving getProjectDtoByProjectName with {}", projectDto.toString());
            return projectDto;
        }
        else{
            logger.log(getLoggerLevel(), "Failure in getProjectDtoByProjectName with name {}", projectName);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_IDENTIFIER_IN_DATA_OBJECT); 
        }
    }

    @Override
    public long getProjectDtoCount() {
        long dtoCount =  projectRepo.count();
        logger.log(getLoggerLevel(), "Leaving getProjectDtoCount with dtoCount {}", dtoCount);
        return dtoCount;
    }

    @Override
    public long getProjectDtoCountByPerson(final long personId) {
        logger.log(getLoggerLevel(), "Entering getProjectDtoCount with id: {}", personId);
        long dtoCount = projectRepo.countByPersonId(personId);
        logger.log(getLoggerLevel(), "Leaving getProjectDtoCount with dtoCount {}", dtoCount);
        return dtoCount;
    }

    @Override
    public List<ProjectDto> getAllProjectDtos() {
        logger.log(getLoggerLevel(), "Entering listAllProjectDtos()");
        List<ProjectDto> projectDtos = new ArrayList<ProjectDto>();
        projectRepo.findAll().forEach(l -> projectDtos.add(convertToDto(l)));
        logger.log(getLoggerLevel(), "Leaving listProjectDtos() with list size: {}", projectDtos.size());
        if (projectDtos.isEmpty())
            return Collections.emptyList();
        else
            return projectDtos;
    }

    @Override
    public List<ProjectDto> getProjectDtos(final long personId) {
        logger.log(getLoggerLevel(), "Entering listProjectDtos with id: {}", personId);
        List<ProjectDto> projectDtos = new ArrayList<ProjectDto>();
        List<Project> projects = projectRepo.findAll().stream().filter(prj -> personId == prj.getPerson().getId())
                .collect(Collectors.toList());
        projects.forEach(prj -> projectDtos.add(convertToDto(prj)));
        logger.log(getLoggerLevel(), "Leaving listProjectDtos with list size: {}", projectDtos.size());

        if (projectDtos.isEmpty())
            return Collections.emptyList();
        else
            return projectDtos;
    }

    @Transactional
    @Override
    public void removeProjectDto(final long projectId) {
        logger.log(getLoggerLevel(), "Entering removeProjectDto with id: {}", projectId);
        if (projectRepo.exists(projectId)) {
            List<Work> allWorks = workRepo.findByProjectId(projectId);
            allWorks.stream().forEach(wrk -> removeUnitDtos(wrk.getId()));
            workRepo.delete(allWorks);
            projectRepo.delete(projectId);
            logger.log(getLoggerLevel(), "Leaving removeProjectDto()");
        } else{
            logger.log(getLoggerLevel(), "Could not remove project. No such project having id = " + projectId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_IDENTIFIER_IN_DATA_OBJECT); 
        }
    }

    @Transactional
    @Override
    public void removeProjectDtos(final List<ProjectDto> entities) {
        logger.log(getLoggerLevel(), "Entering removeProjectDtos with list size: {}", entities.size());
        entities.stream().forEach(prj -> {
            try {
                removeProjectDto(prj.getId());
            } catch (TranslateIt2Exception e) {
                try {
                    throw e;
                } catch (TranslateIt2Exception e1) {}
            }
        });
        logger.log(getLoggerLevel(), "Leaving removeProjectDtos()");
    }

    @Transactional
    @Override
    public ProjectDto updateProjectDto(@Valid final ProjectDto project) {
        logger.log(getLoggerLevel(), "Entering updateProjectDto with {}", project.toString());
        Project perProject = projectRepo.findOne(project.getId());
        convertToEntity(project, perProject);
        ProjectDto projectDto = convertToDto(projectRepo.save(perProject));
        logger.log(getLoggerLevel(), "Leaving updateProjectDto with {}", projectDto.toString());
        return projectDto;
    }

    @Override
    public Map<Long, Integer> getWorkCountPerProject(final String personName) {

        long personId=personRepo.findByFullName(personName).get().getId();
        List<Project> projects = projectRepo.findByPersonId(personId);

        // get projects by name
        //List<Project> projects = projectRepo.findAll();
        HashMap<Long, Integer> workCountMap = new LinkedHashMap<Long, Integer>();
        projects.forEach(prj -> workCountMap.put(prj.getId(), workRepo.findByProjectId(prj.getId()).size()));

        if (workCountMap.isEmpty())
            return Collections.emptyMap();
        else
            return workCountMap;
    }
    /**
     * PROJECT ends
     */

    private FileInfo convertToEntity(final FileInfoDto fileInfoDto) {
        FileInfo info = modelMapper.map(fileInfoDto, FileInfo.class);
        return info;
    }

    private Info convertToEntity(final InfoDto infoDto) {
        Info info = modelMapper.map(infoDto, Info.class);
        return info;
    }

    private Person convertToEntity(final PersonDto personDto) {
        Person person = modelMapper.map(personDto, Person.class);
        return person;
    }

    private Project convertToEntity(final ProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);
        return project;
    }

    private void convertToEntity(final ProjectDto projectDto, final Project project) {
        modelMapper.map(projectDto, project);
    }

    private TranslatorGroup convertToEntity(final TranslatorGroupDto groupDto) {
        TranslatorGroup group = modelMapper.map(groupDto, TranslatorGroup.class);
        return group;
    }

    private InfoDto convertToDto(Info info) {
        if (info == null)
            return null;
        InfoDto infoDto = modelMapper.map(info, InfoDto.class);
        return infoDto;
    }

    private PersonDto convertToDto(final Person person) {
        if (person == null)
            return null;
        PersonDto personDto = modelMapper.map(person, PersonDto.class);
        return personDto;
    }

    private FileInfoDto convertToDto(final FileInfo info) {
        if (info == null)
            return null;
        FileInfoDto infoDto = modelMapper.map(info, FileInfoDto.class);
        return infoDto;
    }

    private TranslatorGroupDto convertToDto(final TranslatorGroup group) {
        if (group == null)
            return null;
        TranslatorGroupDto groupDto = modelMapper.map(group, TranslatorGroupDto.class);
        return groupDto;
    }

    private ProjectDto convertToDto(Project project) {
        if (project == null)
            return null;
        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
        return projectDto;
    }

    /*
     * TODO: currently for testing
     */
    private Level getLoggerLevel() {
        return Level.forName("NOTICE", 450);
    }

    @Transactional
    private void removeUnitDtos(final long workId) {
        logger.log(getLoggerLevel(), "Entering removeUnitDtos with id: {} ", workId);
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        unitRepo.delete(units);
        logger.log(getLoggerLevel(), "Leaving removeUnitDtos()");
    }


}
