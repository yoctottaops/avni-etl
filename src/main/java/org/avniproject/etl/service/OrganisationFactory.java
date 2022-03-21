package org.avniproject.etl.service;

import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;
import org.avniproject.etl.repository.SchemaMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrganisationFactory {
    SchemaMetadataRepository schemaMetadataRepository;

    @Autowired
    public OrganisationFactory(SchemaMetadataRepository schemaMetadataRepository) {
        this.schemaMetadataRepository = schemaMetadataRepository;
    }

    public Organisation create(OrganisationIdentity organisationIdentity) {
        SchemaMetadata schemaMetadata = schemaMetadataRepository.getExistingSchemaMetadata();
        SchemaDataSyncStatus schemaDataSyncStatus = new SchemaDataSyncStatus(new ArrayList<>());

        Organisation organisation = new Organisation();
        organisation.setOrganisationIdentity(organisationIdentity);
        organisation.setCurrentSchemaMetadata(schemaMetadata);
        return organisation;
    }
}
