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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import whereismycomic.domain.Comic;
import whereismycomic.repository.ComicRepository;
import whereismycomic.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link whereismycomic.domain.Comic}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ComicResource {

    private final Logger log = LoggerFactory.getLogger(ComicResource.class);

    private static final String ENTITY_NAME = "comic";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ComicRepository comicRepository;

    public ComicResource(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    /**
     * {@code POST  /comics} : Create a new comic.
     *
     * @param comic the comic to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new comic, or with status {@code 400 (Bad Request)} if the comic has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/comics")
    public ResponseEntity<Comic> createComic(@RequestBody Comic comic) throws URISyntaxException {
        log.debug("REST request to save Comic : {}", comic);
        if (comic.getId() != null) {
            throw new BadRequestAlertException("A new comic cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Comic result = comicRepository.save(comic);
        return ResponseEntity
            .created(new URI("/api/comics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /comics/:id} : Updates an existing comic.
     *
     * @param id the id of the comic to save.
     * @param comic the comic to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated comic,
     * or with status {@code 400 (Bad Request)} if the comic is not valid,
     * or with status {@code 500 (Internal Server Error)} if the comic couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/comics/{id}")
    public ResponseEntity<Comic> updateComic(@PathVariable(value = "id", required = false) final Long id, @RequestBody Comic comic)
        throws URISyntaxException {
        log.debug("REST request to update Comic : {}, {}", id, comic);
        if (comic.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, comic.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!comicRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Comic result = comicRepository.save(comic);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, comic.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /comics/:id} : Partial updates given fields of an existing comic, field will ignore if it is null
     *
     * @param id the id of the comic to save.
     * @param comic the comic to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated comic,
     * or with status {@code 400 (Bad Request)} if the comic is not valid,
     * or with status {@code 404 (Not Found)} if the comic is not found,
     * or with status {@code 500 (Internal Server Error)} if the comic couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/comics/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Comic> partialUpdateComic(@PathVariable(value = "id", required = false) final Long id, @RequestBody Comic comic)
        throws URISyntaxException {
        log.debug("REST request to partial update Comic partially : {}, {}", id, comic);
        if (comic.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, comic.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!comicRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Comic> result = comicRepository
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

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, comic.getId().toString())
        );
    }

    /**
     * {@code GET  /comics} : get all the comics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of comics in body.
     */
    @GetMapping("/comics")
    public List<Comic> getAllComics() {
        log.debug("REST request to get all Comics");
        return comicRepository.findAll();
    }

    /**
     * {@code GET  /comics/:id} : get the "id" comic.
     *
     * @param id the id of the comic to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the comic, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/comics/{id}")
    public ResponseEntity<Comic> getComic(@PathVariable Long id) {
        log.debug("REST request to get Comic : {}", id);
        Optional<Comic> comic = comicRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(comic);
    }

    /**
     * {@code DELETE  /comics/:id} : delete the "id" comic.
     *
     * @param id the id of the comic to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/comics/{id}")
    public ResponseEntity<Void> deleteComic(@PathVariable Long id) {
        log.debug("REST request to delete Comic : {}", id);
        comicRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
