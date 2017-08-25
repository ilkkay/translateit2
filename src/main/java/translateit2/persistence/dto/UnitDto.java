package translateit2.persistence.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import translateit2.persistence.model.Source;
import translateit2.persistence.model.Target;

public class UnitDto {
    private long id;

    private String segmentKey;

    private int serialNumber;

    private Source source;

    private Target target;

    private long workId;

    public Long getId() {
        return id;
    }

    public String getSegmentKey() {
        return segmentKey;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public Source getSource() {
        return source;
    }

    public Target getTarget() {
        return target;
    }

    public long getWorkId() {
        return workId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSegmentKey(String segmentKey) {
        this.segmentKey = segmentKey;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void setWorkId(long workId) {
        this.workId = workId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("workId", workId).append("segmentKey", segmentKey)
                .append("serialNumber", serialNumber).append("source", source).append("target", target).toString();
    }
}
