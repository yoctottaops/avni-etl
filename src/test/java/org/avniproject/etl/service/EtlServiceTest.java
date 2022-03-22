package org.avniproject.etl.service;

import org.avniproject.etl.builder.OrganisationIdentityBuilder;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.repository.SchemaMetadataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class EtlServiceTest {
    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private OrganisationFactory organisationFactory;

    @Mock
    private SchemaMetadataRepository schemaMetadataRepository;

    @Mock
    private SchemaMigrationService schemaMigrationService;

    @Mock
    private SyncService syncService;

    private AutoCloseable closeable;

    @BeforeEach
    public void beforeEach() {
        closeable = openMocks(this);
    }

    @AfterEach
    public void afterEach() throws Exception {
        closeable.close();
    }

    @Test
    public void fetchesListOfOrganisationsToRunEtlOn() {
        EtlService etlService = new EtlService(organisationRepository, organisationFactory, schemaMigrationService, syncService);
        etlService.run();
        verify(organisationRepository).getOrganisationList();
    }

    @Test
    public void callsOrganisationFactoryToCreateAllOrganisations() {
        List<OrganisationIdentity> listOfOrganisations = Arrays.asList(
                new OrganisationIdentityBuilder().withId(1).withDbUser("a").build(),
                new OrganisationIdentityBuilder().withId(2).withDbUser("b").build());
        OrganisationRepository organisationRepository = mock(OrganisationRepository.class);
        when(organisationRepository.getOrganisationList()).thenReturn(listOfOrganisations);
        OrganisationFactory organisationFactory = mock(OrganisationFactory.class);
        EtlService etlService = new EtlService(organisationRepository, organisationFactory, schemaMigrationService, syncService);

        etlService.run();

        verify(organisationFactory, times(listOfOrganisations.size())).create(any());
    }

    @Test
    public void runForOrganisationShouldSetContextForOrganisation() {
        EtlService etlService = new EtlService(organisationRepository, organisationFactory, schemaMigrationService, syncService);

        OrganisationIdentity organisationIdentity = new OrganisationIdentityBuilder().withId(1).withDbUser("a").build();
        etlService.runForOrganisation(organisationIdentity);

        assertThat(ContextHolder.getOrganisationIdentity(), is(organisationIdentity));
    }

    @Test
    public void runForOrganisationShouldCreateOrganisationAndCallEtlServiceForMigration() {
        EtlService etlService = new EtlService(organisationRepository, organisationFactory, schemaMigrationService, syncService);

        OrganisationIdentity organisationIdentity = new OrganisationIdentityBuilder().withId(1).withDbUser("a").build();
        Organisation organisation = mock(Organisation.class);
        when(organisationFactory.create(organisationIdentity)).thenReturn(organisation);
        Organisation newOrganisation = mock(Organisation.class);
        when(schemaMigrationService.migrate(organisation)).thenReturn(newOrganisation);

        etlService.runForOrganisation(organisationIdentity);

        verify(organisationFactory).create(organisationIdentity);
        verify(schemaMigrationService).migrate(organisation);
        verify(syncService).sync(newOrganisation);
    }
}
