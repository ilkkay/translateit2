package translateit2.restapi;

import java.util.List;
import java.util.Map;

import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dto.ProjectDto;

public class ViewProjects {
    private List<ProjectDto> projects;
    private List<LanguageFileType> supportedCharacterSets;
    private List<LanguageFileFormat> supportedFormats;
    private Map<Long, Integer> projectWorkMap;

    public List<ProjectDto> getProjects() {
        return projects;
    }

    public Map<Long, Integer> getProjectWorkMap() {
        return projectWorkMap;
    }

    public void setSupportedCharacterSets(List<LanguageFileType> characterSets) {
        this.supportedCharacterSets = characterSets;
    }

    public void setSupportedFormats(List<LanguageFileFormat> formats) {
        this.supportedFormats = formats;
    }
    
    public List<LanguageFileType> getSupportedCharacterSets() {
        return this.supportedCharacterSets;
    }

    public List<LanguageFileFormat> getSupportedFormats() {
        return this.supportedFormats;
    }
    
    public void setProjects(List<ProjectDto> projects) {
        this.projects = projects;
    }

    public void setProjectWorkMap(Map<Long, Integer> projectWorkMap) {
        this.projectWorkMap = projectWorkMap;
    }

}
