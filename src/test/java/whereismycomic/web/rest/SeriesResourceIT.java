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
import whereismycomic.IntegrationTest;
import whereismycomic.domain.Characters;
import whereismycomic.domain.Comic;
import whereismycomic.domain.Series;
import whereismycomic.repository.SeriesRepository;
import whereismycomic.service.criteria.SeriesCriteria;

/**
 * Integration tests for the {@link SeriesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SeriesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/series";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSeriesMockMvc;

    private Series series;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Series createEntity(EntityManager em) {
        Series series = new Series().name(DEFAULT_NAME);
        return series;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Series createUpdatedEntity(EntityManager em) {
        Series series = new Series().name(UPDATED_NAME);
        return series;
    }

    @BeforeEach
    public void initTest() {
        series = createEntity(em);
    }

    @Test
    @Transactional
    void createSeries() throws Exception {
        int databaseSizeBeforeCreate = seriesRepository.findAll().size();
        // Create the Series
        restSeriesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(series)))
            .andExpect(status().isCreated());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeCreate + 1);
        Series testSeries = seriesList.get(seriesList.size() - 1);
        assertThat(testSeries.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createSeriesWithExistingId() throws Exception {
        // Create the Series with an existing ID
        series.setId(1L);

        int databaseSizeBeforeCreate = seriesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSeriesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(series)))
            .andExpect(status().isBadRequest());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSeries() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList
        restSeriesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(series.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getSeries() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get the series
        restSeriesMockMvc
            .perform(get(ENTITY_API_URL_ID, series.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(series.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getSeriesByIdFiltering() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        Long id = series.getId();

        defaultSeriesShouldBeFound("id.equals=" + id);
        defaultSeriesShouldNotBeFound("id.notEquals=" + id);

        defaultSeriesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSeriesShouldNotBeFound("id.greaterThan=" + id);

        defaultSeriesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSeriesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSeriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where name equals to DEFAULT_NAME
        defaultSeriesShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the seriesList where name equals to UPDATED_NAME
        defaultSeriesShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSeriesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where name not equals to DEFAULT_NAME
        defaultSeriesShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the seriesList where name not equals to UPDATED_NAME
        defaultSeriesShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSeriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSeriesShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the seriesList where name equals to UPDATED_NAME
        defaultSeriesShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSeriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where name is not null
        defaultSeriesShouldBeFound("name.specified=true");

        // Get all the seriesList where name is null
        defaultSeriesShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllSeriesByNameContainsSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where name contains DEFAULT_NAME
        defaultSeriesShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the seriesList where name contains UPDATED_NAME
        defaultSeriesShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSeriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where name does not contain DEFAULT_NAME
        defaultSeriesShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the seriesList where name does not contain UPDATED_NAME
        defaultSeriesShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSeriesByComicIsEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);
        Comic comic;
        if (TestUtil.findAll(em, Comic.class).isEmpty()) {
            comic = ComicResourceIT.createEntity(em);
            em.persist(comic);
            em.flush();
        } else {
            comic = TestUtil.findAll(em, Comic.class).get(0);
        }
        em.persist(comic);
        em.flush();
        series.addComic(comic);
        seriesRepository.saveAndFlush(series);
        Long comicId = comic.getId();

        // Get all the seriesList where comic equals to comicId
        defaultSeriesShouldBeFound("comicId.equals=" + comicId);

        // Get all the seriesList where comic equals to (comicId + 1)
        defaultSeriesShouldNotBeFound("comicId.equals=" + (comicId + 1));
    }

    @Test
    @Transactional
    void getAllSeriesByCharactersIsEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);
        Characters characters;
        if (TestUtil.findAll(em, Characters.class).isEmpty()) {
            characters = CharactersResourceIT.createEntity(em);
            em.persist(characters);
            em.flush();
        } else {
            characters = TestUtil.findAll(em, Characters.class).get(0);
        }
        em.persist(characters);
        em.flush();
        series.setCharacters(characters);
        seriesRepository.saveAndFlush(series);
        Long charactersId = characters.getId();

        // Get all the seriesList where characters equals to charactersId
        defaultSeriesShouldBeFound("charactersId.equals=" + charactersId);

        // Get all the seriesList where characters equals to (charactersId + 1)
        defaultSeriesShouldNotBeFound("charactersId.equals=" + (charactersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSeriesShouldBeFound(String filter) throws Exception {
        restSeriesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(series.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restSeriesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSeriesShouldNotBeFound(String filter) throws Exception {
        restSeriesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSeriesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSeries() throws Exception {
        // Get the series
        restSeriesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSeries() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();

        // Update the series
        Series updatedSeries = seriesRepository.findById(series.getId()).get();
        // Disconnect from session so that the updates on updatedSeries are not directly saved in db
        em.detach(updatedSeries);
        updatedSeries.name(UPDATED_NAME);

        restSeriesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSeries.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSeries))
            )
            .andExpect(status().isOk());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
        Series testSeries = seriesList.get(seriesList.size() - 1);
        assertThat(testSeries.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingSeries() throws Exception {
        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();
        series.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeriesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, series.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(series))
            )
            .andExpect(status().isBadRequest());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSeries() throws Exception {
        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();
        series.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeriesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(series))
            )
            .andExpect(status().isBadRequest());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSeries() throws Exception {
        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();
        series.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeriesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(series)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSeriesWithPatch() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();

        // Update the series using partial update
        Series partialUpdatedSeries = new Series();
        partialUpdatedSeries.setId(series.getId());

        restSeriesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeries.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSeries))
            )
            .andExpect(status().isOk());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
        Series testSeries = seriesList.get(seriesList.size() - 1);
        assertThat(testSeries.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateSeriesWithPatch() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();

        // Update the series using partial update
        Series partialUpdatedSeries = new Series();
        partialUpdatedSeries.setId(series.getId());

        partialUpdatedSeries.name(UPDATED_NAME);

        restSeriesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeries.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSeries))
            )
            .andExpect(status().isOk());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
        Series testSeries = seriesList.get(seriesList.size() - 1);
        assertThat(testSeries.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingSeries() throws Exception {
        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();
        series.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeriesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, series.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(series))
            )
            .andExpect(status().isBadRequest());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSeries() throws Exception {
        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();
        series.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeriesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(series))
            )
            .andExpect(status().isBadRequest());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSeries() throws Exception {
        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();
        series.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeriesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(series)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSeries() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        int databaseSizeBeforeDelete = seriesRepository.findAll().size();

        // Delete the series
        restSeriesMockMvc
            .perform(delete(ENTITY_API_URL_ID, series.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
