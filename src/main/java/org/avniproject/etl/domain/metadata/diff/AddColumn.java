package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.metadata.Column;

import static org.avniproject.etl.domain.metadata.diff.Strings.*;

public class AddColumn implements Diff {
    private final String tableName;
    private final Column column;

    public AddColumn(String tableName, Column column) {
        this.tableName = tableName;
        this.column = column;
    }

    @Override
    public String getSql() {
        return new StringBuffer()
                .append("alter table ")
                .append(tableName)
                .append(" add column ")
                .append(QUOTE)
                .append(column.getName())
                .append(QUOTE)
                .append(SPACE)
                .append(column.getType().typeString())
                .append(END_STATEMENT).toString();
    }
}
