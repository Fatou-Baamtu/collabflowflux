package com.collabflow.repository.rowmapper;

import com.collabflow.domain.Task;
import com.collabflow.domain.enumeration.Status;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Task}, with proper type conversions.
 */
@Service
public class TaskRowMapper implements BiFunction<Row, String, Task> {

    private final ColumnConverter converter;

    public TaskRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Task} stored in the database.
     */
    @Override
    public Task apply(Row row, String prefix) {
        Task entity = new Task();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Status.class));
        entity.setDueDate(converter.fromRow(row, prefix + "_due_date", Instant.class));
        entity.setIsCompleted(converter.fromRow(row, prefix + "_is_completed", Boolean.class));
        entity.setAssigneeId(converter.fromRow(row, prefix + "_assignee_id", Long.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
