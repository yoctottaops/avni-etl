package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.Model;
import org.avniproject.etl.domain.metadata.diff.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableMetadata extends Model {
    private String name;
    private Type type;
    private String subjectTypeUuid;
    private String programUuid;
    private String encounterTypeUuid;
    private String formUuid;
    private List<ColumnMetadata> columnMetadataList = new ArrayList<>();

    private List<IndexMetadata> indexMetadataList = new ArrayList<>();

    public TableMetadata(Integer id) {
        super(id);
    }

    public TableMetadata() {
    }

    public boolean matches(TableMetadata realTable) {
        if (realTable == null) return false;
        return equalsIgnoreNulls(realTable.getType(), this.getType())
                && equalsIgnoreNulls(realTable.getSubjectTypeUuid(), this.subjectTypeUuid)
                && equalsIgnoreNulls(realTable.getFormUuid(), this.formUuid)
                && equalsIgnoreNulls(realTable.getEncounterTypeUuid(), this.encounterTypeUuid)
                && equalsIgnoreNulls(realTable.getProgramUuid(), this.programUuid);
    }

    public List<Diff> findChanges(TableMetadata currentTable) {
        ArrayList<Diff> diffs = new ArrayList<>();
        if (!currentTable.getName().equals(getName())) {
            diffs.add(new RenameTable(currentTable.getName(), getName()));
        }

        getColumnMetadataList().forEach(columnMetadata -> {
            Optional<ColumnMetadata> matchingColumn = currentTable.findMatchingColumn(columnMetadata);
            if (matchingColumn.isEmpty()) {
                diffs.add(new AddColumn(getName(), columnMetadata.getColumn()));
            } else {
                diffs.addAll(columnMetadata.findChanges(this, matchingColumn.get()));
            }
        });

        getIndexMetadataList().forEach(indexMetadata -> {
            Optional<IndexMetadata> matchingIndex = currentTable.findMatchingIndex(indexMetadata);
            if (matchingIndex.isEmpty()) {
                diffs.add(indexMetadata.createIndex(getName()));
            }
        });

        return diffs;
    }

    private Optional<IndexMetadata> findMatchingIndex(IndexMetadata indexMetadata) {
        return this.indexMetadataList
                .stream()
                .filter(index -> index.matches(indexMetadata))
                .findFirst();
    }

    private Optional<ColumnMetadata> findMatchingColumn(ColumnMetadata columnMetadata) {
        return this.columnMetadataList
                .stream()
                .filter(thisColumn -> thisColumn.matches(columnMetadata))
                .findFirst();
    }

    public List<Column> getColumns() {
        return getColumnMetadataList()
                .stream()
                .map(ColumnMetadata::getColumn)
                .collect(Collectors.toList());
    }

    public void mergeWith(TableMetadata oldTableMetadata) {
        setId(oldTableMetadata.getId());
        getColumnMetadataList()
                .forEach(newColumn ->
                        oldTableMetadata
                                .findMatchingColumn(newColumn)
                                .ifPresent(newColumn::mergeWith));
        getIndexMetadataList()
                .forEach(newIndex ->
                        oldTableMetadata.findMatchingIndex(newIndex)
                                .ifPresent(newIndex::mergeWith)
                        );
    }

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

    public String getSubjectTypeUuid() {
        return subjectTypeUuid;
    }

    public void setSubjectTypeUuid(String subjectTypeUuid) {
        this.subjectTypeUuid = subjectTypeUuid;
    }

    public String getProgramUuid() {
        return programUuid;
    }

    public void setProgramUuid(String programUuid) {
        this.programUuid = programUuid;
    }

    public String getEncounterTypeUuid() {
        return encounterTypeUuid;
    }

    public void setEncounterTypeUuid(String encounterTypeUuid) {
        this.encounterTypeUuid = encounterTypeUuid;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public List<ColumnMetadata> getColumnMetadataList() {
        return columnMetadataList;
    }

    public List<ColumnMetadata> getIndexedColumnMetadataList() {
        return columnMetadataList.stream().filter(columnMetadata -> columnMetadata.getColumn().isIndexed()).collect(Collectors.toList());
    }

    public void setColumnMetadataList(List<ColumnMetadata> columnMetadataList) {
        this.columnMetadataList = columnMetadataList;
    }

    public List<ColumnMetadata> getNonDefaultColumnMetadataList() {
        return getColumnMetadataList().stream().filter(columnMetadata -> columnMetadata.getConceptId() != null).collect(Collectors.toList());
    }

    public void addColumnMetadata(List<ColumnMetadata> columnMetadataList) {
        this.columnMetadataList.addAll(columnMetadataList);
    }

    public boolean hasNonDefaultColumns() {
        return !getNonDefaultColumnMetadataList().isEmpty();
    }

    public List<IndexMetadata> getIndexMetadataList() {
        return indexMetadataList;
    }

    public void addIndexMetadata(Column column) {
        addIndexMetadata(new IndexMetadata(findMatchingColumn(new ColumnMetadata(column, null, null, null)).get()));
    }

    public void addIndexMetadata(List<IndexMetadata> indexMetadataList) {
        this.indexMetadataList.addAll(indexMetadataList);
    }

    public void setIndexMetadataList(List<IndexMetadata> indexMetadataList) {
        this.indexMetadataList = indexMetadataList;
    }

    public List<Diff> createNew() {
        List<Diff> diffs = new ArrayList<>();
        diffs.add(new CreateTable(name, getColumns()));
        diffs.addAll(getIndexMetadataList()
                .stream()
                .map(indexMetadata -> new AddIndex(indexMetadata.getName(), getName(), indexMetadata.getColumnName()))
                .collect(Collectors.toList()));
        return diffs;
    }

    public boolean hasColumn(String columnName) {
        return columnMetadataList.stream().anyMatch(columnMetadata -> columnMetadata.getColumn().getName().equals(columnName));
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
        Address,
        Media
    }

    public boolean isSubjectTable() {
        return Arrays.asList(Type.Individual, Type.Person, Type.Household, Type.Group).contains(this.type);
    }

    private void addIndexMetadata(IndexMetadata indexMetadata) {
        this.indexMetadataList.add(indexMetadata);
    }
}
