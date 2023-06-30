package org.avniproject.etl.repository.rowMappers.tableMappers.repeatableQuestionGroup;

import org.avniproject.etl.domain.metadata.Column;
import org.avniproject.etl.repository.rowMappers.tableMappers.Columns;
import org.avniproject.etl.repository.rowMappers.tableMappers.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.avniproject.etl.repository.rowMappers.TableNameGenerator.ProgramEnrolmentRepeatableQuestionGroup;
import static org.avniproject.etl.repository.rowMappers.tableMappers.CommonColumns.*;

public class ProgramEnrolmentRepeatableQuestionGroupTable extends Table {
    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName(ProgramEnrolmentRepeatableQuestionGroup, null, tableDetails, "subject_type_name", "program_name", "parent_concept_name");
    }

    @Override
    public List<Column> columns() {
        return new Columns()
                .withColumns(CommonRepeatableGroupColumns)
                .withColumns(Arrays.asList(
                        new Column("individual_id", Column.Type.integer, Column.ColumnType.index),
                        new Column("address_id", Column.Type.integer, Column.ColumnType.index),
                        new Column("program_enrolment_id", Column.Type.integer, Column.ColumnType.index)
                ))
                .build();
    }
}
