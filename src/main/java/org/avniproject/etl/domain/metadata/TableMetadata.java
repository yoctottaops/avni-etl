package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.Model;
import org.avniproject.etl.domain.metadata.diff.Diff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableMetadata extends Model {
    public TableMetadata(Integer id) {
        super(id);
    }

    public TableMetadata() {
    }

    boolean equalsIgnoreNulls(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null && b != null) return false;
        if (a != null && b == null) return false;
        return a.equals(b);
    }

    public boolean matches(TableMetadata realTable) {
        if (realTable == null) return false;
        return  equalsIgnoreNulls(realTable.getType(), this.getType())
                && equalsIgnoreNulls(realTable.getSubjectTypeId(), this.subjectTypeId)
                && equalsIgnoreNulls(realTable.getFormId(), this.formId)
                && equalsIgnoreNulls(realTable.getEncounterTypeId(), this.encounterTypeId)
                && equalsIgnoreNulls(realTable.getProgramId(), this.programId);
    }

    //todo: implement this
    public List<Diff> findChanges(TableMetadata tableMetadata) {
        return new ArrayList<>();
    }

    public List<Column> getColumns() {
        return getColumnMetadataList()
                .stream()
                .map(columnMetadata -> columnMetadata.getColumn())
                .collect(Collectors.toList());
    }

    public enum Type {
        IndividualProfile,
        Person,
        Household,
        Group,
        ProgramEnrolment,
        ProgramExit,
        ProgramEncounter,
        ProgramEncounterCancellation,
        Encounter,
        EncounterCancellation,
        Address;
    }

    private String name;
    private Type type;
    private Integer subjectTypeId;
    private Integer programId;
    private Integer encounterTypeId;
    private Integer formId;
    private List<ColumnMetadata> columnMetadataList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getSubjectTypeId() {
        return subjectTypeId;
    }

    public void setSubjectTypeId(Integer subjectTypeId) {
        this.subjectTypeId = subjectTypeId;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public Integer getEncounterTypeId() {
        return encounterTypeId;
    }

    public void setEncounterTypeId(Integer encounterTypeId) {
        this.encounterTypeId = encounterTypeId;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public List<ColumnMetadata> getColumnMetadataList() {
        return columnMetadataList;
    }

    public void setColumnMetadataList(List<ColumnMetadata> columnMetadataList) {
        this.columnMetadataList = columnMetadataList;
    }

    public void addColumnMetadata(List<ColumnMetadata> columnMetadataList) {
        this.columnMetadataList.addAll(columnMetadataList);
    }
}
