package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PersonTable extends SubjectTable {
    private final boolean allowMiddleName;

    public PersonTable(boolean allowMiddleName) {
        this.allowMiddleName = allowMiddleName;
    }

    @Override
    public List<Column> columns() {
        return new Columns()
                .withColumns(super.columns())
                .withColumns(allowMiddleName ? Collections.singletonList(new Column("middle_name", Column.Type.text)) : new ArrayList())
                .withColumns(Arrays.asList(
                        new Column("date_of_birth", Column.Type.date, Column.ColumnType.index),
                        new Column("date_of_birth_verified", Column.Type.bool),
                        new Column("gender", Column.Type.text)
                ))
                .build();
    }
}
