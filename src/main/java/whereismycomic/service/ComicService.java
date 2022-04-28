package whereismycomic.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whereismycomic.domain.Comic;
import whereismycomic.repository.ComicRepository;

/**
 * Service Implementation for managing {@link Comic}.
 */
@Service
@Transactional
public class ComicService {

    private final Logger log = LoggerFactory.getLogger(ComicService.class);

    private final ComicRepository comicRepository;

    public ComicService(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    /**
     * Save a comic.
     *
     * @param comic the entity to save.
     * @return the persisted entity.
     */
    public Comic save(Comic comic) {
        log.debug("Request to save Comic : {}", comic);
        return comicRepository.save(comic);
    }

    /**
     * Update a comic.
     *
     * @param comic the entity to save.
     * @return the persisted entity.
     */
    public Comic update(Comic comic) {
        log.debug("Request to save Comic : {}", comic);
        return comicRepository.save(comic);
    }

    /**
     * Partially update a comic.
     *
     * @param comic the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Comic> partialUpdate(Comic comic) {
        log.debug("Request to partially update Comic : {}", comic);

        return comicRepository
            .findById(comic.getId())
            .map(existingComic -> {
                if (comic.getIssuenumber() != null) {
                    existingComic.setIssuenumber(comic.getIssuenumber());
                }
                if (comic.getLocation() != null) {
                    existingComic.setLocation(comic.getLocation());
                }
                if (comic.getTitle() != null) {
                    existingComic.setTitle(comic.getTitle());
                }
                if (comic.getDescription() != null) {
                    existingComic.setDescription(comic.getDescription());
                }
                if (comic.getThumbnail() != null) {
                    existingComic.setThumbnail(comic.getThumbnail());
                }
                if (comic.getThumbnailContentType() != null) {
                    existingComic.setThumbnailContentType(comic.getThumbnailContentType());
                }

                return existingComic;
            })
            .map(comicRepository::save);
    }

    /**
     * Get all the comics.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Comic> findAll() {
        log.debug("Request to get all Comics");
        return comicRepository.findAll();
    }

    /**
     * Get one comic by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Comic> findOne(Long id) {
        log.debug("Request to get Comic : {}", id);
        return comicRepository.findById(id);
    }

    /**
     * Delete the comic by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Comic : {}", id);
        comicRepository.deleteById(id);
    }
}
