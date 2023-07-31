package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.List;
import java.util.Map;

public class UserTable extends Table{
    @Override
    public String name(Map<String, Object> tableDetails) {
        return "user";
    }

    @Override
    public List<Column> columns() {
        return new Columns()
                .withCommonColumns()
                .withColumn(new Column("id",Column.Type.integer))
                .withColumn(new Column("username", Column.Type.text))
                .withColumn(new Column("catchment_id", Column.Type.integer))
                .withColumn(new Column("email", Column.Type.text))
                .withColumn(new Column("phone_number", Column.Type.text))
                .withColumn(new Column("name", Column.Type.text))
                .build();
    }
}