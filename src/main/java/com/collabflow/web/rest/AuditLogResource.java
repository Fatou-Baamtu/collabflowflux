package com.collabflow.web.rest;

import com.collabflow.repository.AuditLogRepository;
import com.collabflow.service.AuditLogService;
import com.collabflow.service.dto.AuditLogDTO;
import com.collabflow.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.collabflow.domain.AuditLog}.
 */
@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogResource.class);

    private static final String ENTITY_NAME = "auditLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuditLogService auditLogService;

    private final AuditLogRepository auditLogRepository;

    public AuditLogResource(AuditLogService auditLogService, AuditLogRepository auditLogRepository) {
        this.auditLogService = auditLogService;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * {@code POST  /audit-logs} : Create a new auditLog.
     *
     * @param auditLogDTO the auditLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditLogDTO, or with status {@code 400 (Bad Request)} if the auditLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AuditLogDTO>> createAuditLog(@Valid @RequestBody AuditLogDTO auditLogDTO) throws URISyntaxException {
        LOG.debug("REST request to save AuditLog : {}", auditLogDTO);
        if (auditLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new auditLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return auditLogService
            .save(auditLogDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/audit-logs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /audit-logs/:id} : Updates an existing auditLog.
     *
     * @param id the id of the auditLogDTO to save.
     * @param auditLogDTO the auditLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditLogDTO,
     * or with status {@code 400 (Bad Request)} if the auditLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditLogDTO>> updateAuditLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditLogDTO auditLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuditLog : {}, {}", id, auditLogDTO);
        if (auditLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditLogRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return auditLogService
                    .update(auditLogDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /audit-logs/:id} : Partial updates given fields of an existing auditLog, field will ignore if it is null
     *
     * @param id the id of the auditLogDTO to save.
     * @param auditLogDTO the auditLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditLogDTO,
     * or with status {@code 400 (Bad Request)} if the auditLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AuditLogDTO>> partialUpdateAuditLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditLogDTO auditLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AuditLog partially : {}, {}", id, auditLogDTO);
        if (auditLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return auditLogRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AuditLogDTO> result = auditLogService.partialUpdate(auditLogDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /audit-logs} : get all the auditLogs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of auditLogs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<AuditLogDTO>>> getAllAuditLogs(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of AuditLogs");
        return auditLogService
            .countAll()
            .zipWith(auditLogService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /audit-logs/:id} : get the "id" auditLog.
     *
     * @param id the id of the auditLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditLogDTO>> getAuditLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditLog : {}", id);
        Mono<AuditLogDTO> auditLogDTO = auditLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditLogDTO);
    }

    /**
     * {@code DELETE  /audit-logs/:id} : delete the "id" auditLog.
     *
     * @param id the id of the auditLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuditLog : {}", id);
        return auditLogService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
