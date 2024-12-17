package com.collabflow.web.rest;

import static com.collabflow.domain.SubTaskAsserts.*;
import static com.collabflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.collabflow.IntegrationTest;
import com.collabflow.domain.SubTask;
import com.collabflow.domain.enumeration.Status;
import com.collabflow.repository.EntityManager;
import com.collabflow.repository.SubTaskRepository;
import com.collabflow.repository.UserRepository;
import com.collabflow.service.dto.SubTaskDTO;
import com.collabflow.service.mapper.SubTaskMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link SubTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SubTaskResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.TODO;
    private static final Status UPDATED_STATUS = Status.IN_PROGRESS;

    private static final Instant DEFAULT_DUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/sub-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubTaskRepository subTaskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubTaskMapper subTaskMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private SubTask subTask;

    private SubTask insertedSubTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubTask createEntity() {
        return new SubTask().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION).status(DEFAULT_STATUS).dueDate(DEFAULT_DUE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubTask createUpdatedEntity() {
        return new SubTask().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).dueDate(UPDATED_DUE_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(SubTask.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        subTask = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSubTask != null) {
            subTaskRepository.delete(insertedSubTask).block();
            insertedSubTask = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createSubTask() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);
        var returnedSubTaskDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SubTaskDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the SubTask in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubTask = subTaskMapper.toEntity(returnedSubTaskDTO);
        assertSubTaskUpdatableFieldsEquals(returnedSubTask, getPersistedSubTask(returnedSubTask));

        insertedSubTask = returnedSubTask;
    }

    @Test
    void createSubTaskWithExistingId() throws Exception {
        // Create the SubTask with an existing ID
        subTask.setId(1L);
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subTask.setTitle(null);

        // Create the SubTask, which fails.
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subTask.setStatus(null);

        // Create the SubTask, which fails.
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSubTasks() {
        // Initialize the database
        insertedSubTask = subTaskRepository.save(subTask).block();

        // Get all the subTaskList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(subTask.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].dueDate")
            .value(hasItem(DEFAULT_DUE_DATE.toString()));
    }

    @Test
    void getSubTask() {
        // Initialize the database
        insertedSubTask = subTaskRepository.save(subTask).block();

        // Get the subTask
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, subTask.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(subTask.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.dueDate")
            .value(is(DEFAULT_DUE_DATE.toString()));
    }

    @Test
    void getNonExistingSubTask() {
        // Get the subTask
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSubTask() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.save(subTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subTask
        SubTask updatedSubTask = subTaskRepository.findById(subTask.getId()).block();
        updatedSubTask.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).dueDate(UPDATED_DUE_DATE);
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(updatedSubTask);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, subTaskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubTaskToMatchAllProperties(updatedSubTask);
    }

    @Test
    void putNonExistingSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, subTaskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSubTaskWithPatch() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.save(subTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subTask using partial update
        SubTask partialUpdatedSubTask = new SubTask();
        partialUpdatedSubTask.setId(subTask.getId());

        partialUpdatedSubTask.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubTaskUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSubTask, subTask), getPersistedSubTask(subTask));
    }

    @Test
    void fullUpdateSubTaskWithPatch() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.save(subTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subTask using partial update
        SubTask partialUpdatedSubTask = new SubTask();
        partialUpdatedSubTask.setId(subTask.getId());

        partialUpdatedSubTask.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).dueDate(UPDATED_DUE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubTaskUpdatableFieldsEquals(partialUpdatedSubTask, getPersistedSubTask(partialUpdatedSubTask));
    }

    @Test
    void patchNonExistingSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, subTaskDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subTaskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSubTask() {
        // Initialize the database
        insertedSubTask = subTaskRepository.save(subTask).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subTask
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, subTask.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subTaskRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected SubTask getPersistedSubTask(SubTask subTask) {
        return subTaskRepository.findById(subTask.getId()).block();
    }

    protected void assertPersistedSubTaskToMatchAllProperties(SubTask expectedSubTask) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubTaskAllPropertiesEquals(expectedSubTask, getPersistedSubTask(expectedSubTask));
        assertSubTaskUpdatableFieldsEquals(expectedSubTask, getPersistedSubTask(expectedSubTask));
    }

    protected void assertPersistedSubTaskToMatchUpdatableProperties(SubTask expectedSubTask) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubTaskAllUpdatablePropertiesEquals(expectedSubTask, getPersistedSubTask(expectedSubTask));
        assertSubTaskUpdatableFieldsEquals(expectedSubTask, getPersistedSubTask(expectedSubTask));
    }
}
