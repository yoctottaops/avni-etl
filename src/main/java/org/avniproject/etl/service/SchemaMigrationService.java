package org.avniproject.etl.service;

import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.repository.SchemaMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchemaMigrationService {
    private SchemaMetadataRepository schemaMetadataRepository;

    @Autowired
    public SchemaMigrationService(SchemaMetadataRepository schemaMetadataRepository) {
        this.schemaMetadataRepository = schemaMetadataRepository;
    }

    public Organisation migrate(Organisation organisation) {
        ensureSchemaExists(organisation.getOrganisationIdentity());

        SchemaMetadata newSchemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();

        schemaMetadataRepository.applyChanges(newSchemaMetadata
                .findChanges(organisation.getCurrentSchemaMetadata()));

        schemaMetadataRepository.save(newSchemaMetadata);

        return organisation;
    }

    private void ensureSchemaExists(OrganisationIdentity organisationIdentity) {
        schemaMetadataRepository.createDBUser(organisationIdentity.getDbUser(), "password");
        schemaMetadataRepository.createImplementationSchema(organisationIdentity.getSchemaName(), organisationIdentity.getDbUser());
    }
}
