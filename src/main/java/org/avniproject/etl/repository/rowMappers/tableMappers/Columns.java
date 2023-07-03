package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.ArrayList;
import java.util.List;

import static org.avniproject.etl.repository.rowMappers.tableMappers.CommonColumns.SerialIdColumn;
import static org.avniproject.etl.repository.rowMappers.tableMappers.CommonColumns.commonColumns;

public class Columns {
    private final ArrayList<Column> columns;

    public Columns() {
        this.columns = new ArrayList<>();
    }

    public Columns withCommonColumns() {
        columns.addAll(commonColumns);
        return this;
    }

    public Columns withSerialIdColumn() {
        columns.add(SerialIdColumn);
        return this;
    }

    public Columns withIdColumn() {
        columns.add(new Column("id", Column.Type.integer, Column.ColumnType.index));
        return this;
    }

    public List<Column> build() {
        return this.columns;
    }

    public Columns withColumns(List<Column> columns) {
        this.columns.addAll(columns);
        return this;
    }

    public Columns withColumn(Column column) {
        this.columns.add(column);
        return this;
    }
}
