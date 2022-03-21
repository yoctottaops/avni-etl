package org.avniproject.etl.repository;

import org.avniproject.etl.BaseIntegrationTest;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Sql("/test-data.sql")
public class SchemaMetadataRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SchemaMetadataRepository schemaMetadataRepository;

    @BeforeEach
    public void before() {
        OrganisationIdentity orgb = new OrganisationIdentity(12, "orgc", "orgc", OrganisationIdentity.OrganisationType.Organisation);
        ContextHolder.create(orgb);
    }

    @Test
    public void shouldGetAllTablesForAnOrganisation() {
        SchemaMetadata schemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();
        assertThat(schemaMetadata.getTableMetadata().size(), is(7));
    }

    @Test
    public void shouldRunStoredProcedureToCreateDbUser() {
        schemaMetadataRepository.createDBUser("newuser", "password");
    }

    @Test
    public void shouldNotFailWhenCreatingSchemaMultipleTimes() {
        schemaMetadataRepository.createDBUser("newuser", "password");
        schemaMetadataRepository.createImplementationSchema("newuser", "newuser");

        schemaMetadataRepository.createDBUser("newuser", "password");
        schemaMetadataRepository.createImplementationSchema("newuser", "newuser");
    }

    @Test
    public void shouldNotHaveAnySchemaDiffsIfSchemaMetadataHasBeenSavedOnce() {
        SchemaMetadata schemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();
        schemaMetadataRepository.save(schemaMetadata);
        List<Diff> changes = schemaMetadata.findChanges(schemaMetadataRepository.getNewSchemaMetadata());
        assertThat(changes.size(), is(0));
    }
}
