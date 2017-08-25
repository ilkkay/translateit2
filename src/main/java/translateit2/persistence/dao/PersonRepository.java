package translateit2.persistence.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import translateit2.persistence.model.Person;

@RepositoryRestResource(exported = false)
public interface PersonRepository extends CrudRepository<Person, Long> {
    Optional<Person> findByFullName(@Param("fullName") String fullName);
}
