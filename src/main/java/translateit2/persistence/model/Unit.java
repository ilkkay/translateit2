package translateit2.persistence.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "trUnit")
@Table(name = "TR_UNIT")
public class Unit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(length=10000)
    private String segment_key;

    @Max(10000) 
    @Min(1) 
    private int serialNumber;

    @Embedded // @Column(columnDefinition="TEXT")
    @AttributeOverrides({
            @AttributeOverride(name = "text", column = @Column(name = "source_text", length=10000, nullable=false)),
            @AttributeOverride(name = "plural", column = @Column(name = "source_plural", length=10000)),
            @AttributeOverride(name = "skeleton_tag", column = @Column(name = "source_skeleton_tag")) })
    private Source source;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "text", column = @Column(name = "target_text", length=10000)),
            @AttributeOverride(name = "plural", column = @Column(name = "target_plural", length=10000)),
            @AttributeOverride(name = "skeleton_tag", column = @Column(name = "target_skeleton_tag")) })
    private Target target;

    @ManyToOne
    private Work work;

    public Long getId() {
        return id;
    }

    public String getSegmentKey() {
        return segment_key;
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

    public Work getWork() {
        return work;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSegmentKey(String segmentKey) {
        this.segment_key = segmentKey;
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

    public void setWork(Work work) {
        this.work = work;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
      }
}
