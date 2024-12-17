package com.collabflow.repository;

import com.collabflow.domain.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends ReactiveCrudRepository<Project, Long>, ProjectRepositoryInternal {
    Flux<Project> findAllBy(Pageable pageable);

    @Override
    <S extends Project> Mono<S> save(S entity);

    @Override
    Flux<Project> findAll();

    @Override
    Mono<Project> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjectRepositoryInternal {
    <S extends Project> Mono<S> save(S entity);

    Flux<Project> findAllBy(Pageable pageable);

    Flux<Project> findAll();

    Mono<Project> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Project> findAllBy(Pageable pageable, Criteria criteria);
}
