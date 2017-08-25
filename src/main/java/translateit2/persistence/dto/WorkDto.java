package translateit2.persistence.dto;

import java.time.LocalDate;
import java.util.Locale;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import translateit2.persistence.model.Priority;
import translateit2.persistence.model.Status;
import translateit2.validator.WorkConstraint;

@WorkConstraint
public class WorkDto {
    private String backupFile;

    @NotNull
    private LocalDate deadLine;
    
    private LocalDate finished;

    private long groupId;

    private Long id;

    @NotNull
    private Locale locale;

    
    //private Locale target_locale;
    
    private String originalFile;

    @NotNull
    private Priority priority;

    private int progress;

    private long projectId;

    private String skeletonFile;

    private LocalDate started;

    private Status status;

    @NotNull
    @Size (min = 1, max = 10)
    @Pattern(regexp=".*[[0-9][\\.]]")
    private String version;

    public String getBackupFile() {
        return backupFile;
    }

    public LocalDate getDeadLine() {
        return deadLine;
    }

    public LocalDate getFinished() {
        return finished;
    }

    public long getGroupId() {
        return groupId;
    }

    public Long getId() {
        return id;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getOriginalFile() {
        return originalFile;
    }
    
    public Priority getPriority() {
        return priority;
    }

    public int getProgress() {
        return progress;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getSkeletonFile() {
        return skeletonFile;
    }

    public LocalDate getStarted() {
        return started;
    }

    public Status getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    public void setBackupFile(String backupFile) {
        this.backupFile = backupFile;
    }

    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public void setFinished(LocalDate finished) {
        this.finished = finished;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setOriginalFile(String originalFile) {
        this.originalFile = originalFile;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public void setSkeletonFile(String skeletonFile) {
        this.skeletonFile = skeletonFile;
    }

    public void setStarted(LocalDate started) {
        this.started = started;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /*
    public Locale getTarget_locale() {
        return target_locale;
    }

    public void setTarget_locale(Locale target_locale) {
        this.target_locale = target_locale;
    }*/

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("groupId", groupId).append("projectId", projectId)
                .append("locale", locale).append("version", version).append("originalFile", originalFile)
                .append("skeletonFile", skeletonFile).append("backupFile", backupFile).append("status", status)
                .append("progress", progress).append("started", started).append("finished", finished).toString();
    }

}
