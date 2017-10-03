package translateit2.persistence.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;

//
// property testing
// https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/?v=6.0#_examples
//

@Entity(name = "trWork")
@Table(name = "TR_WORK")
public class Work implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String backup_file;

    @NotNull
    @Size (min = 1, max = 10)
    @Pattern(regexp=".*[[0-9][\\.]]")
    private String version;
    
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate deadLine;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate finished;
    
    @ManyToOne
    private TranslatorGroup group;

    @NotNull
    private Locale locale;

  
    //private Locale target_locale;
    
    private String original_file;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private int progress;

    @ManyToOne
    private Project project;

    private String skeleton_file;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate started;

    @Enumerated(EnumType.STRING)
    private Status status;


    public String getBackupFile() {
        return backup_file;
    }

    public LocalDate getDeadLine() {
        return deadLine;
    }

    public LocalDate getFinished() {
        return finished;
    }

    public TranslatorGroup getGroup() {
        return group;
    }

    public Long getId() {
        return id;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getOriginalFile() {
        return original_file;
    }

    public Priority getPriority() {
        return priority;
    }

    // note [MD] (3) double?
    public double getProgress() {
        return progress;
    }

    public Project getProject() {
        return project;
    }

    public String getSkeletonFile() {
        return skeleton_file;
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
        this.backup_file = backupFile;
    }

    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public void setFinished(LocalDate finished) {
        this.finished = finished;
    }

    public void setGroup(TranslatorGroup group) {
        this.group = group;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setOriginalFile(String originalFile) {
        this.original_file = originalFile;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setSkeletonFile(String skeletonFile) {
        this.skeleton_file = skeletonFile;
    }

    public void setStarted(LocalDate started) {
        this.started = started;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /*public Locale getTarget_locale() {
        return target_locale;
    }

    public void setTarget_locale(Locale target_locale) {
        this.target_locale = target_locale;
    }*/

    public void setVersion(String version) {
        this.version = version;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
      }
}
