package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SubjectProgramEligibilityTable extends Table {
    @Override
    public List<Column> columns() {
        return new Columns()
                .withIdColumn()
                .withCommonColumns()
                .withColumns(Arrays.asList(
                        new Column("subject_id", Column.Type.integer, Column.ColumnType.index),
                        new Column("program_id", Column.Type.integer, Column.ColumnType.index),
                        new Column("is_eligible", Column.Type.bool),
                        new Column("check_date", Column.Type.timestampWithTimezone)
                        )
                ).build();
    }

    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName("ProgramEligibility", "manual_program_eligibility", tableDetails, "subject_type_name", "program_name");
    }
}
