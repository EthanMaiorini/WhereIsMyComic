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
import whereismycomic.domain.Characters;
import whereismycomic.repository.CharactersRepository;
import whereismycomic.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link whereismycomic.domain.Characters}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CharactersResource {

    private final Logger log = LoggerFactory.getLogger(CharactersResource.class);

    private static final String ENTITY_NAME = "characters";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CharactersRepository charactersRepository;

    public CharactersResource(CharactersRepository charactersRepository) {
        this.charactersRepository = charactersRepository;
    }

    /**
     * {@code POST  /characters} : Create a new characters.
     *
     * @param characters the characters to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new characters, or with status {@code 400 (Bad Request)} if the characters has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/characters")
    public ResponseEntity<Characters> createCharacters(@RequestBody Characters characters) throws URISyntaxException {
        log.debug("REST request to save Characters : {}", characters);
        if (characters.getId() != null) {
            throw new BadRequestAlertException("A new characters cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Characters result = charactersRepository.save(characters);
        return ResponseEntity
            .created(new URI("/api/characters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /characters/:id} : Updates an existing characters.
     *
     * @param id the id of the characters to save.
     * @param characters the characters to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated characters,
     * or with status {@code 400 (Bad Request)} if the characters is not valid,
     * or with status {@code 500 (Internal Server Error)} if the characters couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/characters/{id}")
    public ResponseEntity<Characters> updateCharacters(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Characters characters
    ) throws URISyntaxException {
        log.debug("REST request to update Characters : {}, {}", id, characters);
        if (characters.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, characters.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!charactersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Characters result = charactersRepository.save(characters);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, characters.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /characters/:id} : Partial updates given fields of an existing characters, field will ignore if it is null
     *
     * @param id the id of the characters to save.
     * @param characters the characters to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated characters,
     * or with status {@code 400 (Bad Request)} if the characters is not valid,
     * or with status {@code 404 (Not Found)} if the characters is not found,
     * or with status {@code 500 (Internal Server Error)} if the characters couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/characters/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Characters> partialUpdateCharacters(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Characters characters
    ) throws URISyntaxException {
        log.debug("REST request to partial update Characters partially : {}, {}", id, characters);
        if (characters.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, characters.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!charactersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Characters> result = charactersRepository
            .findById(characters.getId())
            .map(existingCharacters -> {
                if (characters.getFullName() != null) {
                    existingCharacters.setFullName(characters.getFullName());
                }
                if (characters.getDescription() != null) {
                    existingCharacters.setDescription(characters.getDescription());
                }
                if (characters.getThumbnail() != null) {
                    existingCharacters.setThumbnail(characters.getThumbnail());
                }
                if (characters.getThumbnailContentType() != null) {
                    existingCharacters.setThumbnailContentType(characters.getThumbnailContentType());
                }

                return existingCharacters;
            })
            .map(charactersRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, characters.getId().toString())
        );
    }

    /**
     * {@code GET  /characters} : get all the characters.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of characters in body.
     */
    @GetMapping("/characters")
    public List<Characters> getAllCharacters() {
        log.debug("REST request to get all Characters");
        return charactersRepository.findAll();
    }

    /**
     * {@code GET  /characters/:id} : get the "id" characters.
     *
     * @param id the id of the characters to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the characters, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/characters/{id}")
    public ResponseEntity<Characters> getCharacters(@PathVariable Long id) {
        log.debug("REST request to get Characters : {}", id);
        Optional<Characters> characters = charactersRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(characters);
    }

    /**
     * {@code DELETE  /characters/:id} : delete the "id" characters.
     *
     * @param id the id of the characters to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/characters/{id}")
    public ResponseEntity<Void> deleteCharacters(@PathVariable Long id) {
        log.debug("REST request to delete Characters : {}", id);
        charactersRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
