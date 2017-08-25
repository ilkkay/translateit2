package translateit2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

import translateit2.exception.TranslateIt2ErrorCode;
import translateit2.exception.TranslateIt2Exception;
import translateit2.persistence.dao.ProjectRepository;
import translateit2.persistence.dao.TranslatorGroupRepository;
import translateit2.persistence.dao.UnitRepository;
import translateit2.persistence.dao.WorkRepository;
import translateit2.persistence.dto.ProjectMapper;
import translateit2.persistence.dto.UnitDto;
import translateit2.persistence.dto.WorkDto;
import translateit2.persistence.model.State;
import translateit2.persistence.model.Target;
import translateit2.persistence.model.Unit;
import translateit2.persistence.model.Work;

@Validated
@EnableTransactionManagement
@Service
public class WorkServiceImpl implements WorkService {
    static final Logger logger = LogManager.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectMapper modelMapper;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UnitRepository unitRepo;

    @Autowired
    private WorkRepository workRepo;

    @Autowired
    private TranslatorGroupRepository groupRepo;

    @Transactional
    @Override
    public void createUnitDtos(List<UnitDto> unitDtos, long workId) {
        logger.log(getLoggerLevel(), "Entering createUnitDtos with unit list size {} and  id: {} ", unitDtos.size(),
                workId);
        List<Unit> units = new ArrayList<Unit>();
        Work work = workRepo.findOne(workId);
        unitDtos.stream().forEach(dto -> units.add(convertToEntity(dto)));
        units.stream().forEach(unit -> unit.setWork(work));
        unitRepo.save(units);
        // logger.log(getLoggerLevel(), "Leaving createUnitDtos() saved for {}",
        // work.toString());
        logger.log(getLoggerLevel(), "Leaving createUnitDtos()");
    }

