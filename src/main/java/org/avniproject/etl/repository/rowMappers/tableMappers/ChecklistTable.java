package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.repository.rowMappers.ColumnMetadataMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ChecklistTable extends Table {
    private final List<Map<String, Object>> checklistTypes;

    public ChecklistTable(List<Map<String, Object>> checklistTypes) {
        this.checklistTypes = checklistTypes;
    }

    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName("checklist", "", tableDetails, "name");
    }

    @Override
    public List<Column> columns() {
        return new Columns()
                .withIdColumn()
                .withColumn(new Column("program_enrolment_id", Column.Type.integer, Column.ColumnType.index))
                .withColumn(new Column("base_date", Column.Type.date))
                .withCommonColumns()
                .build();
    }
}
