package translateit2.persistence.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Locale;

@Entity(name = "trProject")
@Table(name = "TR_PROJECT")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull
    private String name;

    @ManyToOne
    private Person person;

    @NotNull
    private Locale source_locale;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LanguageFileType charset;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LanguageFileFormat format;

    public Long getId() {
        return id;
    }

    public LanguageFileFormat getFormat() {
        return format;
    }
    
    public String getName() {
        return name;
    }

    public Person getPerson() {
        return person;
    }

    public Locale getSourceLocale() {
        return source_locale;
    }

    public LanguageFileType getType() {
        return charset;
    }

    public void setFormat(LanguageFileFormat format) {
        this.format = format;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setSourceLocale(Locale sourceLocale) {
        this.source_locale = sourceLocale;
    }

    public void setType(LanguageFileType type) {
        this.charset = type;
    }

    // note [MD] (3) toString on entity risks unwanted proxy resolution
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("person", person)
                .append("name", name).append("format", format).append("type", charset).append("sourceLocale", source_locale)
                .toString();
    }
}
