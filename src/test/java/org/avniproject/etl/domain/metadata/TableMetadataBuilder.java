package org.avniproject.etl.domain.metadata;

public class TableMetadataBuilder {
    private final TableMetadata tableMetadata = new TableMetadata();

    public TableMetadataBuilder withSubjectTypeUuid(String uuid) {
        tableMetadata.setSubjectTypeUuid(uuid);
    	return this;
    }

    public TableMetadataBuilder withProgramUuid(String uuid) {
        tableMetadata.setProgramUuid(uuid);
    	return this;
    }

    public TableMetadataBuilder withEncounterTypeUuid(String uuid) {
        tableMetadata.setEncounterTypeUuid(uuid);
    	return this;
    }

    public TableMetadataBuilder withQuestionGroupConceptUuid(String uuid) {
        tableMetadata.setRepeatableQuestionGroupConceptUuid(uuid);
    	return this;
    }

    public TableMetadata build() {
        return tableMetadata;
    }
}
