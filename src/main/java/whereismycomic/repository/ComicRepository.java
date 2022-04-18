package whereismycomic.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import whereismycomic.domain.Comic;

/**
 * Spring Data SQL repository for the Comic entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {}
