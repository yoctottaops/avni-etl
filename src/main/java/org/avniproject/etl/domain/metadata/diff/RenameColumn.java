package org.avniproject.etl.domain.metadata.diff;

import static org.avniproject.etl.domain.metadata.diff.Strings.END_STATEMENT;

public class RenameColumn implements Diff {
    private final String tableName;
    private final String oldName;
    private final String newName;

    public RenameColumn(String tableName, String oldName, String newName) {
        this.tableName = tableName;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public String getSql() {
        return new StringBuffer()
                .append("alter table ")
                .append(tableName)
                .append(" rename column ")
                .append(oldName)
                .append(" to ")
                .append(newName)
                .append(END_STATEMENT).toString();
    }
}
