package carrot;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="wts", path="wts")
public interface WtsRepository extends PagingAndSortingRepository<Wts, Long>{


}
