package com.collabflow.service;

import com.collabflow.service.dto.CommentDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.collabflow.domain.Comment}.
 */
public interface CommentService {
    /**
     * Save a comment.
     *
     * @param commentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CommentDTO> save(CommentDTO commentDTO);

    /**
     * Updates a comment.
     *
     * @param commentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CommentDTO> update(CommentDTO commentDTO);

    /**
     * Partially updates a comment.
     *
     * @param commentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CommentDTO> partialUpdate(CommentDTO commentDTO);

    /**
     * Get all the comments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CommentDTO> findAll(Pageable pageable);

    /**
     * Returns the number of comments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" comment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CommentDTO> findOne(Long id);

    /**
     * Delete the "id" comment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
