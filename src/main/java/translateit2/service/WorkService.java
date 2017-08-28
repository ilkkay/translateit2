package translateit2.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import translateit2.persistence.dto.UnitDto;
import translateit2.persistence.dto.WorkDto;

@Validated
public interface WorkService {
    /**
     * Unit
     */
    
    void createUnitDtos(@Valid List<UnitDto> unitDtos, final long workId);

    UnitDto getUnitDtoById(final long unitId);

    long getUnitDtoCount(final long workId);

    List<UnitDto> getPage(final long workId, final int pageNumber, final int pageSize);

    List<UnitDto> getUnitDtos(final long workId);

    UnitDto updateTranslatedUnitDto(@Valid final UnitDto translatedUnitDto, final long workId);

    void removeUnitDtos(final long workId);

    void updateUnitDtos(@Valid final List<UnitDto> unitDtos, final long workId);

    /**
     * Work
     */

    WorkDto createWorkDto(@Valid final WorkDto entity, final String groupName);

    long getTranslatedLinesCount(final long workId);

    WorkDto getWorkDtoById(final long workId);

    long getWorkDtoCount(final long groupId);

    List<WorkDto> getProjectWorkDtos(final long projectId);

    List<WorkDto> getWorkDtos(final long groupId);

    void removeWorkDto(final long workId);

    void removeWorkDtos(@Valid final List<WorkDto> entities);
    
    WorkDto updateWorkDto(@Valid final WorkDto entity);
    
    WorkDto updateProgress(final long workId);
        
}
