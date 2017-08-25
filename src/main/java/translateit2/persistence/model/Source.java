package translateit2.persistence.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Embeddable
public class Source {

    @Column(length=10000)
    private String plural;

    private String skeleton_tag;

    @NotNull
    @Column(length=10000)
    private String text;

    public String getPlural() {
        return plural;
    }

    public String getSkeletonTag() {
        return skeleton_tag;
    }

    public String getText() {
        return text;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public void setSkeletonTag(String skeletonTag) {
        this.skeleton_tag = skeletonTag;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("text", text).append("plural", plural)
                .append("skeletonTag", skeleton_tag).toString();
    }
}
