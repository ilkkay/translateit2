package translateit2.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "trPerson")
@Table(name = "TR_PERSON")
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "full_name")
    private String fullName;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public String getFullName() {
        return fullName;
    }

    public Long getId() {
        return id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
      }
}
