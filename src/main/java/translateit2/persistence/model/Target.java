package translateit2.persistence.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Embeddable
public class Target {
    
    private boolean equivalent;

    @Column(length=10000)
    private String plural;

    private String skeleton_tag;

    @Column(length=10000)
    private String text;

    Comment comment;

    History history;

    Note note;

    State state;

    public Comment getComment() {
        return comment;
    }

    public History getHistory() {
        return history;
    }

    public Note getNote() {
        return note;
    }

    public String getPlural() {
        return plural;
    }

    public String getSkeletonTag() {
        return skeleton_tag;
    }

    public State getState() {
        return state;
    }

    public String getText() {
        return text;
    }

    public boolean isEquivalent() {
        return equivalent;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setEquivalent(boolean equivalent) {
        this.equivalent = equivalent;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public void setSkeletonTag(String skeletonTag) {
        this.skeleton_tag = skeletonTag;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("text", text).append("plural", plural).append("state", state)
                .append("equivalent", equivalent).append("skeletonTag", skeleton_tag).append("comment", comment)
                .append("history", history).append("note", note).toString();
    }
}
