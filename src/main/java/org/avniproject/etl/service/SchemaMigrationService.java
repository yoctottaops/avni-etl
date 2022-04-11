package org.avniproject.etl.service;

import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.repository.SchemaMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchemaMigrationService {
    private final SchemaMetadataRepository schemaMetadataRepository;
    private final OrganisationRepository organisationRepository;
    private static final Logger log = LoggerFactory.getLogger(SchemaMigrationService.class);

    @Autowired
    public SchemaMigrationService(SchemaMetadataRepository schemaMetadataRepository, OrganisationRepository organisationRepository) {
        this.schemaMetadataRepository = schemaMetadataRepository;
        this.organisationRepository = organisationRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Organisation migrate(Organisation organisation) {
        ensureSchemaExists(organisation.getOrganisationIdentity());

        SchemaMetadata newSchemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();

        List<Diff> changes = newSchemaMetadata.findChanges(organisation.getSchemaMetadata());
        schemaMetadataRepository.applyChanges(changes);

        organisation.applyNewSchema(newSchemaMetadata);

        schemaMetadataRepository.save(organisation.getSchemaMetadata());

        return organisation;
    }

    private void ensureSchemaExists(OrganisationIdentity organisationIdentity) {
        log.debug("Adding schema if not exists");
        organisationRepository.createDBUser(organisationIdentity.getDbUser(), "password");
        organisationRepository.createImplementationSchema(organisationIdentity.getSchemaName(), organisationIdentity.getDbUser());
    }
}
