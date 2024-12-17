package com.collabflow.web.rest;

import static com.collabflow.domain.ProjectAsserts.*;
import static com.collabflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.collabflow.IntegrationTest;
import com.collabflow.domain.Project;
import com.collabflow.domain.enumeration.Priority;
import com.collabflow.repository.EntityManager;
import com.collabflow.repository.ProjectRepository;
import com.collabflow.service.dto.ProjectDTO;
import com.collabflow.service.mapper.ProjectMapper;
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
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Priority DEFAULT_PRIORITY = Priority.URGENT;
    private static final Priority UPDATED_PRIORITY = Priority.NORMAL;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Project project;

    private Project insertedProject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createEntity() {
        return new Project()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .priority(DEFAULT_PRIORITY)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createUpdatedEntity() {
        return new Project()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Project.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        project = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProject != null) {
            projectRepository.delete(insertedProject).block();
            insertedProject = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);
        var returnedProjectDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProjectDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Project in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProject = projectMapper.toEntity(returnedProjectDTO);
        assertProjectUpdatableFieldsEquals(returnedProject, getPersistedProject(returnedProject));

        insertedProject = returnedProject;
    }

    @Test
    void createProjectWithExistingId() throws Exception {
        // Create the Project with an existing ID
        project.setId(1L);
        ProjectDTO projectDTO = projectMapper.toDto(project);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setName(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPriorityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setPriority(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProjects() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList
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
            .value(hasItem(project.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].priority")
            .value(hasItem(DEFAULT_PRIORITY.toString()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getProject() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get the project
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, project.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(project.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.priority")
            .value(is(DEFAULT_PRIORITY.toString()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getNonExistingProject() {
        // Get the project
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).block();
        updatedProject
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        ProjectDTO projectDTO = projectMapper.toDto(updatedProject);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectToMatchAllProperties(updatedProject);
    }

    @Test
    void putNonExistingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject.priority(UPDATED_PRIORITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProject, project), getPersistedProject(project));
    }

    @Test
    void fullUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectUpdatableFieldsEquals(partialUpdatedProject, getPersistedProject(partialUpdatedProject));
    }

    @Test
    void patchNonExistingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProject() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the project
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, project.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectRepository.count().block();
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

    protected Project getPersistedProject(Project project) {
        return projectRepository.findById(project.getId()).block();
    }

    protected void assertPersistedProjectToMatchAllProperties(Project expectedProject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProjectAllPropertiesEquals(expectedProject, getPersistedProject(expectedProject));
        assertProjectUpdatableFieldsEquals(expectedProject, getPersistedProject(expectedProject));
    }

    protected void assertPersistedProjectToMatchUpdatableProperties(Project expectedProject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProjectAllUpdatablePropertiesEquals(expectedProject, getPersistedProject(expectedProject));
        assertProjectUpdatableFieldsEquals(expectedProject, getPersistedProject(expectedProject));
    }
}
