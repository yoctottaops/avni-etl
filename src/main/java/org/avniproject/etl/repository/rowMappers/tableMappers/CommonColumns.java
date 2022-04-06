package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.Arrays;
import java.util.List;

public class CommonColumns {
    public static final List<Column> commonColumns = Arrays.asList(new Column("id", Column.Type.integer),
            new Column("uuid", Column.Type.text),
            new Column("is_voided", Column.Type.bool),
            new Column("created_by_id", Column.Type.integer),
            new Column("last_modified_by_id", Column.Type.integer),
            new Column("created_date_time", Column.Type.timestampWithTimezone),
            new Column("last_modified_date_time", Column.Type.timestampWithTimezone));
}
