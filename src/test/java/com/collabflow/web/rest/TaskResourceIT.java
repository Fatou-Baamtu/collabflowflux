package com.collabflow.web.rest;

import static com.collabflow.domain.TaskAsserts.*;
import static com.collabflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.collabflow.IntegrationTest;
import com.collabflow.domain.Task;
import com.collabflow.domain.enumeration.Status;
import com.collabflow.repository.EntityManager;
import com.collabflow.repository.TaskRepository;
import com.collabflow.repository.UserRepository;
import com.collabflow.service.dto.TaskDTO;
import com.collabflow.service.mapper.TaskMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.TODO;
    private static final Status UPDATED_STATUS = Status.IN_PROGRESS;

    private static final Instant DEFAULT_DUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_COMPLETED = false;
    private static final Boolean UPDATED_IS_COMPLETED = true;

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Task task;

    private Task insertedTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity() {
        return new Task()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .dueDate(DEFAULT_DUE_DATE)
            .isCompleted(DEFAULT_IS_COMPLETED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity() {
        return new Task()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .isCompleted(UPDATED_IS_COMPLETED);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Task.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        task = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTask != null) {
            taskRepository.delete(insertedTask).block();
            insertedTask = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createTask() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        var returnedTaskDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TaskDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Task in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTask = taskMapper.toEntity(returnedTaskDTO);
        assertTaskUpdatableFieldsEquals(returnedTask, getPersistedTask(returnedTask));

        insertedTask = returnedTask;
    }

    @Test
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId(1L);
        TaskDTO taskDTO = taskMapper.toDto(task);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        task.setTitle(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        task.setStatus(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTasksAsStream() {
        // Initialize the database
        taskRepository.save(task).block();

        List<Task> taskList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TaskDTO.class)
            .getResponseBody()
            .map(taskMapper::toEntity)
            .filter(task::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(taskList).isNotNull();
        assertThat(taskList).hasSize(1);
        Task testTask = taskList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTaskAllPropertiesEquals(task, testTask);
        assertTaskUpdatableFieldsEquals(task, testTask);
    }

    @Test
    void getAllTasks() {
        // Initialize the database
        insertedTask = taskRepository.save(task).block();

        // Get all the taskList
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
            .value(hasItem(task.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].dueDate")
            .value(hasItem(DEFAULT_DUE_DATE.toString()))
            .jsonPath("$.[*].isCompleted")
            .value(hasItem(DEFAULT_IS_COMPLETED.booleanValue()));
    }

    @Test
    void getTask() {
        // Initialize the database
        insertedTask = taskRepository.save(task).block();

        // Get the task
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, task.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(task.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.dueDate")
            .value(is(DEFAULT_DUE_DATE.toString()))
            .jsonPath("$.isCompleted")
            .value(is(DEFAULT_IS_COMPLETED.booleanValue()));
    }

    @Test
    void getNonExistingTask() {
        // Get the task
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTask() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.save(task).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).block();
        updatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .isCompleted(UPDATED_IS_COMPLETED);
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskToMatchAllProperties(updatedTask);
    }

    @Test
    void putNonExistingTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.save(task).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask.description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Task in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTask, task), getPersistedTask(task));
    }

    @Test
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.save(task).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .isCompleted(UPDATED_IS_COMPLETED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Task in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskUpdatableFieldsEquals(partialUpdatedTask, getPersistedTask(partialUpdatedTask));
    }

    @Test
    void patchNonExistingTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, taskDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(taskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTask() {
        // Initialize the database
        insertedTask = taskRepository.save(task).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the task
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, task.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskRepository.count().block();
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

    protected Task getPersistedTask(Task task) {
        return taskRepository.findById(task.getId()).block();
    }

    protected void assertPersistedTaskToMatchAllProperties(Task expectedTask) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTaskAllPropertiesEquals(expectedTask, getPersistedTask(expectedTask));
        assertTaskUpdatableFieldsEquals(expectedTask, getPersistedTask(expectedTask));
    }

    protected void assertPersistedTaskToMatchUpdatableProperties(Task expectedTask) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTaskAllUpdatablePropertiesEquals(expectedTask, getPersistedTask(expectedTask));
        assertTaskUpdatableFieldsEquals(expectedTask, getPersistedTask(expectedTask));
    }
}
