package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.metadata.ColumnMetadata;

import java.util.Map;

public class ColumnMetadataMapper {

    public ColumnMetadata create(Map<String, Object> column) {
        return new ColumnMetadata(
                null,
                (String) column.get("concept_name"),
                (Integer) column.get("concept_id"),
                ColumnMetadata.ConceptType.valueOf((String) column.get("element_type")),
                (String) column.get("concept_uuid"));
    }
}
