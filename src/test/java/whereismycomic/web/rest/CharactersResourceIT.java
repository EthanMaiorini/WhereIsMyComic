package whereismycomic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import whereismycomic.IntegrationTest;
import whereismycomic.domain.Characters;
import whereismycomic.repository.CharactersRepository;

/**
 * Integration tests for the {@link CharactersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CharactersResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_THUMBNAIL = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_THUMBNAIL = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_THUMBNAIL_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_THUMBNAIL_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/characters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CharactersRepository charactersRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCharactersMockMvc;

    private Characters characters;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Characters createEntity(EntityManager em) {
        Characters characters = new Characters()
            .fullName(DEFAULT_FULL_NAME)
            .description(DEFAULT_DESCRIPTION)
            .thumbnail(DEFAULT_THUMBNAIL)
            .thumbnailContentType(DEFAULT_THUMBNAIL_CONTENT_TYPE);
        return characters;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Characters createUpdatedEntity(EntityManager em) {
        Characters characters = new Characters()
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE);
        return characters;
    }

    @BeforeEach
    public void initTest() {
        characters = createEntity(em);
    }

    @Test
    @Transactional
    void createCharacters() throws Exception {
        int databaseSizeBeforeCreate = charactersRepository.findAll().size();
        // Create the Characters
        restCharactersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(characters)))
            .andExpect(status().isCreated());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeCreate + 1);
        Characters testCharacters = charactersList.get(charactersList.size() - 1);
        assertThat(testCharacters.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testCharacters.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCharacters.getThumbnail()).isEqualTo(DEFAULT_THUMBNAIL);
        assertThat(testCharacters.getThumbnailContentType()).isEqualTo(DEFAULT_THUMBNAIL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createCharactersWithExistingId() throws Exception {
        // Create the Characters with an existing ID
        characters.setId(1L);

        int databaseSizeBeforeCreate = charactersRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCharactersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(characters)))
            .andExpect(status().isBadRequest());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCharacters() throws Exception {
        // Initialize the database
        charactersRepository.saveAndFlush(characters);

        // Get all the charactersList
        restCharactersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(characters.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbnailContentType").value(hasItem(DEFAULT_THUMBNAIL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].thumbnail").value(hasItem(Base64Utils.encodeToString(DEFAULT_THUMBNAIL))));
    }

    @Test
    @Transactional
    void getCharacters() throws Exception {
        // Initialize the database
        charactersRepository.saveAndFlush(characters);

        // Get the characters
        restCharactersMockMvc
            .perform(get(ENTITY_API_URL_ID, characters.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(characters.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.thumbnailContentType").value(DEFAULT_THUMBNAIL_CONTENT_TYPE))
            .andExpect(jsonPath("$.thumbnail").value(Base64Utils.encodeToString(DEFAULT_THUMBNAIL)));
    }

    @Test
    @Transactional
    void getNonExistingCharacters() throws Exception {
        // Get the characters
        restCharactersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCharacters() throws Exception {
        // Initialize the database
        charactersRepository.saveAndFlush(characters);

        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();

        // Update the characters
        Characters updatedCharacters = charactersRepository.findById(characters.getId()).get();
        // Disconnect from session so that the updates on updatedCharacters are not directly saved in db
        em.detach(updatedCharacters);
        updatedCharacters
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE);

        restCharactersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCharacters.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCharacters))
            )
            .andExpect(status().isOk());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
        Characters testCharacters = charactersList.get(charactersList.size() - 1);
        assertThat(testCharacters.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testCharacters.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCharacters.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
        assertThat(testCharacters.getThumbnailContentType()).isEqualTo(UPDATED_THUMBNAIL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingCharacters() throws Exception {
        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();
        characters.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCharactersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, characters.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(characters))
            )
            .andExpect(status().isBadRequest());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCharacters() throws Exception {
        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();
        characters.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCharactersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(characters))
            )
            .andExpect(status().isBadRequest());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCharacters() throws Exception {
        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();
        characters.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCharactersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(characters)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCharactersWithPatch() throws Exception {
        // Initialize the database
        charactersRepository.saveAndFlush(characters);

        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();

        // Update the characters using partial update
        Characters partialUpdatedCharacters = new Characters();
        partialUpdatedCharacters.setId(characters.getId());

        restCharactersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCharacters.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCharacters))
            )
            .andExpect(status().isOk());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
        Characters testCharacters = charactersList.get(charactersList.size() - 1);
        assertThat(testCharacters.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testCharacters.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCharacters.getThumbnail()).isEqualTo(DEFAULT_THUMBNAIL);
        assertThat(testCharacters.getThumbnailContentType()).isEqualTo(DEFAULT_THUMBNAIL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateCharactersWithPatch() throws Exception {
        // Initialize the database
        charactersRepository.saveAndFlush(characters);

        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();

        // Update the characters using partial update
        Characters partialUpdatedCharacters = new Characters();
        partialUpdatedCharacters.setId(characters.getId());

        partialUpdatedCharacters
            .fullName(UPDATED_FULL_NAME)
            .description(UPDATED_DESCRIPTION)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE);

        restCharactersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCharacters.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCharacters))
            )
            .andExpect(status().isOk());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
        Characters testCharacters = charactersList.get(charactersList.size() - 1);
        assertThat(testCharacters.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testCharacters.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCharacters.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
        assertThat(testCharacters.getThumbnailContentType()).isEqualTo(UPDATED_THUMBNAIL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingCharacters() throws Exception {
        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();
        characters.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCharactersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, characters.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(characters))
            )
            .andExpect(status().isBadRequest());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCharacters() throws Exception {
        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();
        characters.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCharactersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(characters))
            )
            .andExpect(status().isBadRequest());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCharacters() throws Exception {
        int databaseSizeBeforeUpdate = charactersRepository.findAll().size();
        characters.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCharactersMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(characters))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Characters in the database
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCharacters() throws Exception {
        // Initialize the database
        charactersRepository.saveAndFlush(characters);

        int databaseSizeBeforeDelete = charactersRepository.findAll().size();

        // Delete the characters
        restCharactersMockMvc
            .perform(delete(ENTITY_API_URL_ID, characters.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Characters> charactersList = charactersRepository.findAll();
        assertThat(charactersList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
