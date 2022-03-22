package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.metadata.diff.AddColumn;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.domain.metadata.diff.RenameColumn;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ColumnMetadataTest {

    @Test
    public void shouldRenameColumnIfNecessary() {
        ColumnMetadata oldColumnMetadata = new ColumnMetadata(new Column("oldName", Column.Type.text), 12);
        ColumnMetadata newColumnMetadata = new ColumnMetadata(new Column("newName", Column.Type.text), 12);

        TableMetadata newTable = new TableMetadata();
        newTable.setName("table");

        List<Diff> changes = newColumnMetadata.findChanges(newTable, oldColumnMetadata);

        assertThat(changes.size(), is(1));
        assertThat(changes.get(0), instanceOf(RenameColumn.class));
        assertThat(changes.get(0).getSql(), is("alter table table rename column \"oldName\" to \"newName\";"));
    }
}