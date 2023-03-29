package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.metadata.diff.Diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

public class SchemaMetadata {
    private List<TableMetadata> tableMetadata;

    public SchemaMetadata(List<TableMetadata> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    public List<TableMetadata> getTableMetadata() {
        return tableMetadata;
    }

    public void setTableMetadata(List<TableMetadata> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    public List<Diff> findChanges(SchemaMetadata currentSchema) {
        List<TableMetadata> newTables = this.getTableMetadata();
        List<Diff> diffs = new ArrayList<>();
        newTables.forEach(newTable -> {
            diffs.addAll(findChanges(currentSchema, newTable));
        });
        return diffs;
    }

    public Optional<TableMetadata> findMatchingTable(TableMetadata newTable) {
        return this.getTableMetadata().stream().filter(currentTable -> currentTable.matches(newTable)).findFirst();
    }

    private List<Diff> findChanges(SchemaMetadata currentSchema, TableMetadata newTable) {
        List<Diff> diffs = new ArrayList<>();
        Optional<TableMetadata> optionalMatchingTable = currentSchema.findMatchingTable(newTable);
        if (optionalMatchingTable.isPresent()) {
            TableMetadata matchingTable = optionalMatchingTable.get();
            diffs.addAll(newTable.findChanges(matchingTable));
        } else {
            diffs.addAll(newTable.createNew());
        }
        return diffs;
    }

    private BinaryOperator<ArrayList<Diff>> arrayListCombiner() {
        return (diffa, diffb) -> {
            ArrayList<Diff> diffs = new ArrayList<>(diffa);
            diffs.addAll(diffb);
            return diffs;
        };
    }

    public void mergeWith(SchemaMetadata oldSchemaMetadata) {
        getTableMetadata()
                .forEach(newTable ->
                        oldSchemaMetadata
                                .findMatchingTable(newTable)
                                .ifPresent(newTable::mergeWith));
    }
}
