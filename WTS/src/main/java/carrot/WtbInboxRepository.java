package carrot;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="wtbInboxes", path="wtbInboxes")
public interface WtbInboxRepository extends PagingAndSortingRepository<WtbInbox, Long >{


}
