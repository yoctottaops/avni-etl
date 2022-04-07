package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.Model;
import org.avniproject.etl.domain.metadata.diff.AddColumn;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.domain.metadata.diff.RenameTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableMetadata extends Model {
    public TableMetadata(Integer id) {
        super(id);
    }

    public TableMetadata() {
    }

    public boolean matches(TableMetadata realTable) {
        if (realTable == null) return false;
        return  equalsIgnoreNulls(realTable.getType(), this.getType())
                && equalsIgnoreNulls(realTable.getSubjectTypeId(), this.subjectTypeId)
                && equalsIgnoreNulls(realTable.getFormId(), this.formId)
                && equalsIgnoreNulls(realTable.getEncounterTypeId(), this.encounterTypeId)
                && equalsIgnoreNulls(realTable.getProgramId(), this.programId);
    }

    public List<Diff> findChanges(TableMetadata currentTable) {
        ArrayList<Diff> diffs = new ArrayList<>();
        if (!currentTable.getName().equals(getName())) {
            diffs.add(new RenameTable(currentTable.getName(), getName()));
        }

        getColumnMetadataList().forEach(columnMetadata -> {
            Optional<ColumnMetadata> matchingColumn = currentTable.findMatchingColumn(columnMetadata);
            if (!matchingColumn.isPresent()) {
                diffs.add(new AddColumn(getName(), columnMetadata.getColumn()));
            } else {
                diffs.addAll(columnMetadata.findChanges(this, matchingColumn.get()));
            }
        });
        return diffs;
    }

    private Optional<ColumnMetadata> findMatchingColumn(ColumnMetadata columnMetadata) {
        return this.columnMetadataList.stream().filter(thisColumn -> thisColumn.matches(columnMetadata)).findFirst();
    }

    public List<Column> getColumns() {
        return getColumnMetadataList()
                .stream()
                .map(columnMetadata -> columnMetadata.getColumn())
                .collect(Collectors.toList());
    }

    public void mergeWith(TableMetadata oldTableMetadata) {
        setId(oldTableMetadata.getId());
        getColumnMetadataList()
                .stream()
                .forEach(newColumn ->
                        oldTableMetadata
                                .findMatchingColumn(newColumn)
                                .ifPresent(oldColumn -> newColumn.mergeWith(oldColumn)));
    }



    public enum Type {
        Individual,
        Person,
        Household,
        Group,
        ProgramEnrolment,
        ProgramExit,
        ProgramEncounter,
        ProgramEncounterCancellation,
        Encounter,
        IndividualEncounterCancellation,
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

    public List<ColumnMetadata> getNonDefaultColumnMetadataList() {
        return getColumnMetadataList().stream().filter(columnMetadata -> columnMetadata.getConceptId() != null).collect(Collectors.toList());
    }

    public void setColumnMetadataList(List<ColumnMetadata> columnMetadataList) {
        this.columnMetadataList = columnMetadataList;
    }

    public void addColumnMetadata(List<ColumnMetadata> columnMetadataList) {
        this.columnMetadataList.addAll(columnMetadataList);
    }

    public boolean hasNonDefaultColumns() {
        return !getNonDefaultColumnMetadataList().isEmpty();
    }
}
