package translateit2.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import translateit2.persistence.dao.UnitRepository;
import translateit2.persistence.model.State;
import translateit2.restapi.ViewStatistics;

@Component //WorkStatisticsLogic
public class WorkStatisticsLogic {
    @Autowired
    UnitRepository unitRepo;
    
    public ViewStatistics getStatistics(final long workId) {
        ViewStatistics stats = new ViewStatistics();
        
        long translated = unitRepo.countByWorkIdAndTargetState(workId, State.TRANSLATED);
        long needsReview = unitRepo.countByWorkIdAndTargetState(workId, State.NEEDS_REVIEW);
        
        stats.setReviewed(0L);
        stats.setTotal(unitRepo.countByWorkId(workId));
        stats.setTranslated(translated + needsReview);
        stats.setWorkId(workId);
        
        return stats;
    }

}
