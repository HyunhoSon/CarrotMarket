package carrot;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MyPageRepository extends CrudRepository<MyPage, Long> {

    Optional<MyPage> findByWtbId(Long wtbId);
}