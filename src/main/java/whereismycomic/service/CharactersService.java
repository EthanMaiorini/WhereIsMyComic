package whereismycomic.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whereismycomic.domain.Characters;
import whereismycomic.repository.CharactersRepository;

/**
 * Service Implementation for managing {@link Characters}.
 */
@Service
@Transactional
public class CharactersService {

    private final Logger log = LoggerFactory.getLogger(CharactersService.class);

    private final CharactersRepository charactersRepository;

    public CharactersService(CharactersRepository charactersRepository) {
        this.charactersRepository = charactersRepository;
    }

    /**
     * Save a characters.
     *
     * @param characters the entity to save.
     * @return the persisted entity.
     */
    public Characters save(Characters characters) {
        log.debug("Request to save Characters : {}", characters);
        return charactersRepository.save(characters);
    }

    /**
     * Update a characters.
     *
     * @param characters the entity to save.
     * @return the persisted entity.
     */
    public Characters update(Characters characters) {
        log.debug("Request to save Characters : {}", characters);
        return charactersRepository.save(characters);
    }

    /**
     * Partially update a characters.
     *
     * @param characters the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Characters> partialUpdate(Characters characters) {
        log.debug("Request to partially update Characters : {}", characters);

        return charactersRepository
            .findById(characters.getId())
            .map(existingCharacters -> {
                if (characters.getFullname() != null) {
                    existingCharacters.setFullname(characters.getFullname());
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
    }

    /**
     * Get all the characters.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Characters> findAll() {
        log.debug("Request to get all Characters");
        return charactersRepository.findAll();
    }

    /**
     * Get one characters by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Characters> findOne(Long id) {
        log.debug("Request to get Characters : {}", id);
        return charactersRepository.findById(id);
    }

    /**
     * Delete the characters by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Characters : {}", id);
        charactersRepository.deleteById(id);
    }
}
