package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.Model;
import org.avniproject.etl.domain.metadata.Column.Type;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.domain.metadata.diff.RenameColumn;

import java.util.Collections;
import java.util.List;

public class ColumnMetadata extends Model {
    private final Column column;
    private Integer conceptId;

    public ColumnMetadata(Integer id, Column column, Integer conceptId) {
        super(id);
        this.column = column;
        this.conceptId = conceptId;
    }

    public ColumnMetadata(Column column, Integer conceptId) {
        this(null, column, conceptId);
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

    public boolean matches(ColumnMetadata realColumn) {
        if (realColumn == null) return false;
        if (realColumn.getConceptId() == null && getConceptId() == null) {
            return getName().equals(realColumn.getName());
        }
        return equalsIgnoreNulls(realColumn.getConceptId(), getConceptId());
    }

    public List<Diff> findChanges(TableMetadata newTable, ColumnMetadata oldColumnMetadata) {
        if (!getName().equals(oldColumnMetadata.getName())) {
            return List.of(new RenameColumn(newTable.getName(), oldColumnMetadata.getName(), getName()));
        }

        return Collections.emptyList();
    }
}
