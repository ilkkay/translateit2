package translateit2.restapi;

import java.util.List;

import translateit2.persistence.dto.UnitDto;

public class ViewUnits {
    private long pageCount;
    private List<UnitDto> units;

    ViewStatistics statistics;

    public long getPageCount() {
        return pageCount;
    }

    public ViewStatistics getStatistics() {
        return statistics;
    }

    public List<UnitDto> getUnits() {
        return units;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public void setStatistics(ViewStatistics statistics) {
        this.statistics = statistics;
    }

    public void setUnits(List<UnitDto> units) {
        this.units = units;
    }
}
