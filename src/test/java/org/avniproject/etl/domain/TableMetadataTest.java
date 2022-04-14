package org.avniproject.etl.domain;

import org.avniproject.etl.builder.domain.metadata.TableMetadataBuilder;
import org.avniproject.etl.domain.metadata.Column;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.domain.metadata.diff.AddColumn;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.domain.metadata.diff.RenameTable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TableMetadataTest {

    @Test
    public void matches_shouldHandleNullValues() {
        assertThat(new TableMetadata().matches(null), is(false));
    }

    @Test
    public void matches_shouldMatchCorrectlyWithSameTable() {
        assertThat(new TableMetadata().matches(new TableMetadata()), is(true));
    }

    @Test
    public void matches_shouldHandleDifferentTypesOfTables() {
        TableMetadata person = new TableMetadataBuilder().forPerson().build();
        TableMetadata programEnrolment = new TableMetadataBuilder().forProgramEnrolment(1).build();
        TableMetadata anotherProgramEnrolment = new TableMetadataBuilder().forProgramEnrolment(2).build();
        TableMetadata programExit = new TableMetadataBuilder().forProgramExit(1).build();
        TableMetadata anotherProgramExit = new TableMetadataBuilder().forProgramExit(2).build();
        TableMetadata programEncounter = new TableMetadataBuilder().forProgramEncounter(1, 1).build();
        TableMetadata programEncounterCancellation = new TableMetadataBuilder().forProgramEncounterCancellation(1, 1).build();
        TableMetadata anotherProgramProgramEncounter = new TableMetadataBuilder().forProgramEncounter(2, 1).build();
        TableMetadata anotherProgramProgramEncounterCancellation = new TableMetadataBuilder().forProgramEncounterCancellation(2, 1).build();
        TableMetadata addressTable = new TableMetadataBuilder().forAddress().build();

        List<TableMetadata> tables = Arrays.asList(person, programEnrolment, anotherProgramEnrolment,
                programExit, anotherProgramExit, programEncounter, programEncounterCancellation,
                anotherProgramProgramEncounter, anotherProgramProgramEncounterCancellation, addressTable);

        tables.forEach(firstTable -> {
            tables.forEach(secondTable -> {
                if (firstTable == secondTable) {
                    assertThat(firstTable.matches(secondTable), is(true));
                } else {
                    assertThat(firstTable.matches(secondTable), is(false));
                }
            });
        });
    }

    @Test
    public void shouldRenameTableIfNecessary() {
        TableMetadata oldTable = new TableMetadata();
        oldTable.setName("oldTable");
        TableMetadata newTable = new TableMetadata();
        newTable.setName("newTable");

        List<Diff> changes = newTable.findChanges(oldTable);
        assertThat(changes.size(), is(1));
        assertThat(changes.get(0), instanceOf(RenameTable.class));
    }

    @Test
    public void shouldAddColumnIfMissing() {
        ContextHolder.setContext(new OrganisationIdentity("dbUser", "schema"));
        TableMetadata oldTable = new TableMetadataBuilder().forPerson().build();
        TableMetadata newTable = new TableMetadataBuilder().forPerson().build();
        newTable.addColumnMetadata(List.of(new ColumnMetadata(new Column("newColumn", Column.Type.text), 24, ColumnMetadata.ConceptType.Text, UUID.randomUUID().toString())));

        List<Diff> changes = newTable.findChanges(oldTable);

        assertThat(changes.size(), is(1));
        Diff diff = changes.get(0);
        assertThat(diff, instanceOf(AddColumn.class));
        assertThat(diff.getSql(), containsString("newColumn"));
    }
}
