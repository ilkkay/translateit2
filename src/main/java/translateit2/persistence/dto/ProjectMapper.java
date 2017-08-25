package translateit2.persistence.dto;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import translateit2.persistence.model.FileInfo;
import translateit2.persistence.model.Project;
import translateit2.persistence.model.Unit;
import translateit2.persistence.model.Work;

// converter: http://stackoverflow.com/questions/36717365/how-to-use-modelmapper-to-convert-nested-classes
@Component
@Primary
public class ProjectMapper extends ModelMapper {
    PropertyMap<ProjectDto, Project> projectDtoMap = new PropertyMap<ProjectDto, Project>() {
        @Override
        protected void configure() {
            skip().setPerson(null);
        }
    };

    PropertyMap<Project, ProjectDto> projectMap = new PropertyMap<Project, ProjectDto>() {

        @Override
        protected void configure() {
            // TODO Auto-generated method stub
            map().setPersonId(source.getPerson().getId());
        }
    };

    PropertyMap<UnitDto, Unit> unitDtoMap = new PropertyMap<UnitDto, Unit>() {
        @Override
        protected void configure() {
            skip().setWork(null);
        }
    };

    PropertyMap<Unit, UnitDto> unitMap = new PropertyMap<Unit, UnitDto>() {
        @Override
        protected void configure() {
            // TODO Auto-generated method stub
            map().setWorkId(source.getWork().getId());
        }
    };

    PropertyMap<WorkDto, Work> workDtoMap = new PropertyMap<WorkDto, Work>() {
        @Override
        protected void configure() {
            //map().setTarget_locale(destination.getLocale());
        }
    };

    PropertyMap<Work, WorkDto> workMap = new PropertyMap<Work, WorkDto>() {
        @Override
        protected void configure() {
            // TODO Auto-generated method stub
            //map().setTarget_locale(source.getTarget_locale());
        }
    };
    
    PropertyMap<FileInfoDto, FileInfo> fileInfoDtoMap = new PropertyMap<FileInfoDto, FileInfo>() {
        @Override
        protected void configure() {
            skip().setWork(null);
        }
    };

    PropertyMap<FileInfo, FileInfoDto> fileInfoMap = new PropertyMap<FileInfo, FileInfoDto>() {
        @Override
        protected void configure() {
            // TODO Auto-generated method stub
            map().setWorkId(source.getWork().getId());
        }
    };
    
    public ProjectMapper() {
        addMappings(projectMap);
        addMappings(projectDtoMap);
        addMappings(fileInfoMap);
        addMappings(fileInfoDtoMap);
        //addMappings(workMap);
        //addMappings(workDtoMap);
    }

}
