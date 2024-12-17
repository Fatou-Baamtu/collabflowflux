package com.collabflow.repository;

import com.collabflow.domain.SubTask;
import com.collabflow.repository.rowmapper.SubTaskRowMapper;
import com.collabflow.repository.rowmapper.TaskRowMapper;
import com.collabflow.repository.rowmapper.UserRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the SubTask entity.
 */
@SuppressWarnings("unused")
class SubTaskRepositoryInternalImpl extends SimpleR2dbcRepository<SubTask, Long> implements SubTaskRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final TaskRowMapper taskMapper;
    private final SubTaskRowMapper subtaskMapper;

    private static final Table entityTable = Table.aliased("sub_task", EntityManager.ENTITY_ALIAS);
    private static final Table assigneeTable = Table.aliased("jhi_user", "assignee");
    private static final Table taskTable = Table.aliased("task", "task");

    public SubTaskRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        TaskRowMapper taskMapper,
        SubTaskRowMapper subtaskMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(SubTask.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.taskMapper = taskMapper;
        this.subtaskMapper = subtaskMapper;
    }

    @Override
    public Flux<SubTask> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<SubTask> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SubTaskSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(assigneeTable, "assignee"));
        columns.addAll(TaskSqlHelper.getColumns(taskTable, "task"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(assigneeTable)
            .on(Column.create("assignee_id", entityTable))
            .equals(Column.create("id", assigneeTable))
            .leftOuterJoin(taskTable)
            .on(Column.create("task_id", entityTable))
            .equals(Column.create("id", taskTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, SubTask.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<SubTask> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<SubTask> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private SubTask process(Row row, RowMetadata metadata) {
        SubTask entity = subtaskMapper.apply(row, "e");
        entity.setAssignee(userMapper.apply(row, "assignee"));
        entity.setTask(taskMapper.apply(row, "task"));
        return entity;
    }

    @Override
    public <S extends SubTask> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
