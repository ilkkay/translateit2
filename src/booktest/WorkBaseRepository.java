package translateit2.persistence.booktest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface WorkBaseRepository<T extends Work> extends CrudRepository<T, Long> {
}

