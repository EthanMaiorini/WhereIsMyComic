package whereismycomic.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whereismycomic.domain.Series;
import whereismycomic.repository.SeriesRepository;

/**
 * Service Implementation for managing {@link Series}.
 */
@Service
@Transactional
public class SeriesService {

    private final Logger log = LoggerFactory.getLogger(SeriesService.class);

    private final SeriesRepository seriesRepository;

    public SeriesService(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    /**
     * Save a series.
     *
     * @param series the entity to save.
     * @return the persisted entity.
     */
    public Series save(Series series) {
        log.debug("Request to save Series : {}", series);
        return seriesRepository.save(series);
    }

    /**
     * Update a series.
     *
     * @param series the entity to save.
     * @return the persisted entity.
     */
    public Series update(Series series) {
        log.debug("Request to save Series : {}", series);
        return seriesRepository.save(series);
    }

    /**
     * Partially update a series.
     *
     * @param series the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Series> partialUpdate(Series series) {
        log.debug("Request to partially update Series : {}", series);

        return seriesRepository
            .findById(series.getId())
            .map(existingSeries -> {
                if (series.getName() != null) {
                    existingSeries.setName(series.getName());
                }

                return existingSeries;
            })
            .map(seriesRepository::save);
    }

    /**
     * Get all the series.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Series> findAll() {
        log.debug("Request to get all Series");
        return seriesRepository.findAll();
    }

    /**
     * Get one series by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Series> findOne(Long id) {
        log.debug("Request to get Series : {}", id);
        return seriesRepository.findById(id);
    }

    /**
     * Delete the series by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Series : {}", id);
        seriesRepository.deleteById(id);
    }
}
