package com.collabflow.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AuditLogSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("entity", table, columnPrefix + "_entity"));
        columns.add(Column.aliased("action", table, columnPrefix + "_action"));
        columns.add(Column.aliased("timestamp", table, columnPrefix + "_timestamp"));
        columns.add(Column.aliased("entity_id", table, columnPrefix + "_entity_id"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
