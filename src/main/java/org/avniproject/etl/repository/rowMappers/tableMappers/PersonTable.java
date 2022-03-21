package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.Arrays;
import java.util.List;

public class PersonTable extends SubjectTable {

    @Override
    public List<Column> columns() {
        return new Columns()
                .withColumns(super.columns())
                .withColumns(Arrays.asList(
                        new Column("date_of_birth", Column.Type.date),
                        new Column("date_of_birth_verified", Column.Type.bool),
                        new Column("gender", Column.Type.text)
                ))
                .build();
    }
}
