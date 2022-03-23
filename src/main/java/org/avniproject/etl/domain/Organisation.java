package org.avniproject.etl.domain;

import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;

public class Organisation {
    private OrganisationIdentity organisationIdentity;
    private SchemaMetadata schemaMetadata;
    private SchemaDataSyncStatus syncStatus;

    public OrganisationIdentity getOrganisationIdentity() {
        return organisationIdentity;
    }

    public void setOrganisationIdentity(OrganisationIdentity organisationIdentity) {
        this.organisationIdentity = organisationIdentity;
    }

    public SchemaMetadata getSchemaMetadata() {
        return schemaMetadata;
    }

    public void setSchemaMetadata(SchemaMetadata currentSchemaMetadata) {
        this.schemaMetadata = currentSchemaMetadata;
    }

    public SchemaDataSyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SchemaDataSyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public void applyNewSchema(SchemaMetadata newSchemaMetadata) {
        newSchemaMetadata.mergeWith(schemaMetadata);
        this.setSchemaMetadata(newSchemaMetadata);
    }
}
