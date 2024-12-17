package com.collabflow.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TaskSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("due_date", table, columnPrefix + "_due_date"));
        columns.add(Column.aliased("is_completed", table, columnPrefix + "_is_completed"));

        columns.add(Column.aliased("assignee_id", table, columnPrefix + "_assignee_id"));
        columns.add(Column.aliased("project_id", table, columnPrefix + "_project_id"));
        return columns;
    }
}
