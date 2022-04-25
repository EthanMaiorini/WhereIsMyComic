package whereismycomic.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import whereismycomic.domain.Series;

import java.util.List;

/**
 * Spring Data SQL repository for the Series entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    @Query(value = "SELECT s.Name " +
        "FROM Series s " +
        "WHERE s.Character_Id = ?1 ", nativeQuery = true)
    public List<Series> findSeriesById(Long Id);
}
