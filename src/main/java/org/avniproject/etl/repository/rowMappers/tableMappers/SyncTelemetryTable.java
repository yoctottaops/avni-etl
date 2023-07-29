package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.List;
import java.util.Map;

public class SyncTelemetryTable extends Table {

    @Override
    public String name(Map<String, Object> tableDetails) {
        return "sync_telemetry";
    }

    @Override
    public List<Column> columns() {
        return new Columns()
                .withSerialIdColumn()
                .withCommonColumns()
                .withColumn(new Column("user_id", Column.Type.integer, Column.ColumnType.index))
                .withColumn(new Column("sync_status", Column.Type.text))
                .withColumn(new Column("sync_start_time", Column.Type.timestampWithTimezone, Column.ColumnType.index))
                .withColumn(new Column("sync_end_time", Column.Type.timestampWithTimezone, Column.ColumnType.index))
                .withColumn(new Column("device_name", Column.Type.text))
                .withColumn(new Column("android_version", Column.Type.text))
                .withColumn(new Column("app_version", Column.Type.text))
                .withColumn(new Column("device_info", Column.Type.text))
                .withColumn(new Column("sync_source", Column.Type.text))
                .withColumn(new Column("latest_approval_status", Column.Type.text))
                .build();
    }
}