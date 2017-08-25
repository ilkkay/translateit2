package translateit2.persistence.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "trFileInfo")
@Table(name = "TR_file_info")
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String backup_file;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String original_file;

    private String skeleton_file;

    @OneToOne
    private Work work;
    
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

    public void setBackup_file(String backup_file) {
        this.backup_file = backup_file;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOriginal_file(String original_file) {
        this.original_file = original_file;
    }

    public void setSkeleton_file(String skeleton_file) {
        this.skeleton_file = skeleton_file;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
      }    
}
