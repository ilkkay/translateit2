package translateit2.restapi;

import java.util.List;

import translateit2.persistence.dto.WorkDto;
import translateit2.persistence.model.Priority;

public class ViewWorks {
    private List<Priority> supportedPriorities;
    private List<WorkDto> works;

    public List<Priority> getSupportedPriorities() {
        return supportedPriorities;
    }
    
    public List<WorkDto> getWorks() {
        return works;
    }

    public void setSupportedPriorities(List<Priority> supportedPriorities) {
        this.supportedPriorities = supportedPriorities;
    }    

    public void setWorks(List<WorkDto> works) {
        this.works = works;
    }

}
