package org.avniproject.etl.service;

import org.apache.log4j.Logger;
import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.result.EtlResult;
import org.avniproject.etl.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public EtlResult runFor(String organisationUUID) {
        OrganisationIdentity organisationIdentity = organisationRepository.getOrganisation(organisationUUID);
        return this.runFor(organisationIdentity);
    }

    public List<EtlResult> runForOrganisationGroup(String organisationGroupUUID) {
        List<OrganisationIdentity> organisationIdentities = organisationRepository.getOrganisationGroup(organisationGroupUUID);
        return this.runFor(organisationIdentities);
    }

    public List<EtlResult> runFor(List<OrganisationIdentity> organisationIdentities) {
        return organisationIdentities.stream().map(this::runFor).toList();
    }

    public EtlResult runFor(OrganisationIdentity organisationIdentity) {
        log.info(String.format("Running ETL for schema %s with dbUser %s and schemaUser %s",
                organisationIdentity.getSchemaName(), organisationIdentity.getDbUser(), organisationIdentity.getSchemaUser()));
        OrgIdentityContextHolder.setContext(organisationIdentity);

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
