package translateit2.persistence.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import translateit2.persistence.model.Project;

// curl http://localhost:8080/project/search/findByName?name=Translate%20IT%202
//curl -X DELETE http://localhost:8080/work/5
//curl -X DELETE http://localhost:8080/project/4
// insert new project
//curl -i -X POST -H "Content-Type:application/json" -d "{  \"name\" : \"The real world\" }" http://localhost:8080/project
// insert new work
//curl -i -X POST -H "Content-Type:application/json" -d "{  \"locale\" : \"en_US\",  \"project\" : \"/project/6\" }" http://localhost:8080/work


//@RepositoryRestResource(collectionResourceRel = "project", path = "project")
@RepositoryRestResource(exported = false)
public interface ProjectRepository extends CrudRepository<Project, Long> {
    
    Long countByPersonId(final long personId);

    List<Project> findByPersonId(final long personId);
    
    Optional<Project> findByName(@Param("name") String name);
    
    @Override
    List<Project> findAll();
}
