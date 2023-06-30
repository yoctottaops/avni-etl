package org.avniproject.etl.domain.metadata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableMetadataTest {
    @Test
    public void getParentTableType() {
        TableMetadata tableMetadata = new TableMetadataBuilder().withSubjectTypeUuid("a").withQuestionGroupConceptUuid("q").build();
        assertEquals(TableMetadata.TableType.IndividualProfile, tableMetadata.getParentTableType());
        tableMetadata = new TableMetadataBuilder().withSubjectTypeUuid("a").withEncounterTypeUuid("b").withQuestionGroupConceptUuid("q").build();
        assertEquals(TableMetadata.TableType.Encounter, tableMetadata.getParentTableType());
        tableMetadata = new TableMetadataBuilder().withSubjectTypeUuid("a").withProgramUuid("b").withQuestionGroupConceptUuid("q").build();
        assertEquals(TableMetadata.TableType.ProgramEnrolment, tableMetadata.getParentTableType());
        tableMetadata = new TableMetadataBuilder().withSubjectTypeUuid("a").withProgramUuid("b").withQuestionGroupConceptUuid("q").withEncounterTypeUuid("c").build();
        assertEquals(TableMetadata.TableType.ProgramEncounter, tableMetadata.getParentTableType());
    }
}
