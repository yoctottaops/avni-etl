package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EncounterTable extends TableStructure {

    @Override
    public List<Column> columns() {
        return new Columns()
                .withCommonColumns()
                .withColumns(Arrays.asList(
                                new Column("name", Column.Type.text),
                                new Column("individual_id", Column.Type.integer),
                                new Column("encounter_type_name", Column.Type.text),
                                new Column("address_id", Column.Type.integer),
                                new Column("earliest_visit_date_time", Column.Type.timestampWithTimezone),
                                new Column("max_visit_date_time", Column.Type.timestampWithTimezone),
                                new Column("encounter_date_time", Column.Type.timestampWithTimezone),
                                new Column("encounter_location", Column.Type.point),
                                new Column("cancel_date_time", Column.Type.timestampWithTimezone),
                                new Column("cancel_location", Column.Type.point)
                        )
                ).build();
    }

    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName("Encounter", null, tableDetails, "subject_type_name", "encounter_type_name");
    }
}
