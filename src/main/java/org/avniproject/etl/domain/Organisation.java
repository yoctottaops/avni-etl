package org.avniproject.etl.domain;

import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;

public class Organisation {
    private SchemaMetadata currentSchemaMetadata;
    private OrganisationIdentity organisationIdentity;
    private SchemaDataSyncStatus syncStatus;

    public SchemaMetadata getCurrentSchemaMetadata() {
        return currentSchemaMetadata;
    }

    public void setCurrentSchemaMetadata(SchemaMetadata currentSchemaMetadata) {
        this.currentSchemaMetadata = currentSchemaMetadata;
    }

    public OrganisationIdentity getOrganisationIdentity() {
        return organisationIdentity;
    }

    public void setOrganisationIdentity(OrganisationIdentity organisationIdentity) {
        this.organisationIdentity = organisationIdentity;
    }

    public SchemaDataSyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SchemaDataSyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }
}
