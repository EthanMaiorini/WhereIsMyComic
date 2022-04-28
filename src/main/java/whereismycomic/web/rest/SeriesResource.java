package whereismycomic.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import whereismycomic.domain.Series;
import whereismycomic.repository.SeriesRepository;
import whereismycomic.service.SeriesQueryService;
import whereismycomic.service.SeriesService;
import whereismycomic.service.criteria.SeriesCriteria;
import whereismycomic.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link whereismycomic.domain.Series}.
 */
@RestController
@RequestMapping("/api")
public class SeriesResource {

    private final Logger log = LoggerFactory.getLogger(SeriesResource.class);

    private static final String ENTITY_NAME = "series";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SeriesService seriesService;

    private final SeriesRepository seriesRepository;

    private final SeriesQueryService seriesQueryService;

    public SeriesResource(SeriesService seriesService, SeriesRepository seriesRepository, SeriesQueryService seriesQueryService) {
        this.seriesService = seriesService;
        this.seriesRepository = seriesRepository;
        this.seriesQueryService = seriesQueryService;
    }

    /**
     * {@code POST  /series} : Create a new series.
     *
     * @param series the series to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new series, or with status {@code 400 (Bad Request)} if the series has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/series")
    public ResponseEntity<Series> createSeries(@RequestBody Series series) throws URISyntaxException {
        log.debug("REST request to save Series : {}", series);
        if (series.getId() != null) {
            throw new BadRequestAlertException("A new series cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Series result = seriesService.save(series);
        return ResponseEntity
            .created(new URI("/api/series/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /series/:id} : Updates an existing series.
     *
     * @param id the id of the series to save.
     * @param series the series to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated series,
     * or with status {@code 400 (Bad Request)} if the series is not valid,
     * or with status {@code 500 (Internal Server Error)} if the series couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/series/{id}")
    public ResponseEntity<Series> updateSeries(@PathVariable(value = "id", required = false) final Long id, @RequestBody Series series)
        throws URISyntaxException {
        log.debug("REST request to update Series : {}, {}", id, series);
        if (series.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, series.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!seriesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Series result = seriesService.update(series);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, series.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /series/:id} : Partial updates given fields of an existing series, field will ignore if it is null
     *
     * @param id the id of the series to save.
     * @param series the series to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated series,
     * or with status {@code 400 (Bad Request)} if the series is not valid,
     * or with status {@code 404 (Not Found)} if the series is not found,
     * or with status {@code 500 (Internal Server Error)} if the series couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/series/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Series> partialUpdateSeries(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Series series
    ) throws URISyntaxException {
        log.debug("REST request to partial update Series partially : {}, {}", id, series);
        if (series.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, series.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!seriesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Series> result = seriesService.partialUpdate(series);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, series.getId().toString())
        );
    }

    /**
     * {@code GET  /series} : get all the series.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of series in body.
     */
    @GetMapping("/series")
    public ResponseEntity<List<Series>> getAllSeries(SeriesCriteria criteria) {
        log.debug("REST request to get Series by criteria: {}", criteria);
        List<Series> entityList = seriesQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /series/count} : count all the series.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/series/count")
    public ResponseEntity<Long> countSeries(SeriesCriteria criteria) {
        log.debug("REST request to count Series by criteria: {}", criteria);
        return ResponseEntity.ok().body(seriesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /series/:id} : get the "id" series.
     *
     * @param id the id of the series to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the series, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/series/{id}")
    public ResponseEntity<Series> getSeries(@PathVariable Long id) {
        log.debug("REST request to get Series : {}", id);
        Optional<Series> series = seriesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(series);
    }

    /**
     * {@code DELETE  /series/:id} : delete the "id" series.
     *
     * @param id the id of the series to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/series/{id}")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long id) {
        log.debug("REST request to delete Series : {}", id);
        seriesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
