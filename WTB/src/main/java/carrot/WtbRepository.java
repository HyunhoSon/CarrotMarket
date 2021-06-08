package carrot;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="wtbs", path="wtbs")
public interface WtbRepository extends PagingAndSortingRepository<Wtb, Long>{


}
