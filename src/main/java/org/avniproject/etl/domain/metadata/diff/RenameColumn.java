package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.avniproject.etl.domain.metadata.diff.Strings.*;

public class RenameColumn implements Diff {
    private final String tableName;
    private final String oldName;
    private final String newName;
    private static final Logger log = LoggerFactory.getLogger(AddColumn.class);

    public RenameColumn(String tableName, String oldName, String newName) {
        this.tableName = tableName;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public String getSql() {
        String alter_table_rename_col = new StringBuffer()
                .append("alter table ")
                .append(ContextHolder.getDbSchema())
                .append(DOT)
                .append(tableName)
                .append(" rename column ")
                .append(QUOTE)
                .append(oldName)
                .append(QUOTE)
                .append(" to ")
                .append(QUOTE)
                .append(newName)
                .append(QUOTE)
                .append(END_STATEMENT).toString();
        log.error("Altering table to renaming column:" +  alter_table_rename_col);
        return alter_table_rename_col;
    }
}
