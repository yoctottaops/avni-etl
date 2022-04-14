package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.Column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.avniproject.etl.domain.metadata.diff.Strings.*;

public class CreateTable implements Diff {
    private final List<Column> columns = new ArrayList<>();
    private String name = "";

    public CreateTable(String name, List<Column> columns) {
        this.name = name;
        this.columns.addAll(columns);
    }

    @Override
    public String getSql() {
        StringBuffer sql = new StringBuffer("create table ")
                .append(this.getTableName())
                .append(OPEN_BRACKETS)
                .append(addColumnsToSql(columns))
                .append(CLOSE_BRACKETS)
                .append(END_STATEMENT)
                .append(NEWLINE);

        List<String> groupDbUsers = ContextHolder.getOrganisationIdentity().getGroupDbUsers();
        List<String> applicableUsers = groupDbUsers.isEmpty() ? Collections.singletonList(ContextHolder.getDbUser()) : groupDbUsers;
        List<StringBuilder> permissions = applicableUsers.stream()
                .map(user -> grantPermissions(ContextHolder.getDbSchema(), user))
                .collect(Collectors.toList());

        sql.append(String.join("", permissions));
        sql.append(addIndices(columns));

        return sql.toString();
    }

    private StringBuilder grantPermissions(String dbSchema, String user) {
        return new StringBuilder()
                .append("grant all privileges on all tables in schema ")
                .append(dbSchema)
                .append(" to ")
                .append(user)
                .append(END_STATEMENT)
                .append(NEWLINE);
    }

    private String getTableName() {
        return new StringBuilder().append(ContextHolder.getDbSchema())
                .append(DOT)
                .append(name)
                .toString();
    }

    private String addColumnsToSql(List<Column> columns) {
        List<String> defaultColumns = columns.stream().map(column ->
                String.valueOf(
                        new StringBuffer()
                                .append(QUOTE)
                                .append(column.getName())
                                .append(QUOTE)
                                .append(SPACE)
                                .append(column.getType().typeString())))
                .collect(Collectors.toList());

        return String.join(COMMA, defaultColumns);
    }

    private String addIndices(List<Column> columns) {
        List<String> indices = columns.stream()
                .filter(Column::isIndexed)
                .map(column -> String.valueOf(
                        new StringBuffer()
                                .append("create index ")
                                .append(ContextHolder.getDbSchema())
                                .append(name)
                                .append(column.getName())
                                .append("idx")
                                .append(" on ")
                                .append(this.getTableName())
                                .append(OPEN_BRACKETS)
                                .append(column.getName())
                                .append(CLOSE_BRACKETS)
                                .append(END_STATEMENT)
                ))
                .collect(Collectors.toList());
        return String.join(NEWLINE, indices);
    }
}
