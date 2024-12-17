package com.collabflow.repository.rowmapper;

import com.collabflow.domain.Project;
import com.collabflow.domain.enumeration.Priority;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Project}, with proper type conversions.
 */
@Service
public class ProjectRowMapper implements BiFunction<Row, String, Project> {

    private final ColumnConverter converter;

    public ProjectRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Project} stored in the database.
     */
    @Override
    public Project apply(Row row, String prefix) {
        Project entity = new Project();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPriority(converter.fromRow(row, prefix + "_priority", Priority.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", Instant.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", Instant.class));
        return entity;
    }
}
