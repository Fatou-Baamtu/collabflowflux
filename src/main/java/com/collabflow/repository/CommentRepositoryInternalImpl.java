package com.collabflow.repository;

import com.collabflow.domain.Comment;
import com.collabflow.repository.rowmapper.CommentRowMapper;
import com.collabflow.repository.rowmapper.TaskRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Comment entity.
 */
@SuppressWarnings("unused")
class CommentRepositoryInternalImpl extends SimpleR2dbcRepository<Comment, Long> implements CommentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TaskRowMapper taskMapper;
    private final CommentRowMapper commentMapper;

    private static final Table entityTable = Table.aliased("comment", EntityManager.ENTITY_ALIAS);
    private static final Table taskTable = Table.aliased("task", "task");

    public CommentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TaskRowMapper taskMapper,
        CommentRowMapper commentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Comment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.taskMapper = taskMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public Flux<Comment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Comment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CommentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TaskSqlHelper.getColumns(taskTable, "task"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(taskTable)
            .on(Column.create("task_id", entityTable))
            .equals(Column.create("id", taskTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Comment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Comment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Comment> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Comment process(Row row, RowMetadata metadata) {
        Comment entity = commentMapper.apply(row, "e");
        entity.setTask(taskMapper.apply(row, "task"));
        return entity;
    }

    @Override
    public <S extends Comment> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
