package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.Model;
import org.avniproject.etl.domain.metadata.Column.Type;

public class ColumnMetadata extends Model {
    private final Column column;
    private Integer conceptId;

    public ColumnMetadata(Column column, Integer conceptId) {
        this.column = column;
        this.conceptId = conceptId;
    }

    public Integer getConceptId() {
        return conceptId;
    }

    public void setConceptId(Integer conceptId) {
        this.conceptId = conceptId;
    }

    public Column getColumn() {
        return this.column;
    }

    public String getName() {
        return column.getName();
    }

    public Type getType() {
        return column.getType();
    }
}
