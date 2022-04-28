package whereismycomic.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import whereismycomic.domain.*; // for static metamodels
import whereismycomic.domain.Comic;
import whereismycomic.repository.ComicRepository;
import whereismycomic.service.criteria.ComicCriteria;

/**
 * Service for executing complex queries for {@link Comic} entities in the database.
 * The main input is a {@link ComicCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Comic} or a {@link Page} of {@link Comic} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ComicQueryService extends QueryService<Comic> {

    private final Logger log = LoggerFactory.getLogger(ComicQueryService.class);

    private final ComicRepository comicRepository;

    public ComicQueryService(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    /**
     * Return a {@link List} of {@link Comic} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Comic> findByCriteria(ComicCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Comic> specification = createSpecification(criteria);
        return comicRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Comic} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Comic> findByCriteria(ComicCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Comic> specification = createSpecification(criteria);
        return comicRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ComicCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Comic> specification = createSpecification(criteria);
        return comicRepository.count(specification);
    }

    /**
     * Function to convert {@link ComicCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Comic> createSpecification(ComicCriteria criteria) {
        Specification<Comic> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Comic_.id));
            }
            if (criteria.getIssuenumber() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIssuenumber(), Comic_.issuenumber));
            }
            if (criteria.getLocation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLocation(), Comic_.location));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Comic_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Comic_.description));
            }
            if (criteria.getSeriesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSeriesId(), root -> root.join(Comic_.series, JoinType.LEFT).get(Series_.id))
                    );
            }
        }
        return specification;
    }
}
