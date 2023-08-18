package org.avniproject.etl.dto;

import org.avniproject.etl.domain.metadata.ColumnMetadata;

public class ConceptDTO {
    private final ColumnMetadata.ConceptType dataType;
    private final String uuid;
    private final String name;

    public ConceptDTO(ColumnMetadata.ConceptType dataType, String uuid, String name) {
        this.dataType = dataType;
        this.uuid = uuid;
        this.name = name;
    }

    public ColumnMetadata.ConceptType getDataType() {
        return dataType;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
