package com.collabflow.repository.rowmapper;

import com.collabflow.domain.Comment;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Comment}, with proper type conversions.
 */
@Service
public class CommentRowMapper implements BiFunction<Row, String, Comment> {

    private final ColumnConverter converter;

    public CommentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Comment} stored in the database.
     */
    @Override
    public Comment apply(Row row, String prefix) {
        Comment entity = new Comment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setContent(converter.fromRow(row, prefix + "_content", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setTaskId(converter.fromRow(row, prefix + "_task_id", Long.class));
        return entity;
    }
}
