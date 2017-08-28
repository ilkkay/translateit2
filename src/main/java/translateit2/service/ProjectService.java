package translateit2.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import translateit2.persistence.dto.FileInfoDto;
import translateit2.persistence.dto.InfoDto;
import translateit2.persistence.dto.PersonDto;
import translateit2.persistence.dto.ProjectDto;
import translateit2.persistence.dto.TranslatorGroupDto;

@Validated
public interface ProjectService {
    FileInfoDto createFileInfoDto(@Valid final FileInfoDto entity);

    TranslatorGroupDto createGroupDto(@Valid final TranslatorGroupDto entity);

    InfoDto createInfoDto(@Valid final InfoDto entity);

    /**
     * Person + Group + Info are needed for project creation
     */
    PersonDto createPersonDto(@Valid final PersonDto entity);

    /**
     * Project
     */
    ProjectDto createProjectDto(@Valid final ProjectDto entity,final String personName);
    
    void removePersonDto(final long personId);
    
    TranslatorGroupDto getGroupDtoByName(final String name);
    
    void removeGroupDto(final long groupId);

    PersonDto getPersonDtoByPersonName(final String personName);

    ProjectDto getProjectDtoById(long projectId);

    ProjectDto getProjectDtoByProjectName(final String projectName);

    long getProjectDtoCount();

    long getProjectDtoCountByPerson(final long personId);
    
    Map<Long, Integer> getWorkCountPerProject(final String personName);

    List<ProjectDto> getAllProjectDtos();

    List<ProjectDto> getProjectDtos(final long personId);

    void removeProjectDto(final long projectId);
    
    void removeProjectDtos(@Valid final List<ProjectDto> entities);

    ProjectDto updateProjectDto(@Valid final ProjectDto entity);
}
