package com.collabflow.repository;

import com.collabflow.domain.SubTask;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SubTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubTaskRepository extends ReactiveCrudRepository<SubTask, Long>, SubTaskRepositoryInternal {
    Flux<SubTask> findAllBy(Pageable pageable);

    @Query("SELECT * FROM sub_task entity WHERE entity.assignee_id = :id")
    Flux<SubTask> findByAssignee(Long id);

    @Query("SELECT * FROM sub_task entity WHERE entity.assignee_id IS NULL")
    Flux<SubTask> findAllWhereAssigneeIsNull();

    @Query("SELECT * FROM sub_task entity WHERE entity.task_id = :id")
    Flux<SubTask> findByTask(Long id);

    @Query("SELECT * FROM sub_task entity WHERE entity.task_id IS NULL")
    Flux<SubTask> findAllWhereTaskIsNull();

    @Override
    <S extends SubTask> Mono<S> save(S entity);

    @Override
    Flux<SubTask> findAll();

    @Override
    Mono<SubTask> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SubTaskRepositoryInternal {
    <S extends SubTask> Mono<S> save(S entity);

    Flux<SubTask> findAllBy(Pageable pageable);

    Flux<SubTask> findAll();

    Mono<SubTask> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<SubTask> findAllBy(Pageable pageable, Criteria criteria);
}
