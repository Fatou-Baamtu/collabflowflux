package com.collabflow.service;

import com.collabflow.service.dto.ProjectDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.collabflow.domain.Project}.
 */
public interface ProjectService {
    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> save(ProjectDTO projectDTO);

    /**
     * Updates a project.
     *
     * @param projectDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> update(ProjectDTO projectDTO);

    /**
     * Partially updates a project.
     *
     * @param projectDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> partialUpdate(ProjectDTO projectDTO);

    /**
     * Get all the projects.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProjectDTO> findAll(Pageable pageable);

    /**
     * Returns the number of projects available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" project.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProjectDTO> findOne(Long id);

    /**
     * Delete the "id" project.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
