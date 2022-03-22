package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.metadata.Column;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AddColumnTest {

    @Test
    public void shouldAddColumn() {
        AddColumn addColumn = new AddColumn("table", new Column("name", Column.Type.text));
        assertThat(addColumn.getSql(), is("alter table table add column \"name\" text;"));
    }
}
