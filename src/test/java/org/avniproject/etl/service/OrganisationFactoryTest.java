package org.avniproject.etl.service;

import org.avniproject.etl.builder.OrganisationIdentityBuilder;
import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.repository.EntitySyncStatusRepository;
import org.avniproject.etl.repository.SchemaMetadataRepository;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OrganisationFactoryTest {

    @Test
    public void populatesOrganisationIdentityInOrganisation() {
        SchemaMetadataRepository schemaMetadataRepository = mock(SchemaMetadataRepository.class);
        OrganisationIdentity organisationIdentity = new OrganisationIdentityBuilder().build();
        Organisation organisation = new OrganisationFactory(schemaMetadataRepository, mock(EntitySyncStatusRepository.class)).create(organisationIdentity);
        verify(schemaMetadataRepository).getExistingSchemaMetadata();
        assertThat(organisation.getOrganisationIdentity(), is(organisationIdentity));
    }
}
