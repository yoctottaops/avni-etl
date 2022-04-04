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
    private Integer subjectTypeId = 1;
    private Integer programId = null;
    private Integer encounterTypeId = null;
    private Integer formId = 1;
    private final List<ColumnMetadata> columnMetadataList = new ArrayList<>();

    public TableMetadataBuilder forPerson() {
        name = "Person";
        type = TableMetadata.Type.Person;
        programId = null;
        encounterTypeId = null;

        return this;
    }

    public TableMetadataBuilder forIndividual() {
        name = "Individual";
        type = TableMetadata.Type.Individual;
        programId = null;
        encounterTypeId = null;

        return this;
    }

    public TableMetadataBuilder forProgramEnrolment(Integer programId) {
        name = "MyProgram";
        type = TableMetadata.Type.ProgramEnrolment;
        this.programId = programId;
        encounterTypeId = null;

        return this;
    }

    public TableMetadataBuilder withColumnMetadata(ColumnMetadata columnMetadata) {
        this.columnMetadataList.add(columnMetadata);
        return this;
    }

    public TableMetadataBuilder forProgramExit(Integer programId) {
        return forProgramEnrolment(programId).withType(TableMetadata.Type.ProgramExit);
    }

    public TableMetadataBuilder withType(TableMetadata.Type type) {
        this.type = type;
        return this;
    }

    public TableMetadataBuilder forProgramEncounter(Integer programId, Integer encounterTypeId) {
        name = "MyProgram";
        type = TableMetadata.Type.ProgramEncounter;
        this.programId = programId;
        this.encounterTypeId = encounterTypeId;

        return this;
    }

    public TableMetadataBuilder forProgramEncounterCancellation(Integer programId, Integer encounterTypeId) {
        return forProgramEncounter(programId, encounterTypeId).withType(TableMetadata.Type.ProgramEncounterCancellation);
    }


    public TableMetadata build() {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setId(this.id);
        tableMetadata.setName(this.name);
        tableMetadata.setType(this.type);
        tableMetadata.setSubjectTypeId(this.subjectTypeId);
        tableMetadata.setProgramId(this.programId);
        tableMetadata.setEncounterTypeId(this.encounterTypeId);
        tableMetadata.setFormId(this.formId);
        tableMetadata.setColumnMetadataList(this.columnMetadataList);
        return tableMetadata;
    }

    public TableMetadataBuilder forAddress() {
        name = "Address";
        subjectTypeId = null;
        formId = null;
        return withType(TableMetadata.Type.Address);
    }
}
