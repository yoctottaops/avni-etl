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
    private ConceptType conceptType;
    private String conceptUuid;

    public enum ConceptType {
        Audio,
        Date,
        DateTime,
        Duration,
        File,
        GroupAffiliation,
        Id,
        Image,
        Location,
        MultiSelect,
        NA,
        Notes,
        Numeric,
        PhoneNumber,
        SingleSelect,
        Subject,
        Text,
        Time,
        Video;

        Column.Type getColumnDatatype() {
            switch (this) {
                case Numeric:
                    return Column.Type.integer;
                case Date:
                    return Column.Type.date;
                case DateTime:
                case Time:
                    return Column.Type.timestamp;
                case Location:
                    return Column.Type.point;
                default:
                    return Column.Type.text;
            }
        }

    }

    public ColumnMetadata(Integer id, Column column, Integer conceptId, ConceptType conceptType, String conceptUuid) {
        super(id);
        this.column = column;
        this.conceptId = conceptId;
        this.conceptType = conceptType;
        this.conceptUuid = conceptUuid;
    }

    public ColumnMetadata(Integer id, String name, Integer conceptId, ConceptType conceptType, String conceptUuid) {
        this(id, conceptType == null ? new Column(name, null) : new Column(name, conceptType.getColumnDatatype()), conceptId, conceptType, conceptUuid);
    }

    public ColumnMetadata(Column column, Integer conceptId, ConceptType conceptType, String conceptUuid) {
        this(null, column, conceptId, conceptType, conceptUuid);
    }

    public Integer getConceptId() {
        return conceptId;
    }

    public ConceptType getConceptType() {
        return conceptType;
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

    public String getConceptUuid() {
        return conceptUuid;
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
        if (!getType().equals(oldColumnMetadata.getType())) {
            throw new RuntimeException(String.format("Change in datatype detected. Table: %s, Column: %s, Old Type: %s, New Type: %s", newTable.getName(), getName(), getType(), oldColumnMetadata.getType()));
        }
        return Collections.emptyList();
    }

    public void mergeWith(ColumnMetadata oldColumnMetadata) {
        setId(oldColumnMetadata.getId());
    }
}