    @Transactional
    @Override
    public WorkDto createWorkDto(@Valid final WorkDto entity, final String groupName) {
        logger.log(getLoggerLevel(), "Entering createWorkDto with {} for group {} ", entity.toString(),groupName);

        if (groupRepo.findByName(groupName).isPresent()) {

            Work perWork = convertToEntity(entity);
            perWork.setGroup(groupRepo.findByName(groupName).get());
            perWork.setProject(projectRepo.findOne(entity.getProjectId()));
            perWork = workRepo.save(perWork);
            WorkDto workDto = convertToDto(perWork);

            logger.log(getLoggerLevel(), "Leaving createWorkDto with {} ", workDto.toString());
            return workDto;
        }
        else {
            logger.log(getLoggerLevel(), "Failed to create WorkDto with {} for group {} ", entity.toString(),groupName);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_IDENTIFIER_IN_DATA_OBJECT); 
        }

    }

    // https://www.javacodegeeks.com/2013/05/spring-data-solr-tutorial-pagination.html
    @Override
    public List<UnitDto> getPage(final long workId, int pageNumber, int pageSize) {

        logger.log(getLoggerLevel(), "Entering getPage with  id: {}, page number {} and pageSize {}", workId, pageNumber,
                pageSize);

        int pageIndex = pageNumber - 1;
        PageRequest req = new PageRequest(pageIndex, pageSize, Sort.Direction.ASC, "serialNumber");
        Page<Unit> p = unitRepo.findByWorkId(workId, req);

        List<UnitDto> unitDtos = new ArrayList<UnitDto>();
        p.forEach(unit -> unitDtos.add(convertToDto(unit)));

        logger.log(getLoggerLevel(), "Leaving getPage with unit list size {}", unitDtos.size());
        if (unitDtos.isEmpty()) 
            return Collections.emptyList();
        else 
            return unitDtos;

    }

    @Override
    public long getTranslatedLinesCount(long workId) {
        logger.log(getLoggerLevel(), "Entering getStatistics with  id: {} ", workId);

        long translated = unitRepo.countByWorkIdAndTargetState(workId, State.TRANSLATED);
        long needsReview = unitRepo.countByWorkIdAndTargetState(workId, State.NEEDS_REVIEW);

        logger.log(getLoggerLevel(), "Leaving getStatistics with {} ", translated + needsReview);

        return translated + needsReview;
    }

    @Override
    public UnitDto getUnitDtoById(long unitId) {        
        if (unitRepo.exists(unitId)) {
            UnitDto unitDto = convertToDto(unitRepo.findOne(unitId));
            logger.log(getLoggerLevel(), "Leaving getUnitDtoById with id: {} ", unitDto.toString());
            return unitDto;         
        }
        else{
            logger.log(getLoggerLevel(), "Failure in getUnitDtoById with {}", unitId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_IDENTIFIER_IN_DATA_OBJECT); 
        }
    }

    @Override
    public long getUnitDtoCount(final long workId) {
        logger.log(getLoggerLevel(), "Entering getUnitDtoCount with id: {} ", workId);
        long cnt = unitRepo.countByWorkId(workId);
        logger.log(getLoggerLevel(), "Leaving getUnitDtoCount with count: {} ", cnt);
        return cnt;
    }

    @Override
    public WorkDto getWorkDtoById(long workId) {
        if (workRepo.exists(workId)) {
            WorkDto workDto = convertToDto(workRepo.findOne(workId));
            logger.log(getLoggerLevel(), "Leaving getWorkDtoById with {} ", workDto.toString());
            return workDto;
        }
        else{
            logger.log(getLoggerLevel(), "Failure in getWorkDtoById with {}", workId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_IDENTIFIER_IN_DATA_OBJECT); 
        }

    }

    @Override
    public long getWorkDtoCount(final long groupId) {
        logger.log(getLoggerLevel(), "Entering getWorkDtoCount with id: {} ", groupId);
        long cnt = workRepo.countByGroupId(groupId);
        logger.log(getLoggerLevel(), "Leaving getWorkDtoCount with count {} ", cnt);
        return cnt;
    }

    @Override
    public List<WorkDto> getProjectWorkDtos(long projectId) {
        logger.log(getLoggerLevel(), "Entering listProjectWorkDtos with id: {} ", projectId);
        List<WorkDto> workDtos = new ArrayList<WorkDto>();
        List<Work> works = workRepo.findAll().stream().filter(wrk -> projectId == wrk.getProject().getId())
                .collect(Collectors.toList());
        works.forEach(prj -> workDtos.add(convertToDto(prj)));

        logger.log(getLoggerLevel(), "Leaving listWorkDtos with list size: {} ", workDtos.size());
        return workDtos;
    }

    @Override
    public List<UnitDto> getUnitDtos(final long workId) {
        logger.log(getLoggerLevel(), "Entering listUnitDtos with id: {} ", workId);
        List<UnitDto> unitDtos = new ArrayList<UnitDto>();
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        units.forEach(unit -> unitDtos.add(convertToDto(unit)));

        logger.log(getLoggerLevel(), "Leaving listUnitDtos with unit list size: {} ", unitDtos.size());
        return unitDtos;
    }

    @Override
    public List<WorkDto> getWorkDtos(long groupId) {
        logger.log(getLoggerLevel(), "Entering listWorkDtos with id: {} ", groupId);
        List<WorkDto> workDtos = new ArrayList<WorkDto>();
        List<Work> works = workRepo.findAll().stream().filter(wrk -> groupId == wrk.getGroup().getId())
                .collect(Collectors.toList());
        works.forEach(prj -> workDtos.add(convertToDto(prj)));

        logger.log(getLoggerLevel(), "Leaving listWorkDtos with list size: {} ", workDtos.size());
        return workDtos;
    }

    @Transactional
    @Override
    public void removeUnitDtos(final long workId) {
        logger.log(getLoggerLevel(), "Entering removeUnitDtos with id: {} ", workId);
        List<Unit> units = unitRepo.findAll().stream().filter(unit -> workId == unit.getWork().getId())
                .collect(Collectors.toList());
        unitRepo.delete(units);
        logger.log(getLoggerLevel(), "Leaving removeUnitDtos()");
    }

    @Transactional
    @Override
    public void removeWorkDto(final long workId) {
        logger.log(getLoggerLevel(), "Entering removeWorkDto with id: {} ", workId);

        if (workRepo.exists(workId)) {
            removeUnitDtos(workId);
            workRepo.delete(workId);
            logger.log(getLoggerLevel(), "Leaving removeWorkDto()");
        } else {
            logger.log(getLoggerLevel(), "Failure in removeWorkDto(). Improper work id", workId);
            throw new TranslateIt2Exception(TranslateIt2ErrorCode.IMPROPER_IDENTIFIER_IN_DATA_OBJECT); 
        }
    }

    @Transactional
    @Override
    public void removeWorkDtos(List<WorkDto> entities){
        logger.log(getLoggerLevel(), "Entering removeWorkDtos with list size {} ", entities.size());
        entities.stream().forEach(wrk -> {
                try {
                    removeWorkDto(wrk.getId());
                } catch (TranslateIt2Exception e) {
                    try {
                        throw e;
                    } catch (TranslateIt2Exception e1) {} 
                }
        });
        logger.log(getLoggerLevel(), "Leaving removeWorkDtos()");
    }

    @Transactional
    @Override
    public void updateUnitDtos(List<UnitDto> unitDtos, long workId) {
        logger.log(getLoggerLevel(), "Entering updateUnitDtos with unit list size {} and  id: {} ", unitDtos.size(),
                workId);
        List<Unit> units = new ArrayList<Unit>();

        for (UnitDto dto : unitDtos) {
            Unit perUnit = unitRepo.findOne(dto.getId());
            convertToEntity(dto, perUnit);
            units.add(perUnit);
        }
        unitRepo.save(units);
        logger.log(getLoggerLevel(), "Leaving updateUnitDtos()");
    }

    @Transactional
    @Override
    public WorkDto updateWorkDto(@Valid final WorkDto work) {
        logger.log(getLoggerLevel(), "Entering updateWorkDto with {} ", work.toString());
        Work perWork = workRepo.findOne(work.getId());
        convertToEntity(work, perWork);
        WorkDto workDto = convertToDto(workRepo.save(perWork));
        logger.log(getLoggerLevel(), "Leaving updateWorkDto with {} ", workDto.toString());
        return workDto;
    }

    /*
     * UNIT services start
     */
    private UnitDto convertToDto(Unit unit) {
        if (unit == null)
            return null;
        UnitDto unitDto = modelMapper.map(unit, UnitDto.class);
        return unitDto;
    }

    /*
     * WORK services start
     */
    private WorkDto convertToDto(Work work) {
        if (work == null)
            return null;
        WorkDto workDto = modelMapper.map(work, WorkDto.class);
        return workDto;
    }

    private Unit convertToEntity(UnitDto unitDto) {
        Unit unit = modelMapper.map(unitDto, Unit.class);
        return unit;
    }

    private void convertToEntity(UnitDto unitDto, Unit unit) {
        modelMapper.map(unitDto, unit);
    }

    private Work convertToEntity(WorkDto workDto) {
        Work work = modelMapper.map(workDto, Work.class);
        return work;
    }

    private void convertToEntity(WorkDto workDto, Work work) {
        modelMapper.map(workDto, work);
    }

    /*
     * TODO: currently for testing
     */
    private Level getLoggerLevel() {
        return Level.forName("NOTICE", 450);
    }

    @Transactional
    @Override
    public UnitDto updateTranslatedUnitDto(UnitDto unitDto, long workId) {

        logger.log(getLoggerLevel(), "Entering updateTranslatedUnitDto() with unit {} and workId {}", unitDto.toString(), workId);

        if (!(workRepo.exists(workId))) {
            logger.log(getLoggerLevel(), "Failure in updateTranslatedUnitDto() with unit {} and workId {}", unitDto.toString(), workId);
            throw new IllegalArgumentException("Could not remove work. No such work having id = " + workId);
        }

        // IF the target.Text is != empty AND current state is untranslated,
        // THEN increment translated value and set state translated
        if ((unitDto.getTarget().getText().trim().length() > 0)
                && ((unitDto.getTarget().getState() == State.NEEDS_TRANSLATION))
                || (unitDto.getTarget().getState() == State.NEW)) {
            Target t = unitDto.getTarget();
            t.setState(State.TRANSLATED);
        } // if ELSE do nothing (state == translated)


        // IF target.Text.Trim() == empty AND current state is translated,
        // THEN decrement translated value and set state untranslated
        if ((unitDto.getTarget().getText().trim().length() == 0) && (unitDto.getTarget().getState() == State.TRANSLATED)) {
            Target t = unitDto.getTarget();
            t.setState(State.NEEDS_TRANSLATION);
        }  // if ELSE do nothing (state == untranslated)


        Unit perUnit = unitRepo.findOne(unitDto.getId());
        convertToEntity(unitDto, perUnit);
        perUnit = unitRepo.save(perUnit);

        logger.log(getLoggerLevel(), "Leaving updateTranslatedUnitDto() with unit {} and workId {}", perUnit.toString(), workId);

        return convertToDto(perUnit);

    }

    @Transactional
    @Override
    public WorkDto updateProgress(final long workId) {

        long translated = unitRepo.countByWorkIdAndTargetState(workId, State.TRANSLATED);
        long needsReview = unitRepo.countByWorkIdAndTargetState(workId, State.NEEDS_REVIEW);
        long total = unitRepo.countByWorkId(workId);

        double progress = 1.0 * (translated + needsReview ) / total;

        int procents = (int) Math.round(100 * progress);
        
        Work work = workRepo.findOne(workId);
        work.setProgress(procents);
        work = workRepo.save(work);

        return convertToDto(work);
    }
}
