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
import whereismycomic.domain.Comic;
import whereismycomic.repository.ComicRepository;

/**
 * Integration tests for the {@link ComicResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ComicResourceIT {

    private static final Integer DEFAULT_ISSUE_NUMBER = 1;
    private static final Integer UPDATED_ISSUE_NUMBER = 2;

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_THUMBNAIL = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_THUMBNAIL = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_THUMBNAIL_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_THUMBNAIL_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/comics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restComicMockMvc;

    private Comic comic;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comic createEntity(EntityManager em) {
        Comic comic = new Comic()
            .issueNumber(DEFAULT_ISSUE_NUMBER)
            .location(DEFAULT_LOCATION)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .thumbnail(DEFAULT_THUMBNAIL)
            .thumbnailContentType(DEFAULT_THUMBNAIL_CONTENT_TYPE);
        return comic;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comic createUpdatedEntity(EntityManager em) {
        Comic comic = new Comic()
            .issueNumber(UPDATED_ISSUE_NUMBER)
            .location(UPDATED_LOCATION)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE);
        return comic;
    }

    @BeforeEach
    public void initTest() {
        comic = createEntity(em);
    }

    @Test
    @Transactional
    void createComic() throws Exception {
        int databaseSizeBeforeCreate = comicRepository.findAll().size();
        // Create the Comic
        restComicMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(comic)))
            .andExpect(status().isCreated());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeCreate + 1);
        Comic testComic = comicList.get(comicList.size() - 1);
        assertThat(testComic.getIssueNumber()).isEqualTo(DEFAULT_ISSUE_NUMBER);
        assertThat(testComic.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testComic.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testComic.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testComic.getThumbnail()).isEqualTo(DEFAULT_THUMBNAIL);
        assertThat(testComic.getThumbnailContentType()).isEqualTo(DEFAULT_THUMBNAIL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createComicWithExistingId() throws Exception {
        // Create the Comic with an existing ID
        comic.setId(1L);

        int databaseSizeBeforeCreate = comicRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComicMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(comic)))
            .andExpect(status().isBadRequest());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllComics() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList
        restComicMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comic.getId().intValue())))
            .andExpect(jsonPath("$.[*].issueNumber").value(hasItem(DEFAULT_ISSUE_NUMBER)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbnailContentType").value(hasItem(DEFAULT_THUMBNAIL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].thumbnail").value(hasItem(Base64Utils.encodeToString(DEFAULT_THUMBNAIL))));
    }

    @Test
    @Transactional
    void getComic() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get the comic
        restComicMockMvc
            .perform(get(ENTITY_API_URL_ID, comic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(comic.getId().intValue()))
            .andExpect(jsonPath("$.issueNumber").value(DEFAULT_ISSUE_NUMBER))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.thumbnailContentType").value(DEFAULT_THUMBNAIL_CONTENT_TYPE))
            .andExpect(jsonPath("$.thumbnail").value(Base64Utils.encodeToString(DEFAULT_THUMBNAIL)));
    }

    @Test
    @Transactional
    void getNonExistingComic() throws Exception {
        // Get the comic
        restComicMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewComic() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        int databaseSizeBeforeUpdate = comicRepository.findAll().size();

        // Update the comic
        Comic updatedComic = comicRepository.findById(comic.getId()).get();
        // Disconnect from session so that the updates on updatedComic are not directly saved in db
        em.detach(updatedComic);
        updatedComic
            .issueNumber(UPDATED_ISSUE_NUMBER)
            .location(UPDATED_LOCATION)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE);

        restComicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedComic.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedComic))
            )
            .andExpect(status().isOk());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
        Comic testComic = comicList.get(comicList.size() - 1);
        assertThat(testComic.getIssueNumber()).isEqualTo(UPDATED_ISSUE_NUMBER);
        assertThat(testComic.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testComic.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testComic.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testComic.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
        assertThat(testComic.getThumbnailContentType()).isEqualTo(UPDATED_THUMBNAIL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingComic() throws Exception {
        int databaseSizeBeforeUpdate = comicRepository.findAll().size();
        comic.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, comic.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(comic))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComic() throws Exception {
        int databaseSizeBeforeUpdate = comicRepository.findAll().size();
        comic.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(comic))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComic() throws Exception {
        int databaseSizeBeforeUpdate = comicRepository.findAll().size();
        comic.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComicMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(comic)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateComicWithPatch() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        int databaseSizeBeforeUpdate = comicRepository.findAll().size();

        // Update the comic using partial update
        Comic partialUpdatedComic = new Comic();
        partialUpdatedComic.setId(comic.getId());

        partialUpdatedComic.description(UPDATED_DESCRIPTION);

        restComicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComic.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComic))
            )
            .andExpect(status().isOk());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
        Comic testComic = comicList.get(comicList.size() - 1);
        assertThat(testComic.getIssueNumber()).isEqualTo(DEFAULT_ISSUE_NUMBER);
        assertThat(testComic.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testComic.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testComic.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testComic.getThumbnail()).isEqualTo(DEFAULT_THUMBNAIL);
        assertThat(testComic.getThumbnailContentType()).isEqualTo(DEFAULT_THUMBNAIL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateComicWithPatch() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        int databaseSizeBeforeUpdate = comicRepository.findAll().size();

        // Update the comic using partial update
        Comic partialUpdatedComic = new Comic();
        partialUpdatedComic.setId(comic.getId());

        partialUpdatedComic
            .issueNumber(UPDATED_ISSUE_NUMBER)
            .location(UPDATED_LOCATION)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE);

        restComicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComic.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComic))
            )
            .andExpect(status().isOk());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
        Comic testComic = comicList.get(comicList.size() - 1);
        assertThat(testComic.getIssueNumber()).isEqualTo(UPDATED_ISSUE_NUMBER);
        assertThat(testComic.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testComic.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testComic.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testComic.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
        assertThat(testComic.getThumbnailContentType()).isEqualTo(UPDATED_THUMBNAIL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingComic() throws Exception {
        int databaseSizeBeforeUpdate = comicRepository.findAll().size();
        comic.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, comic.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(comic))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComic() throws Exception {
        int databaseSizeBeforeUpdate = comicRepository.findAll().size();
        comic.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(comic))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComic() throws Exception {
        int databaseSizeBeforeUpdate = comicRepository.findAll().size();
        comic.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComicMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(comic)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Comic in the database
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComic() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        int databaseSizeBeforeDelete = comicRepository.findAll().size();

        // Delete the comic
        restComicMockMvc
            .perform(delete(ENTITY_API_URL_ID, comic.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Comic> comicList = comicRepository.findAll();
        assertThat(comicList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
