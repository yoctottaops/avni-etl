package org.avniproject.etl.service;

import org.avniproject.etl.builder.OrganisationIdentityBuilder;
import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;
import org.avniproject.etl.repository.EntitySyncStatusRepository;
import org.avniproject.etl.repository.SchemaMetadataRepository;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class OrganisationFactoryTest {

    @Test
    public void populatesAllRequiredFieldsInOrganisation() {
        SchemaMetadataRepository schemaMetadataRepository = mock(SchemaMetadataRepository.class);
        SchemaMetadata schemaMetadata = mock(SchemaMetadata.class);
        when(schemaMetadataRepository.getExistingSchemaMetadata()).thenReturn(schemaMetadata);

        EntitySyncStatusRepository entitySyncStatusRepository = mock(EntitySyncStatusRepository.class);
        SchemaDataSyncStatus schemaDataSyncStatus = mock(SchemaDataSyncStatus.class);
        when(entitySyncStatusRepository.getSyncStatus()).thenReturn(schemaDataSyncStatus);

        OrganisationIdentity organisationIdentity = new OrganisationIdentityBuilder().build();

        Organisation organisation = new OrganisationFactory(schemaMetadataRepository, entitySyncStatusRepository).create(organisationIdentity);

        assertThat(organisation.getOrganisationIdentity(), is(organisationIdentity));
        assertThat(organisation.getSchemaMetadata(), is(schemaMetadata));
        assertThat(organisation.getSyncStatus(), is(schemaDataSyncStatus));
    }
}
