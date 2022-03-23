package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.metadata.diff.CreateTable;
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

        return newTables.stream().reduce(
                new ArrayList<>(),
                (diffs, newTable) -> findChanges(diffs, currentSchema, newTable),
                arrayListCombiner());
    }

    public Optional<TableMetadata> findMatchingTable(TableMetadata newTable) {
        return this.getTableMetadata().stream().filter(currentTable -> currentTable.matches(newTable)).findFirst();
    }

    private ArrayList<Diff> findChanges(ArrayList<Diff> diffs, SchemaMetadata currentSchema, TableMetadata newTable) {
        Optional<TableMetadata> optionalMatchingTable = currentSchema.findMatchingTable(newTable);
        if (optionalMatchingTable.isPresent()) {
            TableMetadata matchingTable = optionalMatchingTable.get();
            diffs.addAll(newTable.findChanges(matchingTable));
        } else {
            diffs.add(new CreateTable(newTable.getName(), newTable.getColumns()));
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
                .stream()
                .forEach(newTable ->
                        oldSchemaMetadata
                                .findMatchingTable(newTable)
                                .ifPresent(oldTable -> newTable.mergeWith(oldTable)));
    }
}
