package org.avniproject.etl.domain;

import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;

public class Organisation {
    private final OrganisationIdentity organisationIdentity;
    private SchemaMetadata schemaMetadata;
    private final SchemaDataSyncStatus syncStatus;

    public Organisation(OrganisationIdentity organisationIdentity, SchemaMetadata existingSchemaMetadata, SchemaDataSyncStatus syncStatus) {
        this.organisationIdentity = organisationIdentity;
        schemaMetadata = existingSchemaMetadata;
        this.syncStatus = syncStatus;
    }

    public OrganisationIdentity getOrganisationIdentity() {
        return organisationIdentity;
    }

    public SchemaMetadata getSchemaMetadata() {
        return schemaMetadata;
    }

    public SchemaDataSyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void applyNewSchema(SchemaMetadata newSchemaMetadata) {
        newSchemaMetadata.mergeWith(schemaMetadata);
        this.schemaMetadata = newSchemaMetadata;
    }
}
