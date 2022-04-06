package org.avniproject.etl.service;

import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.repository.EntitySyncStatusRepository;
import org.avniproject.etl.repository.SchemaMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganisationFactory {
    SchemaMetadataRepository schemaMetadataRepository;
    EntitySyncStatusRepository entitySyncStatusRepository;

    @Autowired
    public OrganisationFactory(SchemaMetadataRepository schemaMetadataRepository, EntitySyncStatusRepository entitySyncStatusRepository) {
        this.schemaMetadataRepository = schemaMetadataRepository;
        this.entitySyncStatusRepository = entitySyncStatusRepository;
    }

    public Organisation create(OrganisationIdentity organisationIdentity) {
        return new Organisation(organisationIdentity,
                schemaMetadataRepository.getExistingSchemaMetadata(),
                entitySyncStatusRepository.getSyncStatus());
    }
}
