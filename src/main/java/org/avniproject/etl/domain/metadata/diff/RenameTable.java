package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.OrgIdentityContextHolder;

import static org.avniproject.etl.domain.metadata.diff.Strings.*;

public class RenameTable implements Diff {
    private final String oldName;
    private final String newName;

    public RenameTable(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public String getSql() {
        return new StringBuffer()
                .append("alter table ")
                .append(QUOTE)
                .append(OrgIdentityContextHolder.getDbSchema())
                .append(QUOTE)
                .append(DOT)
                .append(oldName)
                .append(" rename to ")
                .append(newName)
                .append(END_STATEMENT).toString();
    }
}
