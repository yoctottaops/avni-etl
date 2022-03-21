package org.avniproject.etl.domain;

import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.domain.metadata.diff.CreateTable;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SchemaMetadataTest {

    @Test
    public void shouldCreateTableForSubjectTypeWhenItDoesNotExist() {
        TableMetadata subjectTable = new TableMetadata();
        subjectTable.setName("OperationalSubjectTypeName");
        subjectTable.setEncounterTypeId(null);
        subjectTable.setFormId(1);
        subjectTable.setProgramId(null);
        subjectTable.setType(TableMetadata.Type.Person);
        SchemaMetadata newSchema = new SchemaMetadata(Arrays.asList(subjectTable));
        SchemaMetadata currentSchema = new SchemaMetadata(new ArrayList<>());

        List<Diff> changes = newSchema.findChanges(currentSchema);

        assertThat(changes.size(), is(1));
        assertThat(changes, hasItem(instanceOf(CreateTable.class)));
    }

    @Test
    public void shouldNotCreateSubjectTableWhenItAlreadyExists() {
        TableMetadata subjectTable = new TableMetadata();
        subjectTable.setName("OperationalSubjectTypeName");
        subjectTable.setEncounterTypeId(null);
        subjectTable.setFormId(1);
        subjectTable.setProgramId(null);
        subjectTable.setType(TableMetadata.Type.Person);
        SchemaMetadata newSchema = new SchemaMetadata(Arrays.asList(subjectTable));

        List<Diff> changes = newSchema.findChanges(newSchema);

        assertThat(changes.size(), is(0));
    }
}
