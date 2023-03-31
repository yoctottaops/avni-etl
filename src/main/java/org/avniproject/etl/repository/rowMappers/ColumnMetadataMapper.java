package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.metadata.Column;
import org.avniproject.etl.domain.metadata.ColumnMetadata;

import java.util.Map;

public class ColumnMetadataMapper {

    public ColumnMetadata create(Map<String, Object> column) {
        if (column.get("parent_concept_name") != null) {
            return new ColumnMetadata(
                    null,
                    (String) column.get("parent_concept_name") + " " + column.get("concept_name"),
                    (Integer) column.get("concept_id"),
                    ColumnMetadata.ConceptType.valueOf((String) column.get("element_type")),
                    (String) column.get("concept_uuid"),
                    (String) column.get("parent_concept_uuid"),
                    null);
        }
        return new ColumnMetadata(
                null,
                (String) column.get("concept_name"),
                (Integer) column.get("concept_id"),
                ColumnMetadata.ConceptType.valueOf((String) column.get("element_type")),
                (String) column.get("concept_uuid"),
                null,
                null);
    }

    public ColumnMetadata createSyncColumnMetadata(Map<String, Object> column, Column.ColumnType columnType) {
        return new ColumnMetadata(
                null,
                (String) column.get("concept_name"),
                (Integer) column.get("concept_id"),
                ColumnMetadata.ConceptType.valueOf((String) column.get("element_type")),
                (String) column.get("concept_uuid"),
                null,
                columnType);
    }
}
