package translateit2.configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dao.ProjectRepository;

@Component
public class CharSetResolver {
    @Autowired
    private ProjectRepository projectRepo;

    public Charset getProjectCharSet(long projectId) {
        LanguageFileType typeExpected = projectRepo.findOne(projectId).getType();
        if (typeExpected.equals(LanguageFileType.ISO8859_1))
            return StandardCharsets.ISO_8859_1;
        else
            return StandardCharsets.UTF_8;
    }
}
