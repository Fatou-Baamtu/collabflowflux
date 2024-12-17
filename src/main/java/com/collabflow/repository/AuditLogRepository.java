package com.collabflow.repository;

import com.collabflow.domain.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AuditLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditLogRepository extends ReactiveCrudRepository<AuditLog, Long>, AuditLogRepositoryInternal {
    Flux<AuditLog> findAllBy(Pageable pageable);

    @Query("SELECT * FROM audit_log entity WHERE entity.user_id = :id")
    Flux<AuditLog> findByUser(Long id);

    @Query("SELECT * FROM audit_log entity WHERE entity.user_id IS NULL")
    Flux<AuditLog> findAllWhereUserIsNull();

    @Override
    <S extends AuditLog> Mono<S> save(S entity);

    @Override
    Flux<AuditLog> findAll();

    @Override
    Mono<AuditLog> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AuditLogRepositoryInternal {
    <S extends AuditLog> Mono<S> save(S entity);

    Flux<AuditLog> findAllBy(Pageable pageable);

    Flux<AuditLog> findAll();

    Mono<AuditLog> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AuditLog> findAllBy(Pageable pageable, Criteria criteria);
}
