package org.avniproject.etl.repository.rowMappers.tableMappers;

import org.avniproject.etl.domain.metadata.Column;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddressTable extends Table{
    @Override
    public String name(Map<String, Object> tableDetails) {
        return "address";
    }

    @Override
    public List<Column> columns() {
        return new Columns()
                .withCommonColumns()
                .withColumn(new Column("title", Column.Type.integer))
                .build();
    }
}
