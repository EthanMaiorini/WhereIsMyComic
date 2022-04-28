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
import whereismycomic.domain.Series;
import whereismycomic.repository.ComicRepository;
import whereismycomic.service.criteria.ComicCriteria;

/**
 * Integration tests for the {@link ComicResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ComicResourceIT {

    private static final Integer DEFAULT_ISSUENUMBER = 1;
    private static final Integer UPDATED_ISSUENUMBER = 2;
    private static final Integer SMALLER_ISSUENUMBER = 1 - 1;

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
            .issuenumber(DEFAULT_ISSUENUMBER)
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
            .issuenumber(UPDATED_ISSUENUMBER)
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
        assertThat(testComic.getIssuenumber()).isEqualTo(DEFAULT_ISSUENUMBER);
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
            .andExpect(jsonPath("$.[*].issuenumber").value(hasItem(DEFAULT_ISSUENUMBER)))
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
            .andExpect(jsonPath("$.issuenumber").value(DEFAULT_ISSUENUMBER))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.thumbnailContentType").value(DEFAULT_THUMBNAIL_CONTENT_TYPE))
            .andExpect(jsonPath("$.thumbnail").value(Base64Utils.encodeToString(DEFAULT_THUMBNAIL)));
    }

    @Test
    @Transactional
    void getComicsByIdFiltering() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        Long id = comic.getId();

        defaultComicShouldBeFound("id.equals=" + id);
        defaultComicShouldNotBeFound("id.notEquals=" + id);

        defaultComicShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultComicShouldNotBeFound("id.greaterThan=" + id);

        defaultComicShouldBeFound("id.lessThanOrEqual=" + id);
        defaultComicShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllComicsByIssuenumberIsEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where issuenumber equals to DEFAULT_ISSUENUMBER
        defaultComicShouldBeFound("issuenumber.equals=" + DEFAULT_ISSUENUMBER);

        // Get all the comicList where issuenumber equals to UPDATED_ISSUENUMBER
        defaultComicShouldNotBeFound("issuenumber.equals=" + UPDATED_ISSUENUMBER);
    }

    @Test
    @Transactional
    void getAllComicsByIssuenumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where issuenumber not equals to DEFAULT_ISSUENUMBER
        defaultComicShouldNotBeFound("issuenumber.notEquals=" + DEFAULT_ISSUENUMBER);

        // Get all the comicList where issuenumber not equals to UPDATED_ISSUENUMBER
        defaultComicShouldBeFound("issuenumber.notEquals=" + UPDATED_ISSUENUMBER);
    }

    @Test
    @Transactional
    void getAllComicsByIssuenumberIsInShouldWork() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where issuenumber in DEFAULT_ISSUENUMBER or UPDATED_ISSUENUMBER
        defaultComicShouldBeFound("issuenumber.in=" + DEFAULT_ISSUENUMBER + "," + UPDATED_ISSUENUMBER);

        // Get all the comicList where issuenumber equals to UPDATED_ISSUENUMBER
        defaultComicShouldNotBeFound("issuenumber.in=" + UPDATED_ISSUENUMBER);
    }

    @Test
    @Transactional
    void getAllComicsByIssuenumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where issuenumber is not null
        defaultComicShouldBeFound("issuenumber.specified=true");

        // Get all the comicList where issuenumber is null
        defaultComicShouldNotBeFound("issuenumber.specified=false");
    }

    @Test
    @Transactional
    void getAllComicsByIssuenumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where issuenumber is greater than or equal to DEFAULT_ISSUENUMBER
        defaultComicShouldBeFound("issuenumber.greaterThanOrEqual=" + DEFAULT_ISSUENUMBER);

        // Get all the comicList where issuenumber is greater than or equal to UPDATED_ISSUENUMBER
        defaultComicShouldNotBeFound("issuenumber.greaterThanOrEqual=" + UPDATED_ISSUENUMBER);
    }

    @Test
    @Transactional
    void getAllComicsByIssuenumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where issuenumber is less than or equal to DEFAULT_ISSUENUMBER
        defaultComicShouldBeFound("issuenumber.lessThanOrEqual=" + DEFAULT_ISSUENUMBER);

        // Get all the comicList where issuenumber is less than or equal to SMALLER_ISSUENUMBER
        defaultComicShouldNotBeFound("issuenumber.lessThanOrEqual=" + SMALLER_ISSUENUMBER);
    }

    @Test
    @Transactional
    void getAllComicsByIssuenumberIsLessThanSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where issuenumber is less than DEFAULT_ISSUENUMBER
        defaultComicShouldNotBeFound("issuenumber.lessThan=" + DEFAULT_ISSUENUMBER);

        // Get all the comicList where issuenumber is less than UPDATED_ISSUENUMBER
        defaultComicShouldBeFound("issuenumber.lessThan=" + UPDATED_ISSUENUMBER);
    }

    @Test
    @Transactional
    void getAllComicsByIssuenumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where issuenumber is greater than DEFAULT_ISSUENUMBER
        defaultComicShouldNotBeFound("issuenumber.greaterThan=" + DEFAULT_ISSUENUMBER);

        // Get all the comicList where issuenumber is greater than SMALLER_ISSUENUMBER
        defaultComicShouldBeFound("issuenumber.greaterThan=" + SMALLER_ISSUENUMBER);
    }

    @Test
    @Transactional
    void getAllComicsByLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where location equals to DEFAULT_LOCATION
        defaultComicShouldBeFound("location.equals=" + DEFAULT_LOCATION);

        // Get all the comicList where location equals to UPDATED_LOCATION
        defaultComicShouldNotBeFound("location.equals=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllComicsByLocationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where location not equals to DEFAULT_LOCATION
        defaultComicShouldNotBeFound("location.notEquals=" + DEFAULT_LOCATION);

        // Get all the comicList where location not equals to UPDATED_LOCATION
        defaultComicShouldBeFound("location.notEquals=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllComicsByLocationIsInShouldWork() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where location in DEFAULT_LOCATION or UPDATED_LOCATION
        defaultComicShouldBeFound("location.in=" + DEFAULT_LOCATION + "," + UPDATED_LOCATION);

        // Get all the comicList where location equals to UPDATED_LOCATION
        defaultComicShouldNotBeFound("location.in=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllComicsByLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where location is not null
        defaultComicShouldBeFound("location.specified=true");

        // Get all the comicList where location is null
        defaultComicShouldNotBeFound("location.specified=false");
    }

    @Test
    @Transactional
    void getAllComicsByLocationContainsSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where location contains DEFAULT_LOCATION
        defaultComicShouldBeFound("location.contains=" + DEFAULT_LOCATION);

        // Get all the comicList where location contains UPDATED_LOCATION
        defaultComicShouldNotBeFound("location.contains=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllComicsByLocationNotContainsSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where location does not contain DEFAULT_LOCATION
        defaultComicShouldNotBeFound("location.doesNotContain=" + DEFAULT_LOCATION);

        // Get all the comicList where location does not contain UPDATED_LOCATION
        defaultComicShouldBeFound("location.doesNotContain=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllComicsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where title equals to DEFAULT_TITLE
        defaultComicShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the comicList where title equals to UPDATED_TITLE
        defaultComicShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllComicsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where title not equals to DEFAULT_TITLE
        defaultComicShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the comicList where title not equals to UPDATED_TITLE
        defaultComicShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllComicsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultComicShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the comicList where title equals to UPDATED_TITLE
        defaultComicShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllComicsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where title is not null
        defaultComicShouldBeFound("title.specified=true");

        // Get all the comicList where title is null
        defaultComicShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllComicsByTitleContainsSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where title contains DEFAULT_TITLE
        defaultComicShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the comicList where title contains UPDATED_TITLE
        defaultComicShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllComicsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where title does not contain DEFAULT_TITLE
        defaultComicShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the comicList where title does not contain UPDATED_TITLE
        defaultComicShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllComicsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where description equals to DEFAULT_DESCRIPTION
        defaultComicShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the comicList where description equals to UPDATED_DESCRIPTION
        defaultComicShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllComicsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where description not equals to DEFAULT_DESCRIPTION
        defaultComicShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the comicList where description not equals to UPDATED_DESCRIPTION
        defaultComicShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllComicsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultComicShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the comicList where description equals to UPDATED_DESCRIPTION
        defaultComicShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllComicsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where description is not null
        defaultComicShouldBeFound("description.specified=true");

        // Get all the comicList where description is null
        defaultComicShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllComicsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where description contains DEFAULT_DESCRIPTION
        defaultComicShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the comicList where description contains UPDATED_DESCRIPTION
        defaultComicShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllComicsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);

        // Get all the comicList where description does not contain DEFAULT_DESCRIPTION
        defaultComicShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the comicList where description does not contain UPDATED_DESCRIPTION
        defaultComicShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllComicsBySeriesIsEqualToSomething() throws Exception {
        // Initialize the database
        comicRepository.saveAndFlush(comic);
        Series series;
        if (TestUtil.findAll(em, Series.class).isEmpty()) {
            series = SeriesResourceIT.createEntity(em);
            em.persist(series);
            em.flush();
        } else {
            series = TestUtil.findAll(em, Series.class).get(0);
        }
        em.persist(series);
        em.flush();
        comic.setSeries(series);
        comicRepository.saveAndFlush(comic);
        Long seriesId = series.getId();

        // Get all the comicList where series equals to seriesId
        defaultComicShouldBeFound("seriesId.equals=" + seriesId);

        // Get all the comicList where series equals to (seriesId + 1)
        defaultComicShouldNotBeFound("seriesId.equals=" + (seriesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultComicShouldBeFound(String filter) throws Exception {
        restComicMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comic.getId().intValue())))
            .andExpect(jsonPath("$.[*].issuenumber").value(hasItem(DEFAULT_ISSUENUMBER)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbnailContentType").value(hasItem(DEFAULT_THUMBNAIL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].thumbnail").value(hasItem(Base64Utils.encodeToString(DEFAULT_THUMBNAIL))));

        // Check, that the count call also returns 1
        restComicMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultComicShouldNotBeFound(String filter) throws Exception {
        restComicMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restComicMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
            .issuenumber(UPDATED_ISSUENUMBER)
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
        assertThat(testComic.getIssuenumber()).isEqualTo(UPDATED_ISSUENUMBER);
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
        assertThat(testComic.getIssuenumber()).isEqualTo(DEFAULT_ISSUENUMBER);
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
            .issuenumber(UPDATED_ISSUENUMBER)
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
        assertThat(testComic.getIssuenumber()).isEqualTo(UPDATED_ISSUENUMBER);
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
