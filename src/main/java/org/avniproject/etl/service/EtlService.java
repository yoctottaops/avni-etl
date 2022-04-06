package org.avniproject.etl.service;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.domain.result.EtlResult;
import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EtlService {
    private final OrganisationRepository organisationRepository;
    private final OrganisationFactory organisationFactory;
    private final SchemaMigrationService schemaMigrationService;
    private final SyncService syncService;
    private static final Logger log = LoggerFactory.getLogger(EtlService.class);

    @Autowired
    public EtlService(OrganisationRepository organisationRepository, OrganisationFactory organisationFactory, SchemaMigrationService schemaMigrationService, SyncService syncService) {
        this.organisationRepository = organisationRepository;
        this.organisationFactory = organisationFactory;
        this.schemaMigrationService = schemaMigrationService;
        this.syncService = syncService;
    }

    public void run() {
        organisationRepository
                .getOrganisationList()
                .stream()
                .forEach(organisationIdentity -> runForOrganisation(organisationIdentity));
    }

    public EtlResult runForOrganisation(OrganisationIdentity organisationIdentity) {
        log.info(String.format("Running ETL for schema %s", organisationIdentity.getSchemaName()));
        ContextHolder.setContext(organisationIdentity);

        Organisation organisation = organisationFactory.create(organisationIdentity);
        Organisation newOrganisation = schemaMigrationService.migrate(organisation);

        syncService.sync(newOrganisation);

        return new EtlResult(true);
    }
}
