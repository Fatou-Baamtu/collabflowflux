package com.collabflow.repository.rowmapper;

import com.collabflow.domain.AuditLog;
import com.collabflow.domain.enumeration.ActionEnum;
import com.collabflow.domain.enumeration.EntityEnum;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AuditLog}, with proper type conversions.
 */
@Service
public class AuditLogRowMapper implements BiFunction<Row, String, AuditLog> {

    private final ColumnConverter converter;

    public AuditLogRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AuditLog} stored in the database.
     */
    @Override
    public AuditLog apply(Row row, String prefix) {
        AuditLog entity = new AuditLog();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setEntity(converter.fromRow(row, prefix + "_entity", EntityEnum.class));
        entity.setAction(converter.fromRow(row, prefix + "_action", ActionEnum.class));
        entity.setTimestamp(converter.fromRow(row, prefix + "_timestamp", Instant.class));
        entity.setEntityId(converter.fromRow(row, prefix + "_entity_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
