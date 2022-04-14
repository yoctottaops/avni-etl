package org.avniproject.etl.repository;

import org.avniproject.etl.BaseIntegrationTest;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Sql("/test-data.sql")
public class SchemaMetadataRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SchemaMetadataRepository schemaMetadataRepository;

    @BeforeEach
    public void before() {
        OrganisationIdentity orgb = new OrganisationIdentity("orgc", "orgc");
        ContextHolder.setContext(orgb);
    }

    @Test
    public void shouldGetAllTablesForAnOrganisation() {
        SchemaMetadata schemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();
        assertThat(schemaMetadata.getTableMetadata().size(), is(8));
    }

    @Test
    public void shouldGetDecisionConcepts() {
        SchemaMetadata schemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();
        TableMetadata growthMonitoringEncounterTable = schemaMetadata.getTableMetadata().stream().filter(tableMetadata1 -> tableMetadata1.getName().equals("person_nutrition_growth_monitoring")).findFirst().get();

        assertThat(growthMonitoringEncounterTable.getNonDefaultColumnMetadataList().size(), is(1));
        ColumnMetadata decisionConceptColumn = growthMonitoringEncounterTable.getNonDefaultColumnMetadataList().get(0);
        assertThat(decisionConceptColumn.getName(), is("Goat Color"));
    }

    @Test
    public void shouldGetAddressTable() {
        SchemaMetadata schemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();
        Optional<TableMetadata> addressTableOptional = schemaMetadata.getTableMetadata().stream().filter(tableMetadata1 -> tableMetadata1.getName().equals("address")).findFirst();

        assertThat(addressTableOptional.isPresent(), is(true));

        TableMetadata addressTable = addressTableOptional.get();
        assertThat(addressTable.getColumns().size(), is(10));
        assertThat(addressTable.getColumns().stream().anyMatch(column -> column.getName().equals("District")), is(true));
        assertThat(addressTable.getColumns().stream().anyMatch(column -> column.getName().equals("Block")), is(true));
        assertThat(addressTable.getColumns().stream().anyMatch(column -> column.getName().equals("Gram Panchayat")), is(true));
    }


    @Test
    public void shouldNotHaveAnySchemaDiffsIfSchemaMetadataHasBeenSavedOnce() {
        SchemaMetadata schemaMetadata = schemaMetadataRepository.getNewSchemaMetadata();
        schemaMetadataRepository.save(schemaMetadata);
        List<Diff> changes = schemaMetadata.findChanges(schemaMetadataRepository.getNewSchemaMetadata());
        assertThat(changes.size(), is(0));
    }
}
