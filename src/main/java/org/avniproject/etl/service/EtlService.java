package org.avniproject.etl.service;

import org.apache.log4j.Logger;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.result.EtlResult;
import org.avniproject.etl.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EtlService {
    private final OrganisationRepository organisationRepository;
    private final OrganisationFactory organisationFactory;
    private final SchemaMigrationService schemaMigrationService;
    private final SyncService syncService;
    private static final Logger log = Logger.getLogger(EtlService.class);

    @Autowired
    public EtlService(OrganisationRepository organisationRepository, OrganisationFactory organisationFactory,
                      SchemaMigrationService schemaMigrationService, SyncService syncService) {
        this.organisationRepository = organisationRepository;
        this.organisationFactory = organisationFactory;
        this.schemaMigrationService = schemaMigrationService;
        this.syncService = syncService;
    }

    public void run() {
        runForOrganisationSchemaNames(new ArrayList<>());
    }

    public void runForOrganisationSchemaNames(@Nonnull List<String> organisationSchemaNameFilter) {
        boolean runForAll = organisationSchemaNameFilter.isEmpty();
        Set<String> nonProcessedSchemaNames = new HashSet<>(organisationSchemaNameFilter);
        organisationRepository
                .getOrganisationList()
                .stream()
                .filter(oi -> runForAll || organisationSchemaNameFilter.contains(oi.getSchemaName()))
                .forEach(organisationIdentity -> {
                    nonProcessedSchemaNames.remove(organisationIdentity.getSchemaName());
                    runForOrganisation(organisationIdentity);
                });
        if (!nonProcessedSchemaNames.isEmpty()) {
            log.error("Failed to perform ETL job for schemas: " + nonProcessedSchemaNames);
        }
    }

    public EtlResult runForOrganisation(OrganisationIdentity organisationIdentity) {
        log.info(String.format("Running ETL for schema %s with dbUser %s",
                organisationIdentity.getSchemaName(), organisationIdentity.getDbUser()));
        ContextHolder.setContext(organisationIdentity);

        try {
            Organisation organisation = organisationFactory.create(organisationIdentity);
            Organisation newOrganisation = schemaMigrationService.migrate(organisation);
            syncService.sync(newOrganisation);
        } catch (Exception e) {
            log.error("Could not migrate organisation", e);
            return new EtlResult(false);
        }

        return new EtlResult(true);
    }
}
