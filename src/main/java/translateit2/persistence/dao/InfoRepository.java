package translateit2.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import translateit2.persistence.model.Info;

@RepositoryRestResource(exported = false)
public interface InfoRepository extends CrudRepository<Info, Long> {

}