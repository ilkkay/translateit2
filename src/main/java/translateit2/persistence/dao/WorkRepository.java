package translateit2.persistence.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import translateit2.persistence.model.Work;

//@RepositoryRestResource(collectionResourceRel = "work", path = "work")
@RepositoryRestResource(exported = false)
public interface WorkRepository extends CrudRepository<Work, Long> {
    Long countByGroupId(final long groupId);

    List<Work> findByProjectId(final long projectId);
    
    Optional<Work> findByVersion(@Param("version") String version);
    
    Optional<Work> findByProjectIdAndVersion(final long projectId, final String version);
    
    Optional<Work> findByProjectIdAndVersionAndIdNot(final long projectId, final String version, final long workId);

    @Override
    List<Work> findAll();
}
