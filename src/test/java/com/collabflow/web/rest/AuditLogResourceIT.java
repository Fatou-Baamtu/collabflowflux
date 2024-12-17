package com.collabflow.web.rest;

import static com.collabflow.domain.AuditLogAsserts.*;
import static com.collabflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.collabflow.IntegrationTest;
import com.collabflow.domain.AuditLog;
import com.collabflow.domain.enumeration.ActionEnum;
import com.collabflow.domain.enumeration.EntityEnum;
import com.collabflow.repository.AuditLogRepository;
import com.collabflow.repository.EntityManager;
import com.collabflow.repository.UserRepository;
import com.collabflow.service.dto.AuditLogDTO;
import com.collabflow.service.mapper.AuditLogMapper;
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
 * Integration tests for the {@link AuditLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuditLogResourceIT {

    private static final EntityEnum DEFAULT_ENTITY = EntityEnum.PROJECT;
    private static final EntityEnum UPDATED_ENTITY = EntityEnum.TASK;

    private static final ActionEnum DEFAULT_ACTION = ActionEnum.CREATED;
    private static final ActionEnum UPDATED_ACTION = ActionEnum.UPDATED;

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_ENTITY_ID = 1L;
    private static final Long UPDATED_ENTITY_ID = 2L;

    private static final String ENTITY_API_URL = "/api/audit-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AuditLog auditLog;

    private AuditLog insertedAuditLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditLog createEntity() {
        return new AuditLog().entity(DEFAULT_ENTITY).action(DEFAULT_ACTION).timestamp(DEFAULT_TIMESTAMP).entityId(DEFAULT_ENTITY_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditLog createUpdatedEntity() {
        return new AuditLog().entity(UPDATED_ENTITY).action(UPDATED_ACTION).timestamp(UPDATED_TIMESTAMP).entityId(UPDATED_ENTITY_ID);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AuditLog.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        auditLog = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAuditLog != null) {
            auditLogRepository.delete(insertedAuditLog).block();
            insertedAuditLog = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createAuditLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);
        var returnedAuditLogDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuditLogDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AuditLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditLog = auditLogMapper.toEntity(returnedAuditLogDTO);
        assertAuditLogUpdatableFieldsEquals(returnedAuditLog, getPersistedAuditLog(returnedAuditLog));

        insertedAuditLog = returnedAuditLog;
    }

    @Test
    void createAuditLogWithExistingId() throws Exception {
        // Create the AuditLog with an existing ID
        auditLog.setId(1L);
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEntityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLog.setEntity(null);

        // Create the AuditLog, which fails.
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLog.setAction(null);

        // Create the AuditLog, which fails.
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLog.setTimestamp(null);

        // Create the AuditLog, which fails.
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEntityIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLog.setEntityId(null);

        // Create the AuditLog, which fails.
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAuditLogs() {
        // Initialize the database
        insertedAuditLog = auditLogRepository.save(auditLog).block();

        // Get all the auditLogList
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
            .value(hasItem(auditLog.getId().intValue()))
            .jsonPath("$.[*].entity")
            .value(hasItem(DEFAULT_ENTITY.toString()))
            .jsonPath("$.[*].action")
            .value(hasItem(DEFAULT_ACTION.toString()))
            .jsonPath("$.[*].timestamp")
            .value(hasItem(DEFAULT_TIMESTAMP.toString()))
            .jsonPath("$.[*].entityId")
            .value(hasItem(DEFAULT_ENTITY_ID.intValue()));
    }

    @Test
    void getAuditLog() {
        // Initialize the database
        insertedAuditLog = auditLogRepository.save(auditLog).block();

        // Get the auditLog
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, auditLog.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(auditLog.getId().intValue()))
            .jsonPath("$.entity")
            .value(is(DEFAULT_ENTITY.toString()))
            .jsonPath("$.action")
            .value(is(DEFAULT_ACTION.toString()))
            .jsonPath("$.timestamp")
            .value(is(DEFAULT_TIMESTAMP.toString()))
            .jsonPath("$.entityId")
            .value(is(DEFAULT_ENTITY_ID.intValue()));
    }

    @Test
    void getNonExistingAuditLog() {
        // Get the auditLog
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuditLog() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.save(auditLog).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLog
        AuditLog updatedAuditLog = auditLogRepository.findById(auditLog.getId()).block();
        updatedAuditLog.entity(UPDATED_ENTITY).action(UPDATED_ACTION).timestamp(UPDATED_TIMESTAMP).entityId(UPDATED_ENTITY_ID);
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(updatedAuditLog);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditLogDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditLogToMatchAllProperties(updatedAuditLog);
    }

    @Test
    void putNonExistingAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditLogDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuditLogWithPatch() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.save(auditLog).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLog using partial update
        AuditLog partialUpdatedAuditLog = new AuditLog();
        partialUpdatedAuditLog.setId(auditLog.getId());

        partialUpdatedAuditLog.entity(UPDATED_ENTITY).timestamp(UPDATED_TIMESTAMP);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditLog.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditLog))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditLogUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAuditLog, auditLog), getPersistedAuditLog(auditLog));
    }

    @Test
    void fullUpdateAuditLogWithPatch() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.save(auditLog).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLog using partial update
        AuditLog partialUpdatedAuditLog = new AuditLog();
        partialUpdatedAuditLog.setId(auditLog.getId());

        partialUpdatedAuditLog.entity(UPDATED_ENTITY).action(UPDATED_ACTION).timestamp(UPDATED_TIMESTAMP).entityId(UPDATED_ENTITY_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditLog.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditLog))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditLogUpdatableFieldsEquals(partialUpdatedAuditLog, getPersistedAuditLog(partialUpdatedAuditLog));
    }

    @Test
    void patchNonExistingAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, auditLogDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditLogDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuditLog() {
        // Initialize the database
        insertedAuditLog = auditLogRepository.save(auditLog).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditLog
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, auditLog.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditLogRepository.count().block();
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

    protected AuditLog getPersistedAuditLog(AuditLog auditLog) {
        return auditLogRepository.findById(auditLog.getId()).block();
    }

    protected void assertPersistedAuditLogToMatchAllProperties(AuditLog expectedAuditLog) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditLogAllPropertiesEquals(expectedAuditLog, getPersistedAuditLog(expectedAuditLog));
        assertAuditLogUpdatableFieldsEquals(expectedAuditLog, getPersistedAuditLog(expectedAuditLog));
    }

    protected void assertPersistedAuditLogToMatchUpdatableProperties(AuditLog expectedAuditLog) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditLogAllUpdatablePropertiesEquals(expectedAuditLog, getPersistedAuditLog(expectedAuditLog));
        assertAuditLogUpdatableFieldsEquals(expectedAuditLog, getPersistedAuditLog(expectedAuditLog));
    }
}
