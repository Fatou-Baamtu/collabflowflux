package com.collabflow.web.rest;

import com.collabflow.repository.SubTaskRepository;
import com.collabflow.service.SubTaskService;
import com.collabflow.service.dto.SubTaskDTO;
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
 * REST controller for managing {@link com.collabflow.domain.SubTask}.
 */
@RestController
@RequestMapping("/api/sub-tasks")
public class SubTaskResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubTaskResource.class);

    private static final String ENTITY_NAME = "subTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubTaskService subTaskService;

    private final SubTaskRepository subTaskRepository;

    public SubTaskResource(SubTaskService subTaskService, SubTaskRepository subTaskRepository) {
        this.subTaskService = subTaskService;
        this.subTaskRepository = subTaskRepository;
    }

    /**
     * {@code POST  /sub-tasks} : Create a new subTask.
     *
     * @param subTaskDTO the subTaskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subTaskDTO, or with status {@code 400 (Bad Request)} if the subTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SubTaskDTO>> createSubTask(@Valid @RequestBody SubTaskDTO subTaskDTO) throws URISyntaxException {
        LOG.debug("REST request to save SubTask : {}", subTaskDTO);
        if (subTaskDTO.getId() != null) {
            throw new BadRequestAlertException("A new subTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return subTaskService
            .save(subTaskDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/sub-tasks/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /sub-tasks/:id} : Updates an existing subTask.
     *
     * @param id the id of the subTaskDTO to save.
     * @param subTaskDTO the subTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subTaskDTO,
     * or with status {@code 400 (Bad Request)} if the subTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SubTaskDTO>> updateSubTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubTaskDTO subTaskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SubTask : {}, {}", id, subTaskDTO);
        if (subTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return subTaskRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return subTaskService
                    .update(subTaskDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /sub-tasks/:id} : Partial updates given fields of an existing subTask, field will ignore if it is null
     *
     * @param id the id of the subTaskDTO to save.
     * @param subTaskDTO the subTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subTaskDTO,
     * or with status {@code 400 (Bad Request)} if the subTaskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subTaskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SubTaskDTO>> partialUpdateSubTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubTaskDTO subTaskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SubTask partially : {}, {}", id, subTaskDTO);
        if (subTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return subTaskRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SubTaskDTO> result = subTaskService.partialUpdate(subTaskDTO);

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
     * {@code GET  /sub-tasks} : get all the subTasks.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subTasks in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<SubTaskDTO>>> getAllSubTasks(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of SubTasks");
        return subTaskService
            .countAll()
            .zipWith(subTaskService.findAll(pageable).collectList())
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
     * {@code GET  /sub-tasks/:id} : get the "id" subTask.
     *
     * @param id the id of the subTaskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SubTaskDTO>> getSubTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SubTask : {}", id);
        Mono<SubTaskDTO> subTaskDTO = subTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subTaskDTO);
    }

    /**
     * {@code DELETE  /sub-tasks/:id} : delete the "id" subTask.
     *
     * @param id the id of the subTaskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSubTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SubTask : {}", id);
        return subTaskService
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
