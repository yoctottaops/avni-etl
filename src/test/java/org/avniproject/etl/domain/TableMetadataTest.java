package org.avniproject.etl.domain;

import org.avniproject.etl.builder.domain.metadata.TableMetadataBuilder;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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



}
