package org.avniproject.etl.service;

import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.repository.SchemaMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchemaMigrationService {
    private final SchemaMetadataRepository schemaMetadataRepository;
    private final OrganisationRepository organisationRepository;

    @Autowired
    public SchemaMigrationService(SchemaMetadataRepository schemaMetadataRepository, OrganisationRepository organisationRepository) {
        this.schemaMetadataRepository = schemaMetadataRepository;
        this.organisationRepository = organisationRepository;
    }

    public Organisation migrate(Organisation organisation) {
        ensureSchemaExists(organisation.getOrganisationIdentity());

        SchemaMetadata newSchemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();

        schemaMetadataRepository.applyChanges(newSchemaMetadata
                .findChanges(organisation.getSchemaMetadata()));

        organisation.applyNewSchema(newSchemaMetadata);

        schemaMetadataRepository.save(organisation.getSchemaMetadata());

        return organisation;
    }

    private void ensureSchemaExists(OrganisationIdentity organisationIdentity) {
        organisationRepository.createDBUser(organisationIdentity.getDbUser(), "password");
        organisationRepository.createImplementationSchema(organisationIdentity.getSchemaName(), organisationIdentity.getDbUser());
    }
}
