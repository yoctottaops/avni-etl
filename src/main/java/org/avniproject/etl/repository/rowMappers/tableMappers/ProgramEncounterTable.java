package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.List;
import java.util.Map;

public class ProgramEncounterTable extends EncounterTable {

    @Override
    public List<Column> columns() {
        return new Columns()
                .withColumns(super.columns())
                .withColumn(new Column("program_enrolment_id", Column.Type.integer, Column.ColumnType.index)).build();
    }

    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName("ProgramEncounter", null, tableDetails, "subject_type_name", "program_name", "encounter_type_name");
    }
}
