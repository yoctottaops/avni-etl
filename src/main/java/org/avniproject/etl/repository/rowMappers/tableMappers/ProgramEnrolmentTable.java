package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProgramEnrolmentTable extends TableStructure {

    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName("ProgramEnrolment", null, tableDetails, "subject_type_name", "program_name");
    }

    @Override
    public List<Column> columns() {
        return new Columns()
                .withCommonColumns()
                .withColumns(Arrays.asList(
                        new Column("program_id", Column.Type.integer),
                        new Column("individual_id", Column.Type.integer),
                        new Column("program_outcome_id", Column.Type.integer),
                        new Column("enrolment_date_time", Column.Type.timestampWithTimezone),
                        new Column("program_exit_date_time", Column.Type.timestampWithTimezone),
                        new Column("enrolment_location", Column.Type.point),
                        new Column("exit_location", Column.Type.point),
                        new Column("legacy_id", Column.Type.integer)
                ))
                .build();
    }
}
