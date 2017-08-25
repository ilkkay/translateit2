package translateit2.persistence.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class FileInfoDto {
    private String backup_file;

    private long id;

    private String original_file;
    
    private String skeleton_file;

    private long workId;

    public String getBackup_file() {
        return backup_file;
    }

    public long getId() {
        return id;
    }

    public String getOriginal_file() {
        return original_file;
    }


    public String getSkeleton_file() {
        return skeleton_file;
    }


    public long getWorkId() {
        return workId;
    }


    public void setBackup_file(String backup_file) {
        this.backup_file = backup_file;
    }

    public void setOriginal_file(String original_file) {
        this.original_file = original_file;
    }

    public void setWorkId(long workId) {
        this.workId = workId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                .append("original_file", original_file)
                .append("backup_file", backup_file)
                .append("skeleton_file", skeleton_file)
                .toString();
    }
}

