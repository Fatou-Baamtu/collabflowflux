package com.collabflow.service;

import com.collabflow.service.dto.SubTaskDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.collabflow.domain.SubTask}.
 */
public interface SubTaskService {
    /**
     * Save a subTask.
     *
     * @param subTaskDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SubTaskDTO> save(SubTaskDTO subTaskDTO);

    /**
     * Updates a subTask.
     *
     * @param subTaskDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SubTaskDTO> update(SubTaskDTO subTaskDTO);

    /**
     * Partially updates a subTask.
     *
     * @param subTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SubTaskDTO> partialUpdate(SubTaskDTO subTaskDTO);

    /**
     * Get all the subTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SubTaskDTO> findAll(Pageable pageable);

    /**
     * Returns the number of subTasks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" subTask.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SubTaskDTO> findOne(Long id);

    /**
     * Delete the "id" subTask.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
