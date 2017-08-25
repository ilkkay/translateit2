package translateit2.persistence.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class InfoDto {
    private long id;

    private long projectId;
    
    private String text;

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("text", text).toString();
    }
}
