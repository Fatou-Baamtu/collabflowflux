package com.collabflow.repository.rowmapper;

import com.collabflow.domain.SubTask;
import com.collabflow.domain.enumeration.Status;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link SubTask}, with proper type conversions.
 */
@Service
public class SubTaskRowMapper implements BiFunction<Row, String, SubTask> {

    private final ColumnConverter converter;

    public SubTaskRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link SubTask} stored in the database.
     */
    @Override
    public SubTask apply(Row row, String prefix) {
        SubTask entity = new SubTask();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Status.class));
        entity.setDueDate(converter.fromRow(row, prefix + "_due_date", Instant.class));
        entity.setAssigneeId(converter.fromRow(row, prefix + "_assignee_id", Long.class));
        entity.setTaskId(converter.fromRow(row, prefix + "_task_id", Long.class));
        return entity;
    }
}
