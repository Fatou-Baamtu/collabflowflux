package com.collabflow.service;

import com.collabflow.service.dto.TaskDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.collabflow.domain.Task}.
 */
public interface TaskService {
    /**
     * Save a task.
     *
     * @param taskDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TaskDTO> save(TaskDTO taskDTO);

    /**
     * Updates a task.
     *
     * @param taskDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TaskDTO> update(TaskDTO taskDTO);

    /**
     * Partially updates a task.
     *
     * @param taskDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TaskDTO> partialUpdate(TaskDTO taskDTO);

    /**
     * Get all the tasks.
     *
     * @return the list of entities.
     */
    Flux<TaskDTO> findAll();

    /**
     * Returns the number of tasks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" task.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TaskDTO> findOne(Long id);

    /**
     * Delete the "id" task.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
