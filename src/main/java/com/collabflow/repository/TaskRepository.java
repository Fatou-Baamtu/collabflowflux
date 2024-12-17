package com.collabflow.repository;

import com.collabflow.domain.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends ReactiveCrudRepository<Task, Long>, TaskRepositoryInternal {
    @Query("SELECT * FROM task entity WHERE entity.assignee_id = :id")
    Flux<Task> findByAssignee(Long id);

    @Query("SELECT * FROM task entity WHERE entity.assignee_id IS NULL")
    Flux<Task> findAllWhereAssigneeIsNull();

    @Query("SELECT * FROM task entity WHERE entity.project_id = :id")
    Flux<Task> findByProject(Long id);

    @Query("SELECT * FROM task entity WHERE entity.project_id IS NULL")
    Flux<Task> findAllWhereProjectIsNull();

    @Override
    <S extends Task> Mono<S> save(S entity);

    @Override
    Flux<Task> findAll();

    @Override
    Mono<Task> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TaskRepositoryInternal {
    <S extends Task> Mono<S> save(S entity);

    Flux<Task> findAllBy(Pageable pageable);

    Flux<Task> findAll();

    Mono<Task> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Task> findAllBy(Pageable pageable, Criteria criteria);
}
