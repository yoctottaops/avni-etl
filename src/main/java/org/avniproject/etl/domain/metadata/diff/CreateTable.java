package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.avniproject.etl.domain.metadata.diff.Strings.*;

public class CreateTable implements Diff {
    private List<Column> columns = new ArrayList<>();
    private String name = "";

    public CreateTable(String name, List<Column> columns) {
        this.name = name;
        this.columns.addAll(columns);
    }

    @Override
    public String getSql() {
        StringBuffer sql = new StringBuffer("create table ")
                .append(ContextHolder.getDbSchema())
                .append(DOT)
                .append(name)
                .append(OPEN_BRACKETS)
                .append(addColumnsToSql(columns))
                .append(CLOSE_BRACKETS)
                .append(END_STATEMENT);

        return sql.toString();
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
}
