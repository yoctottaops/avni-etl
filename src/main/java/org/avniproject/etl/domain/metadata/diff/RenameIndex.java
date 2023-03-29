package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.ContextHolder;

import static org.avniproject.etl.domain.metadata.diff.Strings.*;

public class RenameIndex implements Diff{
    private final String oldName;
    private final String newName;

    public RenameIndex(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public String getSql() {
        return new StringBuffer()
                .append("ALTER INDEX ")
                .append(ContextHolder.getDbSchema())
                .append(DOT)
                .append(QUOTE)
                .append(oldName)
                .append(QUOTE)
                .append(" RENAME TO ")
                .append(newName)
                .append(END_STATEMENT)
                .append(NEWLINE)
                .toString();
    }
}
