package whereismycomic.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import whereismycomic.domain.Characters;

/**
 * Spring Data SQL repository for the Characters entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CharactersRepository extends JpaRepository<Characters, Long> {}
