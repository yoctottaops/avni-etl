package org.avniproject.etl.builder.domain.metadata;

import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;
import static java.lang.Math.round;

public class TableMetadataBuilder {
    private final Integer id = Math.toIntExact(round(random() * 1000));
    private String name = "Person";
    private TableMetadata.Type type = TableMetadata.Type.Person;
    private String subjectTypeUuid = "1";
    private String programUuid = null;
    private String encounterTypeUuid = null;
    private String formUuid = "1";
    private final List<ColumnMetadata> columnMetadataList = new ArrayList<>();

    public TableMetadataBuilder forPerson() {
        name = "Person";
        type = TableMetadata.Type.Person;
        programUuid = null;
        encounterTypeUuid = null;

        return this;
    }

    public TableMetadataBuilder forIndividual() {
        name = "Individual";
        type = TableMetadata.Type.Individual;
        programUuid = null;
        encounterTypeUuid = null;

        return this;
    }

    public TableMetadataBuilder forProgramEnrolment(String programUuid) {
        name = "MyProgram";
        type = TableMetadata.Type.ProgramEnrolment;
        this.programUuid = programUuid;
        encounterTypeUuid = null;

        return this;
    }

    public TableMetadataBuilder withColumnMetadata(ColumnMetadata columnMetadata) {
        this.columnMetadataList.add(columnMetadata);
        return this;
    }

    public TableMetadataBuilder forProgramExit(String programUuid) {
        return forProgramEnrolment(programUuid).withType(TableMetadata.Type.ProgramExit);
    }

    public TableMetadataBuilder withType(TableMetadata.Type type) {
        this.type = type;
        return this;
    }

    public TableMetadataBuilder forProgramEncounter(String programUuid, String encounterTypeUuid) {
        name = "MyProgram";
        type = TableMetadata.Type.ProgramEncounter;
        this.programUuid = programUuid;
        this.encounterTypeUuid = encounterTypeUuid;

        return this;
    }

    public TableMetadataBuilder forProgramEncounterCancellation(String programUuid, String encounterTypeUuid) {
        return forProgramEncounter(programUuid, encounterTypeUuid).withType(TableMetadata.Type.ProgramEncounterCancellation);
    }


    public TableMetadata build() {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setId(this.id);
        tableMetadata.setName(this.name);
        tableMetadata.setType(this.type);
        tableMetadata.setSubjectTypeUuid(this.subjectTypeUuid);
        tableMetadata.setProgramUuid(this.programUuid);
        tableMetadata.setEncounterTypeUuid(this.encounterTypeUuid);
        tableMetadata.setFormUuid(this.formUuid);
        tableMetadata.setColumnMetadataList(this.columnMetadataList);
        return tableMetadata;
    }

    public TableMetadataBuilder forAddress() {
        name = "Address";
        subjectTypeUuid = null;
        formUuid = null;
        return withType(TableMetadata.Type.Address);
    }
}
