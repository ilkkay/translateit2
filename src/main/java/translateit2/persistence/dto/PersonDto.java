package translateit2.persistence.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PersonDto {
    private String fullName;

    private long id;

    public String getFullName() {
        return fullName;
    }

    public long getId() {
        return id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("fullName", fullName).toString();
    }
}
