package whereismycomic.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import whereismycomic.domain.Series;

/**
 * Spring Data SQL repository for the Series entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {}
